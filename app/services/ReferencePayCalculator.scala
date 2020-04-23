/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import models.PayQuestion.Varies
import models.PaymentFrequency.Monthly
import models.{Amount, CylbEligibility, CylbOperators, FullPeriod, NonFurloughPay, PartialPeriod, PaymentFrequency, PaymentWithPeriod, Period, PeriodWithPaymentDate, Periods, VariableLengthEmployed}
import utils.AmountRounding._

import scala.math.BigDecimal.RoundingMode
import scala.math.BigDecimal.RoundingMode._

trait ReferencePayCalculator extends PreviousYearPeriod {

  def calculateVariablePay(
    nonFurloughPay: NonFurloughPay,
    priorFurloughPeriod: Period,
    afterFurloughPayPeriod: Seq[PeriodWithPaymentDate],
    amount: Amount,
    cylbs: Seq[Amount],
    frequency: PaymentFrequency): Seq[PaymentWithPeriod] = {
    val avg: Seq[PaymentWithPeriod] =
      afterFurloughPayPeriod.map(period => calculateAveragePay(nonFurloughPay, priorFurloughPeriod, period, amount))

    if (cylbs.isEmpty) avg
    else
      greaterGrossPay(calculateCylb(nonFurloughPay, frequency, cylbs, afterFurloughPayPeriod), avg)
  }

  def cylbCalculationPredicate(variableLength: VariableLengthEmployed, employeeStartDate: LocalDate): CylbEligibility =
    CylbEligibility(variableLength == VariableLengthEmployed.Yes || employeeStartDate.isBefore(LocalDate.of(2019, 4, 6)))

  private def calculateAveragePay(
    nonFurloughPay: NonFurloughPay,
    priorFurloughPeriod: Period,
    afterFurloughPayPeriod: PeriodWithPaymentDate,
    amount: Amount): PaymentWithPeriod = {

    val period = afterFurloughPayPeriod.period match {
      case FullPeriod(p)       => p
      case PartialPeriod(_, p) => p
    }

    val daily = periodDaysCount(period) * averageDailyCalculator(priorFurloughPeriod, amount)

    val nfp = determineNonFurloughPay(afterFurloughPayPeriod.period, nonFurloughPay)

    PaymentWithPeriod(nfp, Amount(daily), afterFurloughPayPeriod, Varies)
  }

  protected def greaterGrossPay(cylb: Seq[PaymentWithPeriod], avg: Seq[PaymentWithPeriod]): Seq[PaymentWithPeriod] =
    cylb.zip(avg) map {
      case (cylbPayment, avgPayment) =>
        if (cylbPayment.furloughPayment.value > avgPayment.furloughPayment.value)
          cylbPayment
        else avgPayment
    }

  protected def calculateCylb(
    nonFurloughPay: NonFurloughPay,
    frequency: PaymentFrequency,
    cylbs: Seq[Amount],
    periods: Seq[PeriodWithPaymentDate]): Seq[PaymentWithPeriod] =
    for {
      period <- periods.zip(cylbs.sliding(2, 1).toList)
      payOne <- period._2.headOption
      payTwo = period._2.tail.headOption.fold(Amount(0.0))(v => v)
      nfp = determineNonFurloughPay(period._1.period, nonFurloughPay)
    } yield {
      val cylbOps = operatorsEnhanced(frequency, period._1.period)
      val amount: Amount = (period._1.period, frequency, cylbOps) match {
        case (_: FullPeriod, Monthly, _)                   => totalOneToOne(payOne, payTwo, cylbOps, period._1.period)
        case (_: FullPeriod, _, _)                         => totalTwoToOne(payOne, payTwo, cylbOps)
        case (p: PartialPeriod, _, CylbOperators(_, 0, _)) => totalOneToOne(payOne, payTwo, cylbOps, period._1.period)
        case (p: PartialPeriod, _, CylbOperators(_, _, 0)) => totalOneToOne(payOne, payTwo, cylbOps, period._1.period)
        case (p: PartialPeriod, _, _)                      => totalTwoToOne(payOne, payTwo, cylbOps)
      }

      val furlough = roundAmountWithMode(amount, RoundingMode.HALF_UP)

      PaymentWithPeriod(nfp, furlough, period._1, Varies)
    }

  private def totalTwoToOne(payOne: Amount, payTwo: Amount, operator: CylbOperators): Amount = {
    import operator._
    Amount(((payOne.value / divider) * daysFromPrevious) + ((payTwo.value / divider) * daysFromCurrent))
  }

  private def totalOneToOne(payOne: Amount, payTwo: Amount, operator: CylbOperators, period: Periods): Amount = {
    import operator._
    period match {
      case _: FullPeriod                          => Amount((payOne.value / divider) * daysFromCurrent)
      case p: PartialPeriod if isFurloughStart(p) => Amount((payOne.value / divider) * operator.daysFromCurrent)
      case p: PartialPeriod if isFurloughEnd(p)   => Amount((payTwo.value / divider) * operator.daysFromPrevious)
    }
  }

  protected def averageDailyCalculator(period: Period, amount: Amount): BigDecimal = {
    val count = periodDaysCount(period)
    val res = roundWithMode(amount.value / periodDaysCount(period), HALF_UP)
    res
  }

  private def determineNonFurloughPay(period: Periods, nonFurloughPay: NonFurloughPay): Amount =
    period match {
      case _: FullPeriod => Amount(0.00)
      case pp: PartialPeriod =>
        val pre = if (isFurloughStart(pp)) nonFurloughPay.preAmount else Amount(0.00)
        val post = if (isFurloughEnd(pp)) nonFurloughPay.postAmount else Amount(0.00)
        Amount(pre.value + post.value)
    }
}
