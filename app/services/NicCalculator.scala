/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.Calculation.NicCalculationResult
import models.{Amount, CalculationResult, FullPeriod, PartialPeriod, PaymentDate, PaymentFrequency, PeriodBreakdown, PeriodWithPaymentDate}
import utils.AmountRounding.roundWithMode
import utils.TaxYearFinder

import scala.math.BigDecimal.RoundingMode

trait NicCalculator extends TaxYearFinder with FurloughCapCalculator {

  def calculateNicGrant(frequency: PaymentFrequency, furloughBreakdown: Seq[PeriodBreakdown]): CalculationResult = {
    val nicBreakdowns = furloughBreakdown.map { breakdown =>
      breakdown.periodWithPaymentDate.period match {
        case fp @ FullPeriod(_) =>
          calculateFullPeriodNic(frequency, breakdown.nonFurloughPay, breakdown.grant, fp, breakdown.periodWithPaymentDate.paymentDate)
        case pp @ PartialPeriod(_, _) =>
          calculatePartialPeriodNic(frequency, breakdown.nonFurloughPay, breakdown.grant, pp, breakdown.periodWithPaymentDate.paymentDate)
      }
    }

    CalculationResult(NicCalculationResult, nicBreakdowns.map(_.grant.value).sum, nicBreakdowns)
  }

  protected def calculateFullPeriodNic(
    frequency: PaymentFrequency,
    grossPay: Amount,
    furloughPayment: Amount,
    period: FullPeriod,
    paymentDate: PaymentDate): PeriodBreakdown = {
    val threshold = FrequencyTaxYearThresholdMapping.findThreshold(frequency, taxYearAt(paymentDate), NiRate())

    val roundedFurloughPayment = furloughPayment.value.setScale(0, RoundingMode.DOWN)

    val grant =
      if (roundedFurloughPayment < threshold) {
        BigDecimal(0).setScale(2)
      } else {
        roundWithMode((roundedFurloughPayment - threshold) * NiRate().value, RoundingMode.HALF_UP)
      }

    PeriodBreakdown(grossPay, Amount(grant), PeriodWithPaymentDate(period, paymentDate))
  }

  protected def calculatePartialPeriodNic(
    frequency: PaymentFrequency,
    nonFurloughPay: Amount,
    furloughPayment: Amount,
    period: PartialPeriod,
    paymentDate: PaymentDate): PeriodBreakdown = {
    val roundedTotalPay = (nonFurloughPay.value + furloughPayment.value).setScale(0, RoundingMode.DOWN)
    val threshold = FrequencyTaxYearThresholdMapping.findThreshold(frequency, taxYearAt(paymentDate), NiRate())

    val grant = if (roundedTotalPay < threshold) {
      BigDecimal(0).setScale(2)
    } else {
      val grossNi = roundWithMode((roundedTotalPay - threshold) * NiRate().value, RoundingMode.HALF_UP)
      val dailyNi = grossNi / periodDaysCount(period.original)
      roundWithMode(dailyNi * periodDaysCount(period.partial), RoundingMode.HALF_UP)
    }

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
