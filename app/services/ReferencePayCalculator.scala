/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import models.PayQuestion.Varies
import models.{Amount, CylbEligibility, CylbOperators, CylbPayment, FullPeriod, NonFurloughPay, PartialPeriod, PaymentFrequency, PaymentWithPeriod, Period, PeriodWithPaymentDate, Periods, VariableLengthEmployed}
import utils.AmountRounding._

import scala.math.BigDecimal.RoundingMode
import scala.math.BigDecimal.RoundingMode._

trait ReferencePayCalculator extends PreviousYearPeriod {

  def calculateVariablePay(
    nonFurloughPay: NonFurloughPay,
    priorFurloughPeriod: Period,
    afterFurloughPayPeriod: Seq[PeriodWithPaymentDate],
    amount: Amount,
    cylbs: Seq[CylbPayment],
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
    cylbs: Seq[CylbPayment],
    periods: Seq[PeriodWithPaymentDate]): Seq[PaymentWithPeriod] =
    for {
      period <- periods
      datesRequired = previousYearPayDate(frequency, period)
      nfp = determineNonFurloughPay(period.period, nonFurloughPay)
    } yield {
      val cylbOps = operators(frequency, period.period)
      val previousPayments: Seq[CylbPayment] = datesRequired.flatMap(date => cylbs.find(_.date == date))

      val amount =
        if (previousPayments.length == 1)
          totalOneToOne(previousPayments.head.amount, cylbOps)
        else
          totalTwoToOne(previousPayments.head.amount, previousPayments.tail.head.amount, cylbOps)

      val furlough = roundAmountWithMode(amount, RoundingMode.HALF_UP)

      PaymentWithPeriod(nfp, furlough, period, Varies)
    }

  private def totalTwoToOne(payOne: Amount, payTwo: Amount, operator: CylbOperators): Amount = {
    import operator._
    Amount(((payOne.value / divider) * daysFromPrevious) + ((payTwo.value / divider) * daysFromCurrent))
  }

  private def totalOneToOne(pay: Amount, operator: CylbOperators): Amount =
    operator match {
      case CylbOperators(div, 0, multiplier) => Amount((pay.value / div) * multiplier)
      case CylbOperators(div, multiplier, 0) => Amount((pay.value / div) * multiplier)
    }

  protected def averageDailyCalculator(period: Period, amount: Amount): BigDecimal =
    roundWithMode(amount.value / periodDaysCount(period), HALF_UP)

  private def determineNonFurloughPay(period: Periods, nonFurloughPay: NonFurloughPay): Amount =
    period match {
      case _: FullPeriod => Amount(0.00)
      case pp: PartialPeriod =>
        val pre = if (isFurloughStart(pp)) nonFurloughPay.preAmount else Amount(0.00)
        val post = if (isFurloughEnd(pp)) nonFurloughPay.postAmount else Amount(0.00)
        Amount(pre.value + post.value)
    }
}
