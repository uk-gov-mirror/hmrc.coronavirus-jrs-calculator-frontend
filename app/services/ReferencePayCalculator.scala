/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import cats.data.NonEmptyList
import models.NonFurloughPay._
import models.PayMethod.Regular
import models.{Amount, CylbPayment, FullPeriodWithPaymentDate, NonFurloughPay, PartialPeriodWithPaymentDate, PaymentFrequency, PaymentWithFullPeriod, PaymentWithPartialPeriod, PaymentWithPeriod, Period, PeriodWithPaymentDate}

trait ReferencePayCalculator extends CylbCalculator with AverageCalculator with Calculators {

  def calculateRegularPay(wage: Amount, periods: Seq[PeriodWithPaymentDate]): Seq[PaymentWithPeriod] =
    periods.map {
      case fp: FullPeriodWithPaymentDate => PaymentWithFullPeriod(wage, fp, Regular)
      case pp: PartialPeriodWithPaymentDate =>
        val furloughAmount = partialPeriodDailyCalculation(wage, pp.period)
        val nonFurlough = Amount(wage.value - furloughAmount.value)
        PaymentWithPartialPeriod(nonFurlough, furloughAmount, pp, Regular)
    }

  def calculateVariablePay(
    nonFurloughPay: NonFurloughPay,
    priorFurloughPeriod: Period,
    furloughPayPeriods: Seq[PeriodWithPaymentDate],
    amount: Amount,
    cylbs: Seq[CylbPayment],
    frequency: PaymentFrequency): Seq[PaymentWithPeriod] = {
    val avg: Seq[PaymentWithPeriod] =
      furloughPayPeriods.map(period => calculateAveragePay(nonFurloughPay, priorFurloughPeriod, period, amount))

    NonEmptyList
      .fromList(cylbs.toList)
      .fold(avg)(_ =>
        takeGreaterGrossPay(calculateCylb(nonFurloughPay, frequency, cylbs, furloughPayPeriods, determineNonFurloughPay), avg))
  }

  protected def takeGreaterGrossPay(cylb: Seq[PaymentWithPeriod], avg: Seq[PaymentWithPeriod]): Seq[PaymentWithPeriod] =
    cylb.zip(avg) map {
      case (cylbPayment, avgPayment) =>
        if (cylbPayment.furloughPayment.value > avgPayment.furloughPayment.value)
          cylbPayment
        else avgPayment
    }

}
