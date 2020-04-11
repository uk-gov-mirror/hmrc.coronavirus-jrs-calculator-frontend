/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.{FourWeekly, FurloughPayment, Monthly, PaymentFrequency, TaxYear, TaxYearEnding2020, TaxYearEnding2021}
import utils.TaxYearFinder

trait NicCalculatorService extends TaxYearFinder {

  def calculateNic(paymentFrequency: PaymentFrequency, furloughPayment: FurloughPayment): Double = {
    val frequencyTaxYearKey = FrequencyTaxYearKey(paymentFrequency, taxYearAt(furloughPayment.payPeriod))
    val cappedFurloughPayment = calculateCappedFurloughPayment(furloughPayment)
    val threshold = calculateThreshold(frequencyTaxYearKey)

    if (cappedFurloughPayment < threshold) 0
    else BigDecimal(((cappedFurloughPayment - threshold) * 0.138)).setScale(2, BigDecimal.RoundingMode.DOWN).toDouble

  }

  private def calculateThreshold(key: FrequencyTaxYearKey): Double = FrequencyTaxYearMapping.mappings(key)

  private def calculateCappedFurloughPayment(furloughPayment: FurloughPayment): Double =
    if (furloughPayment.amount > 2500.00) 2500.00 else furloughPayment.amount

}

case class FrequencyTaxYearKey(paymentFrequency: PaymentFrequency, taxYear: TaxYear)

object FrequencyTaxYearMapping {
  val mappings: Map[FrequencyTaxYearKey, Double] = Map(
    FrequencyTaxYearKey(Monthly, TaxYearEnding2020)    -> 719.00,
    FrequencyTaxYearKey(Monthly, TaxYearEnding2021)    -> 732.00,
    FrequencyTaxYearKey(FourWeekly, TaxYearEnding2020) -> 664.00
  )
}
