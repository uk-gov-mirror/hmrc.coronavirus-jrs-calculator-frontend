/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.{Amount, PartialPeriodBreakdown, PartialPeriodWithPaymentDate, PaymentFrequency, PeriodBreakdown, PeriodWithPaymentDate}
import utils.AmountRounding.roundWithMode
import utils.TaxYearFinder

import scala.math.BigDecimal.RoundingMode

trait NicCalculator extends TaxYearFinder with FurloughCapCalculator {

  def calculateFullPeriod(
    frequency: PaymentFrequency,
    furloughPayment: Amount,
    periodWithPaymentDate: PeriodWithPaymentDate): PeriodBreakdown = {
    val threshold = FrequencyTaxYearThresholdMapping.findThreshold(frequency, taxYearAt(periodWithPaymentDate.paymentDate), NiRate())

    val roundedFurloughPayment = furloughPayment.value.setScale(0, RoundingMode.DOWN)
    val cap = furloughCap(frequency, periodWithPaymentDate.period).setScale(0, RoundingMode.DOWN)
    val cappedFurloughPayment = cap.min(roundedFurloughPayment)

    val grant =
      if (cappedFurloughPayment < threshold) {
        BigDecimal(0).setScale(2)
      } else {
        roundWithMode((cappedFurloughPayment - threshold) * NiRate().value, RoundingMode.HALF_UP)
      }

    PeriodBreakdown(Amount(grant), periodWithPaymentDate)
  }

  def calculatePartialPeriod(
    frequency: PaymentFrequency,
    totalPay: Amount,
    partialPeriodWithPaymentDate: PartialPeriodWithPaymentDate): PartialPeriodBreakdown = {
    val threshold = FrequencyTaxYearThresholdMapping.findThreshold(frequency, taxYearAt(partialPeriodWithPaymentDate.paymentDate), NiRate())

    val roundedTotalPay = totalPay.value.setScale(0, RoundingMode.DOWN)

    val totalNic = roundWithMode((roundedTotalPay - threshold) * NiRate().value, RoundingMode.HALF_UP)
    val dailyNic = totalNic / periodDaysCount(partialPeriodWithPaymentDate.period.original)
    val furloughNic = roundWithMode(dailyNic * periodDaysCount(partialPeriodWithPaymentDate.period.partial), RoundingMode.HALF_UP)

    PartialPeriodBreakdown(Amount(furloughNic), partialPeriodWithPaymentDate)
  }

  def calculatePartialPeriodWithTopUp(
    frequency: PaymentFrequency,
    totalPay: Amount,
    furloughPayment: Amount,
    partialPeriodWithPaymentDate: PartialPeriodWithPaymentDate): PartialPeriodBreakdown = {
    val threshold = FrequencyTaxYearThresholdMapping.findThreshold(frequency, taxYearAt(partialPeriodWithPaymentDate.paymentDate), NiRate())

    val roundedTotalPay = totalPay.value.setScale(0, RoundingMode.DOWN)

    val totalNic = roundWithMode((roundedTotalPay - threshold) * NiRate().value, RoundingMode.HALF_UP)
    val dailyNic = totalNic / periodDaysCount(partialPeriodWithPaymentDate.period.original)

    val totalPayForFurloughPeriod =
      (totalPay.value / periodDaysCount(partialPeriodWithPaymentDate.period.original)) * periodDaysCount(
        partialPeriodWithPaymentDate.period.partial)

    val furloughNic = dailyNic * periodDaysCount(partialPeriodWithPaymentDate.period.partial)

    val grant = roundWithMode(furloughNic * (furloughPayment.value / totalPayForFurloughPeriod), RoundingMode.HALF_UP)

    PartialPeriodBreakdown(Amount(grant), partialPeriodWithPaymentDate)
  }

}
