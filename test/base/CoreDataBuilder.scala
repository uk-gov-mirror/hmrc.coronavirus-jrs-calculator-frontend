/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package base

import java.time.LocalDate

import models.{Amount, FullPeriod, FullPeriodWithPaymentDate, PartialPeriod, PartialPeriodWithPaymentDate, PayQuestion, PaymentDate, PaymentWithPeriod, Period, PeriodWithPaymentDate}

trait CoreDataBuilder {

  def period(start: String, end: String) =
    Period(buildLocalDate(periodBuilder(start)), buildLocalDate(periodBuilder(end)))

  def partialPeriod(original: (String, String), partial: (String, String)) =
    PartialPeriod(period(original._1, original._2), period(partial._1, partial._2))

  def fullPeriod(start: String, end: String) = FullPeriod(period(start, end))

  def paymentWithPeriod(
    nonFurloughPay: BigDecimal,
    furloughPayment: BigDecimal,
    period: PeriodWithPaymentDate,
    payQuestion: PayQuestion): PaymentWithPeriod =
    PaymentWithPeriod(Amount(nonFurloughPay), Amount(furloughPayment), period, payQuestion)

  def fullPeriodWithPaymentDate(start: String, end: String, paymentDate: String): PeriodWithPaymentDate =
    FullPeriodWithPaymentDate(FullPeriod(period(start, end)), PaymentDate(buildLocalDate(periodBuilder(paymentDate))))

  def partialPeriodWithPaymentDate(start: String, end: String, pstart: String, pend: String, paymentDate: String): PeriodWithPaymentDate =
    PartialPeriodWithPaymentDate(
      PartialPeriod(period(start, end), period(pstart, pend)),
      PaymentDate(buildLocalDate(periodBuilder(paymentDate))))

  def paymentDate(date: String): PaymentDate = PaymentDate(buildLocalDate(periodBuilder(date)))

  private val periodBuilder: String => Array[Int] =
    date => date.replace(" ", "").replace("-", ",").split(",").map(_.toInt)

  private val buildLocalDate: Array[Int] => LocalDate = array => LocalDate.of(array(0), array(1), array(2))
}
