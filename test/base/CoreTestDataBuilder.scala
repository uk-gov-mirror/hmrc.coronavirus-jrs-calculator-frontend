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
import models._
import org.scalatest.TryValues
import services.Threshold

trait CoreTestDataBuilder extends TryValues {

  def period(start: String, end: String) =
    Period(start.toLocalDate, end.toLocalDate)

  def partialPeriod(original: (String, String), partial: (String, String)) =
    PartialPeriod(period(original._1, original._2), period(partial._1, partial._2))

  def fullPeriod(start: String, end: String) = FullPeriod(period(start, end))

  def regularPaymentWithFullPeriod(regularPay: BigDecimal, referencePay: BigDecimal, period: FullPeriodWithPaymentDate) =
    RegularPaymentWithFullPeriod(Amount(regularPay), Amount(referencePay), period)

  def regularPaymentWithPartialPeriod(
    nonFurloughPay: BigDecimal,
    regularPay: BigDecimal,
    referencePay: BigDecimal,
    period: PartialPeriodWithPaymentDate) =
    RegularPaymentWithPartialPeriod(Amount(nonFurloughPay), Amount(regularPay), Amount(referencePay), period)

  def averagePaymentWithFullPeriod(
    referencePay: BigDecimal,
    period: FullPeriodWithPaymentDate,
    annualPay: BigDecimal,
    priorFurloughPeriod: Period) =
    AveragePaymentWithFullPeriod(Amount(referencePay), period, Amount(annualPay), priorFurloughPeriod)

  def averagePaymentWithPartialPeriod(
    nonFurloughPay: BigDecimal,
    referencePay: BigDecimal,
    period: PartialPeriodWithPaymentDate,
    annualPay: BigDecimal,
    priorFurloughPeriod: Period) =
    AveragePaymentWithPartialPeriod(Amount(nonFurloughPay), Amount(referencePay), period, Amount(annualPay), priorFurloughPeriod)

  def cylbPaymentWithFullPeriod(
    referencePay: BigDecimal,
    period: FullPeriodWithPaymentDate,
    averagePayment: AveragePayment,
    cylbBreakdown: CylbBreakdown) =
    CylbPaymentWithFullPeriod(Amount(referencePay), period, averagePayment, cylbBreakdown)

  def cylbPaymentWithPartialPeriod(
    nonFurloughPay: BigDecimal,
    referencePay: BigDecimal,
    period: PartialPeriodWithPaymentDate,
    averagePayment: AveragePayment,
    cylbBreakdown: CylbBreakdown) =
    CylbPaymentWithPartialPeriod(Amount(nonFurloughPay), Amount(referencePay), period, averagePayment, cylbBreakdown)

  def fullPeriodWithPaymentDate(start: String, end: String, paymentDate: String): FullPeriodWithPaymentDate =
    FullPeriodWithPaymentDate(FullPeriod(period(start, end)), PaymentDate(paymentDate.toLocalDate))

  def partialPeriodWithPaymentDate(
    start: String,
    end: String,
    pstart: String,
    pend: String,
    paymentDate: String): PartialPeriodWithPaymentDate =
    PartialPeriodWithPaymentDate(PartialPeriod(period(start, end), period(pstart, pend)), PaymentDate(paymentDate.toLocalDate))

  def fullPeriodFurloughBreakdown(grant: BigDecimal, payment: PaymentWithFullPeriod, cap: FurloughCap): FullPeriodFurloughBreakdown =
    FullPeriodFurloughBreakdown(
      Amount(grant),
      payment,
      cap
    )

  def partialPeriodFurloughBreakdown(
    grant: BigDecimal,
    payment: PaymentWithPartialPeriod,
    cap: FurloughCap): PartialPeriodFurloughBreakdown =
    PartialPeriodFurloughBreakdown(
      Amount(grant),
      payment,
      cap
    )

  def fullPeriodNicBreakdown(
    grant: BigDecimal,
    topUp: BigDecimal,
    additional: BigDecimal,
    payment: PaymentWithFullPeriod,
    threshold: Threshold,
    nicCap: NicCap): FullPeriodNicBreakdown =
    FullPeriodNicBreakdown(
      Amount(grant),
      Amount(topUp),
      Amount(additional),
      payment,
      threshold,
      nicCap
    )

  def partialPeriodNicBreakdown(
    grant: BigDecimal,
    topUp: BigDecimal,
    additional: BigDecimal,
    payment: PaymentWithPartialPeriod,
    threshold: Threshold,
    nicCap: NicCap): PartialPeriodNicBreakdown =
    PartialPeriodNicBreakdown(
      Amount(grant),
      Amount(topUp),
      Amount(additional),
      payment,
      threshold,
      nicCap
    )

  def fullPeriodPensionBreakdown(
    grant: BigDecimal,
    payment: PaymentWithFullPeriod,
    threshold: Threshold,
    allowance: BigDecimal): FullPeriodPensionBreakdown =
    FullPeriodPensionBreakdown(
      Amount(grant),
      payment,
      threshold,
      Amount(allowance)
    )

  def partialPeriodPensionBreakdown(
    grant: BigDecimal,
    payment: PaymentWithPartialPeriod,
    threshold: Threshold,
    allowance: BigDecimal): PartialPeriodPensionBreakdown =
    PartialPeriodPensionBreakdown(
      Amount(grant),
      payment,
      threshold,
      Amount(allowance)
    )

  def paymentDate(date: String): PaymentDate = PaymentDate(date.toLocalDate)

  val periodBuilder: String => Array[Int] =
    date => date.replace(" ", "").replace("-", ",").split(",").map(_.toInt)

  val buildLocalDate: Array[Int] => LocalDate = array => LocalDate.of(array(0), array(1), array(2))

  private val claimPeriod: Period = period("2020-3-1", "2020-3-31")

  val defaultReferencePayData =
    ReferencePayData(FurloughWithinClaim(claimPeriod), Seq(fullPeriodWithPaymentDate("2020-3-1", "2020-3-31", "2020-3-31")), Monthly)

  implicit class ToLocalDate(date: String) {
    def toLocalDate = buildLocalDate(periodBuilder(date))
  }
}
