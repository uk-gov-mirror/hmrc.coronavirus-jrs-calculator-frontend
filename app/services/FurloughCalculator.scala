/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.{FurloughPayment, PayPeriodWithPayDay, PaymentDate, PaymentFrequency, RegularPayment}

import scala.math.BigDecimal.RoundingMode

trait FurloughCalculator extends FurloughCapCalculator {

  def calculateMultiple(paymentFrequency: PaymentFrequency, regularPayments: List[RegularPayment]): List[FurloughPayment] =
    regularPayments.map(payment =>
      FurloughPayment(calculate(paymentFrequency, payment), PayPeriodWithPayDay(payment.payPeriod, PaymentDate(payment.payPeriod.end))))

  protected def calculate(paymentFrequency: PaymentFrequency, regularPayment: RegularPayment): Double = {
    val eighty = helper(regularPayment.salary.amount * 0.8, RoundingMode.HALF_UP)
    val cap = furloughCap(paymentFrequency, regularPayment.payPeriod)

    calculation(eighty, cap)
  }

  private def calculation(eighty: Double, cap: Double): Double =
    if (eighty > cap) cap else eighty
}
