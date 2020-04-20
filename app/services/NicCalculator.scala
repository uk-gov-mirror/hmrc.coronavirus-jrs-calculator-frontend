/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.Calculation.NicCalculationResult
import models.{Amount, CalculationResult, FullPeriod, PartialPeriod, PaymentDate, PaymentFrequency, PeriodBreakdown, PeriodWithPaymentDate}
import utils.AmountRounding.roundWithMode

import scala.math.BigDecimal.RoundingMode

trait NicCalculator extends FurloughCapCalculator with CommonCalculationService {

  def calculateNicGrant(frequency: PaymentFrequency, furloughBreakdown: Seq[PeriodBreakdown]): CalculationResult = {
    val nicBreakdowns = furloughBreakdown.map { breakdown =>
      import breakdown._
      breakdown.periodWithPaymentDate.period match {
        case fp @ FullPeriod(_) =>
          fullPeriodCalculation(frequency, nonFurloughPay, grant, fp, periodWithPaymentDate.paymentDate, NiRate())
        case pp @ PartialPeriod(_, _) =>
          calculatePartialPeriodNic(frequency, nonFurloughPay, grant, pp, periodWithPaymentDate.paymentDate)
      }
    }
    CalculationResult(NicCalculationResult, nicBreakdowns.map(_.grant.value).sum, nicBreakdowns)
  }

  protected def calculatePartialPeriodNic(
    frequency: PaymentFrequency,
    nonFurloughPay: Amount,
    furloughPayment: Amount,
    period: PartialPeriod,
    paymentDate: PaymentDate): PeriodBreakdown = {
    val roundedTotalPay = (nonFurloughPay.value + furloughPayment.value).setScale(0, RoundingMode.DOWN)
    val threshold = FrequencyTaxYearThresholdMapping.findThreshold(frequency, taxYearAt(paymentDate), NiRate())
    val grossNi = greaterThanAllowance(roundedTotalPay, threshold, NiRate())
    val dailyNi = grossNi / periodDaysCount(period.original)
    val grant = roundWithMode(dailyNi * periodDaysCount(period.partial), RoundingMode.HALF_UP)

    PeriodBreakdown(nonFurloughPay, Amount(grant), PeriodWithPaymentDate(period, paymentDate))
  }

//  def calculatePartialPeriodWithTopUp(
//    frequency: PaymentFrequency,
//    totalPay: Amount,
//    furloughPayment: Amount,
//    PeriodWithPaymentDate: PeriodWithPaymentDate): PeriodBreakdown = {
//    val threshold = FrequencyTaxYearThresholdMapping.findThreshold(frequency, taxYearAt(partialPeriodWithPaymentDate.paymentDate), NiRate())
//
//    val roundedTotalPay = totalPay.value.setScale(0, RoundingMode.DOWN)
//
//    val totalNic = roundWithMode((roundedTotalPay - threshold) * NiRate().value, RoundingMode.HALF_UP)
//    val dailyNic = totalNic / periodDaysCount(partialPeriodWithPaymentDate.period.original)
//
//    val totalPayForFurloughPeriod =
//      (totalPay.value / periodDaysCount(partialPeriodWithPaymentDate.period.original)) * periodDaysCount(
//        partialPeriodWithPaymentDate.period.partial)
//
//    val furloughNic = dailyNic * periodDaysCount(partialPeriodWithPaymentDate.period.partial)
//
//    val grant = roundWithMode(furloughNic * (furloughPayment.value / totalPayForFurloughPeriod), RoundingMode.HALF_UP)
//
//    PartialPeriodBreakdown(Amount(grant), partialPeriodWithPaymentDate)
//  }

}
