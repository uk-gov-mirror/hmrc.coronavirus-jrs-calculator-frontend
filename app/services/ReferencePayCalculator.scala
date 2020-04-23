/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import models.PayQuestion.Varies
import models.PaymentFrequency.{Monthly, OperatorKey, _}
import models.{Amount, CylbEligibility, Divider, FullPeriod, Multiplier, NonFurloughPay, PartialPeriod, PaymentFrequency, PaymentWithPeriod, Period, PeriodWithPaymentDate, Periods, VariableLengthEmployed}
import utils.AmountRounding._

import scala.math.BigDecimal.RoundingMode
import scala.math.BigDecimal.RoundingMode._

trait ReferencePayCalculator extends PeriodHelper {

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

    val daily = periodDaysCount(afterFurloughPayPeriod.period.period) * averageDailyCalculator(priorFurloughPeriod, amount)

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
    frequency match {
      case Monthly => monthlyCylb(nonFurloughPay, cylbs, periods)
      case _       => nonMonthlyCylb(nonFurloughPay, frequency, cylbs, periods)
    }

  private def nonMonthlyCylb(
    nonFurloughPay: NonFurloughPay,
    frequency: PaymentFrequency,
    cylbs: Seq[Amount],
    periods: Seq[PeriodWithPaymentDate]) =
    for {
      period        <- periods.zip(cylbs.sliding(2, 1).toList)
      firstPayment  <- period._2.headOption
      secondPayment <- period._2.tail.headOption
      nfp = determineNonFurloughPay(period._1.period, nonFurloughPay)
    } yield buildPayWithPeriod(frequency, period, nfp, firstPayment, secondPayment)

  private def buildPayWithPeriod(
    frequency: PaymentFrequency,
    period: (PeriodWithPaymentDate, Seq[Amount]),
    nfp: Amount,
    head: Amount,
    taildHead: Amount) = {
    val divisor = cylbOperator(frequency    -> Divider)
    val multiplier = cylbOperator(frequency -> Multiplier)
    val amount =
      roundAmountWithMode(totalFromBothWeeks(head, taildHead, divisor, multiplier), RoundingMode.HALF_UP)
    PaymentWithPeriod(nfp, amount, period._1, Varies)
  }

  private def totalFromBothWeeks(head: Amount, taildHead: Amount, divisor: Int, multiplier: Int): Amount =
    Amount(((head.value / divisor) * 2) + ((taildHead.value / divisor) * multiplier))

  private def monthlyCylb(nonFurloughPay: NonFurloughPay, cylbs: Seq[Amount], periods: Seq[PeriodWithPaymentDate]) =
    for {
      period <- periods
      amount <- cylbs
      nfp = determineNonFurloughPay(period.period, nonFurloughPay)
    } yield PaymentWithPeriod(nfp, amount, period, Varies)

  protected def averageDailyCalculator(period: Period, amount: Amount): BigDecimal =
    roundWithMode(amount.value / periodDaysCount(period), HALF_UP)

  private def cylbOperator(key: OperatorKey): Int =
    operators(key)

  private def determineNonFurloughPay(period: Periods, nonFurloughPay: NonFurloughPay): Amount =
    period match {
      case _: FullPeriod => Amount(0.00)
      case pp: PartialPeriod =>
        val pre = if (isFurloughStart(pp)) nonFurloughPay.preAmount else Amount(0.00)
        val post = if (isFurloughEnd(pp)) nonFurloughPay.postAmount else Amount(0.00)
        Amount(pre.value + post.value)
    }
}
