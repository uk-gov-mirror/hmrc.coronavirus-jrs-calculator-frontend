/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.{CalculationResult, FurloughPayment, PaymentDateBreakdown, PaymentFrequency}
import play.api.Logger
import utils.TaxYearFinder

trait CalculatorService extends TaxYearFinder {

  def calculateResult(paymentFrequency: PaymentFrequency, furloughPayment: List[FurloughPayment], rate: Rate): CalculationResult = {
    val paymentDateBreakdowns: Seq[PaymentDateBreakdown] =
      furloughPayment.map(payment => PaymentDateBreakdown(calculate(paymentFrequency, payment, rate), payment.paymentDate))

    CalculationResult(paymentDateBreakdowns.map(_.amount).sum, paymentDateBreakdowns)
  }

  protected def calculate(paymentFrequency: PaymentFrequency, furloughPayment: FurloughPayment, rate: Rate): Double = {
    val frequencyTaxYearKey = FrequencyTaxYearKey(paymentFrequency, taxYearAt(furloughPayment.paymentDate), rate)

    FrequencyTaxYearThresholdMapping.mappings
      .get(frequencyTaxYearKey)
      .fold {
        Logger.warn(s"Unable to find a rate for $frequencyTaxYearKey")
        0.00
      } { threshold =>
        val cappedFurloughPayment =
          if (furloughPayment.amount > threshold.upper) threshold.upper else furloughPayment.amount

        if (cappedFurloughPayment < threshold.lower) 0.00
        else
          BigDecimal(((cappedFurloughPayment - threshold.lower) * frequencyTaxYearKey.rate.value))
            .setScale(2, BigDecimal.RoundingMode.HALF_UP)
            .toDouble
      }
  }
}
