/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.{Amount, FullPeriod, PaymentDate, PaymentFrequency, PeriodBreakdown, PeriodWithPaymentDate}
import utils.AmountRounding.roundWithMode
import utils.TaxYearFinder

import scala.math.BigDecimal.RoundingMode

trait CommonCalculationService extends TaxYearFinder {

  def fullPeriodCalculation(
    frequency: PaymentFrequency,
    grossPay: Amount,
    furloughPayment: Amount,
    period: FullPeriod,
    paymentDate: PaymentDate,
    rate: Rate): PeriodBreakdown = {

    val threshold = thresholdFinder(frequency, paymentDate, rate)
    val roundedFurloughPayment = furloughPayment.value.setScale(0, RoundingMode.DOWN)
    val grant = greaterThanAllowance(roundedFurloughPayment, threshold, rate)

    PeriodBreakdown(grossPay, Amount(grant), PeriodWithPaymentDate(period, paymentDate))
  }

  protected def greaterThanAllowance(amount: BigDecimal, threshold: BigDecimal, rate: Rate): BigDecimal =
    if (amount < threshold) BigDecimal(0).setScale(2)
    else roundWithMode((amount - threshold) * rate.value, RoundingMode.HALF_UP)

  private def thresholdFinder(frequency: PaymentFrequency, paymentDate: PaymentDate, rate: Rate): BigDecimal =
    FrequencyTaxYearThresholdMapping.findThreshold(frequency, taxYearAt(paymentDate), rate)

}
