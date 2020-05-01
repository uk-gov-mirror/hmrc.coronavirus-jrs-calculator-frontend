/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package base

import java.time.LocalDate

import models.{Amount, FullPeriod, FullPeriodWithPaymentDate, PartialPeriod, PartialPeriodWithPaymentDate, PayMethod, PaymentDate, PaymentWithFullPeriod, PaymentWithPartialPeriod, Period}

trait CoreDataBuilder {

  def period(start: String, end: String) =
    Period(buildLocalDate(periodBuilder(start)), buildLocalDate(periodBuilder(end)))

  def partialPeriod(original: (String, String), partial: (String, String)) =
    PartialPeriod(period(original._1, original._2), period(partial._1, partial._2))

  def fullPeriod(start: String, end: String) = FullPeriod(period(start, end))

  def paymentWithFullPeriod(furloughPayment: BigDecimal, period: FullPeriodWithPaymentDate, payMethod: PayMethod): PaymentWithFullPeriod =
    PaymentWithFullPeriod(Amount(furloughPayment), period, payMethod)

  def paymentWithPartialPeriod(
    nonFurloughPay: BigDecimal,
    furloughPayment: BigDecimal,
    period: PartialPeriodWithPaymentDate,
    payMethod: PayMethod): PaymentWithPartialPeriod =
    PaymentWithPartialPeriod(Amount(nonFurloughPay), Amount(furloughPayment), period, payMethod)

  def fullPeriodWithPaymentDate(start: String, end: String, paymentDate: String): FullPeriodWithPaymentDate =
    FullPeriodWithPaymentDate(FullPeriod(period(start, end)), PaymentDate(buildLocalDate(periodBuilder(paymentDate))))

  def partialPeriodWithPaymentDate(
    start: String,
    end: String,
    pstart: String,
    pend: String,
    paymentDate: String): PartialPeriodWithPaymentDate =
    PartialPeriodWithPaymentDate(
      PartialPeriod(period(start, end), period(pstart, pend)),
      PaymentDate(buildLocalDate(periodBuilder(paymentDate))))

  def paymentDate(date: String): PaymentDate = PaymentDate(buildLocalDate(periodBuilder(date)))

  private val periodBuilder: String => Array[Int] =
    date => date.replace(" ", "").replace("-", ",").split(",").map(_.toInt)

  private val buildLocalDate: Array[Int] => LocalDate = array => LocalDate.of(array(0), array(1), array(2))
}
