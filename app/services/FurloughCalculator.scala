/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.{FurloughPayment, PaymentFrequency, RegularPayment}
import play.api.Logger

case class Salary(value: Double)

trait FurloughCalculator {

  def calculateMultiple(paymentFrequency: PaymentFrequency, regularPayments: List[RegularPayment]): List[FurloughPayment] =
    regularPayments.map(payment => FurloughPayment(calculate(paymentFrequency, payment.salary), payment.payPeriod.paymentDate))

  protected def calculate(paymentFrequency: PaymentFrequency, salary: Salary): Double = {
    val eigthy = salary.value * 0.8
    FurloughCapMapping.mappings
      .get(paymentFrequency)
      .fold {
        Logger.warn(s"Unable to find a rate for $paymentFrequency")
        0.00
      } { cap =>
        calculation(eigthy, cap)
      }
  }

  private def calculation(eigthy: Double, cap: FurloughCap): Double =
    if (eigthy > cap.value) cap.value else eigthy
}
