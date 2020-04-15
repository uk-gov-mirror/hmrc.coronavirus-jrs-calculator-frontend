/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import models.Calculation.FurloughCalculationResult
import models.{CalculationResult, PayPeriodBreakdown, PayPeriodWithPayDay, PaymentDate, PaymentFrequency, RegularPayment}
import utils.TaxYearFinder

import scala.math.BigDecimal.RoundingMode

trait FurloughCalculator extends FurloughCapCalculator with TaxYearFinder {

  def calculateFurlough(
    paymentFrequency: PaymentFrequency,
    regularPayments: Seq[RegularPayment],
    taxYearPayDate: LocalDate): CalculationResult = {
    val paymentDateBreakdowns = regularPayments.map { payment =>
      val paymentDate = containsNewTaxYear(payment.payPeriod) match {
        case true  => PaymentDate(taxYearPayDate)
        case false => PaymentDate(payment.payPeriod.end)
      }

      PayPeriodBreakdown(calculate(paymentFrequency, payment), PayPeriodWithPayDay(payment.payPeriod, paymentDate))
    }

    CalculationResult(FurloughCalculationResult, paymentDateBreakdowns.map(_.amount).sum, paymentDateBreakdowns)
  }

  protected def calculate(paymentFrequency: PaymentFrequency, regularPayment: RegularPayment): Double = {
    val eighty = helper(regularPayment.salary.amount * 0.8, RoundingMode.HALF_UP)
    val cap = furloughCap(paymentFrequency, regularPayment.payPeriod)

    if (eighty > cap) cap else eighty
  }

}
