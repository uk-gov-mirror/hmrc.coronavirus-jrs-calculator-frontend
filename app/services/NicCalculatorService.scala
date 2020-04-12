/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.{FurloughPayment, NicCalculationResult, PaymentDateBreakdown, PaymentFrequency}
import utils.TaxYearFinder

trait NicCalculatorService extends TaxYearFinder {

  def calculateNics(
    paymentFrequency: PaymentFrequency,
    furloughPayment: List[FurloughPayment]): NicCalculationResult = {
    val periodBreakdowns: Seq[PaymentDateBreakdown] =
      furloughPayment.map(payment => PaymentDateBreakdown(calculateNic(paymentFrequency, payment), payment.paymentDate))

    NicCalculationResult(periodBreakdowns.map(_.amount).sum, periodBreakdowns)
  }

  protected def calculateNic(paymentFrequency: PaymentFrequency, furloughPayment: FurloughPayment): Double = {
    val frequencyTaxYearKey = FrequencyTaxYearKey(paymentFrequency, taxYearAt(furloughPayment.paymentDate))

    FrequencyTaxYearThresholdMapping.mappings.get(frequencyTaxYearKey).fold(0.00) { threshold =>
      val cappedFurloughPayment =
        if (furloughPayment.amount > threshold.upper) threshold.upper else furloughPayment.amount

      if (cappedFurloughPayment < threshold.lower) 0.00
      else
        BigDecimal(((cappedFurloughPayment - threshold.lower) * 0.138))
          .setScale(2, BigDecimal.RoundingMode.HALF_UP)
          .toDouble
    }
  }
}
