/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.Calculation.FurloughCalculationResult
import models.PayQuestion.{Regularly, Varies}
import models.{Amount, CalculationResult, FullPeriod, PartialPeriod, PaymentDate, PaymentFrequency, PaymentWithPeriod, PeriodBreakdown, PeriodWithPaymentDate}
import utils.TaxYearFinder

trait FurloughCalculator extends FurloughCapCalculator with TaxYearFinder with Calculators {

  def calculateFurloughGrant(paymentFrequency: PaymentFrequency, payments: Seq[PaymentWithPeriod]): CalculationResult = {
    val paymentDateBreakdowns = payPeriodBreakdownFromRegularPayment(paymentFrequency, payments)
    CalculationResult(FurloughCalculationResult, paymentDateBreakdowns.map(_.grant.value).sum, paymentDateBreakdowns)
  }

  protected def payPeriodBreakdownFromRegularPayment(
    paymentFrequency: PaymentFrequency,
    paymentsWithPeriod: Seq[PaymentWithPeriod]): Seq[PeriodBreakdown] =
    paymentsWithPeriod.map { payment =>
      payment.period.period match {
        case fp: FullPeriod =>
          PeriodBreakdown(
            Amount(0.0),
            calculateFullPeriod(paymentFrequency, payment, fp),
            PeriodWithPaymentDate(fp, payment.period.paymentDate))
        case pp: PartialPeriod =>
          calculatePartialPeriod(payment, pp, payment.period.paymentDate)
      }
    }

  protected def proRatePay(paymentWithPeriod: PaymentWithPeriod): Amount =
    (paymentWithPeriod.period.period, paymentWithPeriod.payQuestion) match {
      case (PartialPeriod(o, p), Regularly) =>
        partialPeriodDailyCalculation(paymentWithPeriod.furloughPayment, o, p)
      case _ => paymentWithPeriod.furloughPayment
    }

  protected def calculateFullPeriod(
    paymentFrequency: PaymentFrequency,
    payment: PaymentWithPeriod,
    period: FullPeriod,
  ): Amount = {
    val payForPeriod = proRatePay(payment)
    val cap = furloughCap(paymentFrequency, period.period)

    claimableAmount(payForPeriod, cap).halfUp
  }

  protected def calculatePartialPeriod(payment: PaymentWithPeriod, period: PartialPeriod, paymentDate: PaymentDate): PeriodBreakdown = {
    val payForPeriod = proRatePay(payment)
    val cap = partialFurloughCap(period.partial)

    val fullPeriodDays = periodDaysCount(period.original)
    val furloughDays = periodDaysCount(period.partial)
    val preFurloughDays = fullPeriodDays - furloughDays
    val nonFurloughPay = payment.payQuestion match {
      case Regularly => dailyCalculation(payment.furloughPayment, fullPeriodDays, preFurloughDays)
      case Varies    => payment.nonFurloughPay
    }

    PeriodBreakdown(nonFurloughPay, claimableAmount(payForPeriod, cap).halfUp, PeriodWithPaymentDate(period, paymentDate))
  }

}
