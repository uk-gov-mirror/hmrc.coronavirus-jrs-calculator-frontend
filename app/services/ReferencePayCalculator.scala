/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import cats.data.NonEmptyList
import models.NonFurloughPay._
import models.{Amount, CylbPayment, NonFurloughPay, PaymentFrequency, PaymentWithPeriod, Period, PeriodWithPaymentDate}

trait ReferencePayCalculator extends CylbCalculator with AverageCalculator {

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
