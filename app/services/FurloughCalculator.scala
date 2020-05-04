/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.Calculation.FurloughCalculationResult
import models.{Amount, CalculationResult, FullPeriodBreakdown, PartialPeriodBreakdown, PartialPeriodWithPaymentDate, PaymentFrequency, PaymentWithFullPeriod, PaymentWithPartialPeriod, PaymentWithPeriod, PeriodBreakdown}
import services.Calculators._
import utils.TaxYearFinder

trait FurloughCalculator extends FurloughCapCalculator with TaxYearFinder with Calculators {

  def calculateFurloughGrant(paymentFrequency: PaymentFrequency, payments: Seq[PaymentWithPeriod]): CalculationResult = {
    val paymentDateBreakdowns = payPeriodBreakdownFromRegularPayment(paymentFrequency, payments)
    CalculationResult(FurloughCalculationResult, paymentDateBreakdowns.map(_.grant.value).sum, paymentDateBreakdowns)
  }

  protected def payPeriodBreakdownFromRegularPayment(
    paymentFrequency: PaymentFrequency,
    paymentsWithPeriod: Seq[PaymentWithPeriod]): Seq[PeriodBreakdown] =
    paymentsWithPeriod.map {
      case fp: PaymentWithFullPeriod =>
        FullPeriodBreakdown(calculateFullPeriod(paymentFrequency, fp), fp.periodWithPaymentDate)
      case pp: PaymentWithPartialPeriod =>
        calculatePartialPeriod(pp)
    }

  protected def calculateFullPeriod(
    paymentFrequency: PaymentFrequency,
    payment: PaymentWithFullPeriod,
  ): Amount = {
    val cap = furloughCap(paymentFrequency, payment.periodWithPaymentDate.period.period)

    claimableAmount(payment.furloughPayment, cap).halfUp
  }

  protected def calculatePartialPeriod(payment: PaymentWithPartialPeriod): PartialPeriodBreakdown = {
    import payment.periodWithPaymentDate._
    val cap = partialFurloughCap(period.partial)

    PartialPeriodBreakdown(
      payment.nonFurloughPay,
      claimableAmount(payment.furloughPayment, cap).halfUp,
      PartialPeriodWithPaymentDate(period, paymentDate))
  }

}
