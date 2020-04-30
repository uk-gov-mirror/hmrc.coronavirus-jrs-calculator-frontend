/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.Calculation.FurloughCalculationResult
import models.PayQuestion.{Regularly, Varies}
import models.{Amount, CalculationResult, FullPeriodBreakdown, PartialPeriodBreakdown, PartialPeriodWithPaymentDate, PaymentFrequency, PaymentWithFullPeriod, PaymentWithPartialPeriod, PaymentWithPeriod, PeriodBreakdown}
import utils.TaxYearFinder
import Calculators._

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
        FullPeriodBreakdown(calculateFullPeriod(paymentFrequency, fp), fp.period)
      case pp: PaymentWithPartialPeriod =>
        calculatePartialPeriod(pp)
    }

  protected def proRatePay(paymentWithPeriod: PaymentWithPartialPeriod): Amount = paymentWithPeriod.question match {
    case Regularly => partialPeriodDailyCalculation(paymentWithPeriod.furloughPayment, paymentWithPeriod.period.period)
    case Varies    => paymentWithPeriod.furloughPayment
  }

  protected def calculateFullPeriod(
    paymentFrequency: PaymentFrequency,
    payment: PaymentWithFullPeriod,
  ): Amount = {
    val cap = furloughCap(paymentFrequency, payment.period.period.period) //TODO obvious

    claimableAmount(payment.furloughPayment, cap).halfUp
  }

  protected def calculatePartialPeriod(paymentWithPeriod: PaymentWithPartialPeriod): PartialPeriodBreakdown = {
    import paymentWithPeriod.period._
    val payForPeriod = proRatePay(paymentWithPeriod)
    val cap = partialFurloughCap(period.partial)

    val fullPeriodDays = periodDaysCount(period.original)
    val furloughDays = periodDaysCount(period.partial)
    val preFurloughDays = fullPeriodDays - furloughDays
    val nonFurloughPay = paymentWithPeriod.question match {
      case Regularly => dailyCalculation(paymentWithPeriod.furloughPayment, fullPeriodDays, preFurloughDays)
      case Varies    => paymentWithPeriod.nonFurloughPay
    }

    PartialPeriodBreakdown(nonFurloughPay, claimableAmount(payForPeriod, cap).halfUp, PartialPeriodWithPaymentDate(period, paymentDate))
  }

}
