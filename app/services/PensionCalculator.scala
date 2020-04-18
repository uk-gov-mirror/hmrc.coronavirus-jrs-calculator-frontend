/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.Calculation.PensionCalculationResult
import models.{Amount, CalculationResult, FullPeriod, PartialPeriod, PaymentDate, PaymentFrequency, PeriodBreakdown, PeriodWithPaymentDate}
import utils.AmountRounding._
import utils.TaxYearFinder

import scala.math.BigDecimal.RoundingMode

trait PensionCalculator extends TaxYearFinder with FurloughCapCalculator {

  def calculatePensionGrant(frequency: PaymentFrequency, furloughBreakdown: Seq[PeriodBreakdown]): CalculationResult = {
    val pensionBreakdowns = furloughBreakdown.map { breakdown =>
      breakdown.periodWithPaymentDate.period match {
        case fp @ FullPeriod(_) =>
          calculateFullPeriodPension(frequency, breakdown.grossPay, breakdown.grant, fp, breakdown.periodWithPaymentDate.paymentDate)
        case pp @ PartialPeriod(_, _) =>
          calculatePartialPeriodPension(frequency, breakdown.grossPay, breakdown.grant, pp, breakdown.periodWithPaymentDate.paymentDate)
      }
    }

    CalculationResult(PensionCalculationResult, pensionBreakdowns.map(_.grant.value).sum, pensionBreakdowns)
  }

  protected def calculateFullPeriodPension(
    frequency: PaymentFrequency,
    grossPay: Amount,
    furloughPayment: Amount,
    period: FullPeriod,
    paymentDate: PaymentDate): PeriodBreakdown = {
    val threshold = FrequencyTaxYearThresholdMapping.findThreshold(frequency, taxYearAt(paymentDate), PensionRate())

    val roundedFurloughPayment = furloughPayment.value.setScale(0, RoundingMode.DOWN)

    val grant =
      if (roundedFurloughPayment < threshold) {
        BigDecimal(0).setScale(2)
      } else {
        roundWithMode((roundedFurloughPayment - threshold) * PensionRate().value, RoundingMode.HALF_UP)
      }

    PeriodBreakdown(grossPay, Amount(grant), PeriodWithPaymentDate(period, paymentDate))
  }

  private def calculatePartialPeriodPension(
    frequency: PaymentFrequency,
    grossPay: Amount,
    furloughPayment: Amount,
    period: PartialPeriod,
    paymentDate: PaymentDate): PeriodBreakdown = {
    val fullPeriodDays = periodDaysCount(period.original)
    val furloughDays = periodDaysCount(period.partial)
    val threshold = FrequencyTaxYearThresholdMapping.findThreshold(frequency, taxYearAt(paymentDate), PensionRate())

    val allowance = roundWithMode((threshold / fullPeriodDays) * furloughDays, RoundingMode.HALF_UP)

    val roundedFurloughPayment = furloughPayment.value.setScale(0, RoundingMode.DOWN)

    val grant =
      if (roundedFurloughPayment < allowance) {
        BigDecimal(0).setScale(2)
      } else {
        roundWithMode((roundedFurloughPayment - allowance) * PensionRate().value, RoundingMode.HALF_UP)
      }

    PeriodBreakdown(grossPay, Amount(grant), PeriodWithPaymentDate(period, paymentDate))
  }

}
