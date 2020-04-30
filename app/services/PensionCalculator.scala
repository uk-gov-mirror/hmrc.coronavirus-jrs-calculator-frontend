/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.Calculation.PensionCalculationResult
import models.{Amount, CalculationResult, FullPeriodBreakdown, PartialPeriodBreakdown, PartialPeriodWithPaymentDate, PaymentFrequency, PeriodBreakdown}
import utils.AmountRounding._

import scala.math.BigDecimal.RoundingMode

trait PensionCalculator extends FurloughCapCalculator with CommonCalculationService {

  def calculatePensionGrant(frequency: PaymentFrequency, furloughBreakdown: Seq[PeriodBreakdown]): CalculationResult = {
    val pensionBreakdowns = furloughBreakdown.map {
      case FullPeriodBreakdown(grant, period) =>
        fullPeriodCalculation(frequency, grant, period.period, period.paymentDate, PensionRate())
      case PartialPeriodBreakdown(nonFurlough, grant, periodWithPaymentDate) =>
        calculatePartialPeriodPension(frequency, nonFurlough, grant, periodWithPaymentDate)
    }

    CalculationResult(PensionCalculationResult, pensionBreakdowns.map(_.grant.value).sum, pensionBreakdowns)
  }

  private def calculatePartialPeriodPension(
    frequency: PaymentFrequency,
    grossPay: Amount,
    furloughPayment: Amount,
    period: PartialPeriodWithPaymentDate): PartialPeriodBreakdown = {
    val fullPeriodDays = periodDaysCount(period.period.original)
    val furloughDays = periodDaysCount(period.period.partial)
    val threshold = FrequencyTaxYearThresholdMapping.findThreshold(frequency, taxYearAt(period.paymentDate), PensionRate())

    val allowance = roundWithMode((threshold / fullPeriodDays) * furloughDays, RoundingMode.HALF_UP)
    val roundedFurloughPayment = furloughPayment.value.setScale(0, RoundingMode.DOWN)
    val grant = greaterThanAllowance(roundedFurloughPayment, allowance, PensionRate())

    PartialPeriodBreakdown(grossPay, Amount(grant), PartialPeriodWithPaymentDate(period.period, period.paymentDate))
  }

}
