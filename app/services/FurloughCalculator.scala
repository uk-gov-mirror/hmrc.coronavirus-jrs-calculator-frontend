/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import models.Calculation.FurloughCalculationResult
import models.{Amount, CalculationResult, PaymentDate, PaymentFrequency, PaymentWithPeriod, Period, PeriodBreakdown, PeriodWithPaymentDate}
import utils.AmountRounding._
import utils.TaxYearFinder

import scala.math.BigDecimal.RoundingMode

trait FurloughCalculator extends FurloughCapCalculator with TaxYearFinder with PeriodHelper {

  def calculateFurlough(
    paymentFrequency: PaymentFrequency,
    regularPayments: Seq[PaymentWithPeriod],
    Period: Period,
    taxYearPayDate: LocalDate): CalculationResult = {
    val paymentDateBreakdowns = payPeriodBreakdownFromRegularPayment(paymentFrequency, regularPayments, Period, taxYearPayDate)
    CalculationResult(FurloughCalculationResult, paymentDateBreakdowns.map(_.payment.value).sum, paymentDateBreakdowns)
  }

  protected def payPeriodBreakdownFromRegularPayment(
    paymentFrequency: PaymentFrequency,
    paymentsWithPeriod: Seq[PaymentWithPeriod],
    furloughPeriod: Period,
    taxYearPayDate: LocalDate): Seq[PeriodBreakdown] =
    paymentsWithPeriod.map { payment =>
      val paymentDate = periodContainsNewTaxYear(payment.period) match {
        case true  => PaymentDate(taxYearPayDate)
        case false => PaymentDate(payment.period.end)
      }

      val furloughPayPeriod = payPeriodFromFurloughPeriod(furloughPeriod, payment.period)

      val isPartialPeriod = periodDaysCount(furloughPayPeriod) != periodDaysCount(payment.period)

      if (isPartialPeriod) {
        val partialPayment = regularPaymentForFurloughPeriod(furloughPeriod, payment)
        calculatePartialPeriod(partialPayment, PeriodWithPaymentDate(partialPayment.period, paymentDate))
      } else {
        calculateFullPeriod(paymentFrequency, payment, PeriodWithPaymentDate(payment.period, paymentDate))
      }
    }

  protected def regularPaymentForFurloughPeriod(furloughPeriod: Period, payment: PaymentWithPeriod): PaymentWithPeriod = {
    val furloughPayPeriod: Period = payPeriodFromFurloughPeriod(furloughPeriod, payment.period)
    val daysInPayPeriod = periodDaysCount(payment.period)
    val daysInFurloughDayPeriod = periodDaysCount(furloughPayPeriod)
    val daily = roundWithMode(payment.amount.value / daysInPayPeriod, RoundingMode.HALF_UP)

    val amount = if (daysInPayPeriod != daysInFurloughDayPeriod) {
      Amount(roundWithMode(daily * daysInFurloughDayPeriod, RoundingMode.HALF_UP))
    } else {
      payment.amount
    }

    PaymentWithPeriod(amount, Period(furloughPayPeriod.start, furloughPayPeriod.end))
  }

  protected def payPeriodFromFurloughPeriod(furloughPeriod: Period, payPeriod: Period) = {
    val start =
      if (furloughPeriod.start.isAfter(payPeriod.start) && furloughPeriod.start.isBefore(payPeriod.end)) {
        furloughPeriod.start
      } else {
        payPeriod.start
      }

    val end =
      if (furloughPeriod.end.isAfter(payPeriod.start) && furloughPeriod.end.isBefore(payPeriod.end)) {
        furloughPeriod.end
      } else {
        payPeriod.end
      }

    Period(start, end)
  }

  protected def calculateFullPeriod(
    paymentFrequency: PaymentFrequency,
    payment: PaymentWithPeriod,
    periodWithPaymentDate: PeriodWithPaymentDate): PeriodBreakdown = {
    val eighty = roundWithMode(payment.amount.value * 0.8, RoundingMode.HALF_UP)
    val cap = furloughCap(paymentFrequency, payment.period)

    val amount: BigDecimal = if (eighty > cap) cap else eighty

    PeriodBreakdown(Amount(amount), periodWithPaymentDate)
  }

  protected def calculatePartialPeriod(payment: PaymentWithPeriod, periodWithPaymentDate: PeriodWithPaymentDate): PeriodBreakdown = {
    val eighty = roundWithMode(payment.amount.value * 0.8, RoundingMode.HALF_UP)
    val cap = partialFurloughCap(payment.period)

    if (eighty > cap) cap else eighty

    val amount: BigDecimal = if (eighty > cap) cap else eighty

    PeriodBreakdown(Amount(amount), periodWithPaymentDate)
  }

}
