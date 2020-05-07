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

import models.FurloughStatus.FurloughOngoing
import models.NicCategory.Payable
import models.PayMethod.Regular
import models.PaymentFrequency.Monthly
import models.{Amount, FullPeriod, FullPeriodWithPaymentDate, FurloughWithinClaim, PartialPeriod, PartialPeriodWithPaymentDate, PaymentDate, PaymentWithFullPeriod, PaymentWithPartialPeriod, PensionStatus, Period, ReferencePayData, UserAnswers}
import org.scalatest.TryValues
import pages._
import play.api.libs.json.Writes
import queries.Settable

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

  private val periodBuilder: String => Array[Int] =
    date => date.replace(" ", "").replace("-", ",").split(",").map(_.toInt)

  private val buildLocalDate: Array[Int] => LocalDate = array => LocalDate.of(array(0), array(1), array(2))

  private val claimPeriod: Period = period("2020-3-1", "2020-3-31")

  implicit class UserAnswersHelper(val userAnswers: UserAnswers) {
    def setValue[A](page: Settable[A], value: A, idx: Option[Int] = None)(implicit writes: Writes[A]) =
      userAnswers.set(page, value, idx).success.value
  }

  val defaultReferencePayData =
    ReferencePayData(FurloughWithinClaim(claimPeriod), Seq(fullPeriodWithPaymentDate("2020-3-1", "2020-3-31", "2020-3-31")), Monthly)

  val mandatoryAnswers = UserAnswers("id")
    .setValue(ClaimPeriodStartPage, LocalDate.of(2020, 3, 1))
    .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 3, 31))
    .setValue(PaymentFrequencyPage, Monthly)
    .setValue(NicCategoryPage, Payable)
    .setValue(PensionStatusPage, PensionStatus.DoesContribute)
    .setValue(PayMethodPage, Regular)
    .setValue(FurloughStatusPage, FurloughOngoing)
    .setValue(FurloughStartDatePage, LocalDate.of(2020, 3, 1))
    .setValue(LastPayDatePage, LocalDate.of(2020, 3, 31))
    .setValue(PayDatePage, LocalDate.of(2020, 2, 29), Some(1))
    .setValue(PayDatePage, LocalDate.of(2020, 3, 31), Some(2))
}
