/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import models.Calculation.FurloughCalculationResult
import models.{Amount, CalculationResult, FullPeriod, PartialPeriod, PaymentDate, PaymentFrequency, PaymentWithPeriod, Period, PeriodBreakdown, PeriodWithPaymentDate}
import utils.AmountRounding._
import utils.TaxYearFinder

import scala.math.BigDecimal.RoundingMode

trait FurloughCalculator extends FurloughCapCalculator with TaxYearFinder with PeriodHelper {

  def calculateFurloughGrant(
    paymentFrequency: PaymentFrequency,
    regularPayments: Seq[PaymentWithPeriod],
    furloughPeriod: Period,
    taxYearPayDate: LocalDate): CalculationResult = {
    val paymentDateBreakdowns = payPeriodBreakdownFromRegularPayment(paymentFrequency, regularPayments, furloughPeriod, taxYearPayDate)
    CalculationResult(FurloughCalculationResult, paymentDateBreakdowns.map(_.grant.value).sum, paymentDateBreakdowns)
  }

  protected def payPeriodBreakdownFromRegularPayment(
    paymentFrequency: PaymentFrequency,
    paymentsWithPeriod: Seq[PaymentWithPeriod],
    furloughPeriod: Period,
    taxYearPayDate: LocalDate): Seq[PeriodBreakdown] =
    paymentsWithPeriod.map { payment =>
      payment.period match {
        case fp @ FullPeriod(p) if periodContainsNewTaxYear(p) =>
          calculatePeriodBreakdown(paymentFrequency, payment.amount, PeriodWithPaymentDate(fp, PaymentDate(taxYearPayDate)))
        case pp @ PartialPeriod(o, _) if periodContainsNewTaxYear(o) =>
          calculatePeriodBreakdown(paymentFrequency, proRatePay(payment), PeriodWithPaymentDate(pp, PaymentDate(taxYearPayDate)))
        case fp @ FullPeriod(p) =>
          calculatePeriodBreakdown(paymentFrequency, payment.amount, PeriodWithPaymentDate(fp, PaymentDate(p.end)))
        case pp @ PartialPeriod(o, _) =>
          calculatePeriodBreakdown(paymentFrequency, proRatePay(payment), PeriodWithPaymentDate(pp, PaymentDate(o.end)))
      }
    }

  protected def proRatePay(paymentWithPeriod: PaymentWithPeriod): Amount =
    paymentWithPeriod.period match {
      case FullPeriod(_) => paymentWithPeriod.amount
      case PartialPeriod(o, p) => {
        val proRatedPay = roundWithMode((paymentWithPeriod.amount.value / periodDaysCount(o)) * periodDaysCount(p), RoundingMode.HALF_UP)
        Amount(proRatedPay)
      }
    }

  protected def calculatePeriodBreakdown(
    paymentFrequency: PaymentFrequency,
    payment: Amount,
    periodWithPaymentDate: PeriodWithPaymentDate): PeriodBreakdown = {
    val eighty = roundWithMode(payment.value * 0.8, RoundingMode.HALF_UP)
    val cap = periodWithPaymentDate.period match {
      case FullPeriod(p)       => furloughCap(paymentFrequency, p)
      case PartialPeriod(_, p) => partialFurloughCap(p)
    }

    val amount: BigDecimal = if (eighty > cap) cap else eighty

    PeriodBreakdown(payment, Amount(amount), periodWithPaymentDate)
  }

}
