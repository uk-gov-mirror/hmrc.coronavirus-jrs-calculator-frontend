/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package base

import java.time.LocalDate

import models.PaymentFrequency.Monthly
import models.{Amount, FullPeriod, FullPeriodWithPaymentDate, FurloughWithinClaim, PartialPeriod, PartialPeriodWithPaymentDate, PaymentDate, PaymentWithFullPeriod, PaymentWithPartialPeriod, Period, ReferencePayData}
import org.scalatest.TryValues

trait CoreTestDataBuilder extends TryValues {

  def period(start: String, end: String) =
    Period(buildLocalDate(periodBuilder(start)), buildLocalDate(periodBuilder(end)))

  def partialPeriod(original: (String, String), partial: (String, String)) =
    PartialPeriod(period(original._1, original._2), period(partial._1, partial._2))

  def fullPeriod(start: String, end: String) = FullPeriod(period(start, end))

  def paymentWithFullPeriod(furloughPayment: BigDecimal, period: FullPeriodWithPaymentDate): PaymentWithFullPeriod =
    PaymentWithFullPeriod(Amount(furloughPayment), period)

  def paymentWithPartialPeriod(
    nonFurloughPay: BigDecimal,
    furloughPayment: BigDecimal,
    period: PartialPeriodWithPaymentDate): PaymentWithPartialPeriod =
    PaymentWithPartialPeriod(Amount(nonFurloughPay), Amount(furloughPayment), period)

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

  val periodBuilder: String => Array[Int] =
    date => date.replace(" ", "").replace("-", ",").split(",").map(_.toInt)

  val buildLocalDate: Array[Int] => LocalDate = array => LocalDate.of(array(0), array(1), array(2))

  private val claimPeriod: Period = period("2020-3-1", "2020-3-31")

  val defaultReferencePayData =
    ReferencePayData(FurloughWithinClaim(claimPeriod), Seq(fullPeriodWithPaymentDate("2020-3-1", "2020-3-31", "2020-3-31")), Monthly)
}
