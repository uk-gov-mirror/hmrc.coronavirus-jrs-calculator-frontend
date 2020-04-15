/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.{CalculationResult, PayPeriodBreakdown, PaymentFrequency}
import play.api.Logger
import utils.TaxYearFinder

trait NicPensionCalculator extends TaxYearFinder with FurloughCapCalculator {

  def calculateGrant(paymentFrequency: PaymentFrequency, furloughPayment: Seq[PayPeriodBreakdown], rate: Rate): CalculationResult = {
    val paymentDateBreakdowns: Seq[PayPeriodBreakdown] =
      furloughPayment.map(payment => PayPeriodBreakdown(calculate(paymentFrequency, payment, rate), payment.payPeriodWithPayDay))

    CalculationResult(paymentDateBreakdowns.map(_.amount).sum, paymentDateBreakdowns)
  }

  protected def calculate(paymentFrequency: PaymentFrequency, furloughPayment: PayPeriodBreakdown, rate: Rate): Double = {
    val frequencyTaxYearKey = FrequencyTaxYearKey(paymentFrequency, taxYearAt(furloughPayment.payPeriodWithPayDay.paymentDate), rate)

    FrequencyTaxYearThresholdMapping.mappings
      .get(frequencyTaxYearKey)
      .fold {
        Logger.warn(s"Unable to find a threshold for $frequencyTaxYearKey")
        0.00
      } { threshold =>
        val cap = furloughCap(paymentFrequency, furloughPayment.payPeriodWithPayDay.payPeriod).floor //Remove the pennies
        val cappedFurloughPayment = cap.min(furloughPayment.amount)

        if (cappedFurloughPayment < threshold.lower) 0.00
        else
          BigDecimal(((cappedFurloughPayment - threshold.lower) * frequencyTaxYearKey.rate.value))
            .setScale(2, BigDecimal.RoundingMode.HALF_UP)
            .toDouble
      }
  }
}
