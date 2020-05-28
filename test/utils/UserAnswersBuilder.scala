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

package utils

import base.CoreTestDataBuilder
import models.Amount._
import models.EmployeeStarted.{After1Feb2019, OnOrBefore1Feb2019}
import models.FurloughStatus.FurloughOngoing
import models.NicCategory.Payable
import models.PayMethod.Regular
import models.PensionStatus.DoesContribute
import models.TopUpStatus.ToppedUp
import models.{AdditionalPayment, AnnualPayAmount, ClaimPeriodQuestion, FurloughPartialPay, FurloughPeriodQuestion, FurloughStatus, LastYearPayment, NicCategory, PayMethod, PayPeriodQuestion, PaymentFrequency, PensionStatus, Salary, TopUpPayment, TopUpPeriod, TopUpStatus, UserAnswers}
import pages.{QuestionPage, TopUpPeriodsPage, _}
import play.api.libs.json.Writes
import queries.Settable

import scala.annotation.tailrec

trait UserAnswersBuilder extends CoreTestDataBuilder {

  implicit class UserAnswerBuilder(userAnswers: UserAnswers) {

    def withNiCategory(category: NicCategory = Payable): UserAnswers =
      userAnswers.setValue(NicCategoryPage, category)

    def withPensionStatus(status: PensionStatus = DoesContribute): UserAnswers =
      userAnswers.setValue(PensionStatusPage, status)

    def withFurloughStatus(status: FurloughStatus = FurloughOngoing): UserAnswers =
      userAnswers.setValue(FurloughStatusPage, status)

    def withFurloughStartDate(startDate: String): UserAnswers =
      userAnswers.setValue(FurloughStartDatePage, startDate.toLocalDate)

    def withFurloughEndDate(startDate: String): UserAnswers =
      userAnswers.setValue(FurloughEndDatePage, startDate.toLocalDate)

    def withEmployeeStartDate(startDate: String): UserAnswers =
      userAnswers.setValue(EmployeeStartDatePage, startDate.toLocalDate)

    def withLastPayDate(date: String): UserAnswers =
      userAnswers.setValue(LastPayDatePage, date.toLocalDate)

    def withClaimPeriodEnd(date: String): UserAnswers =
      userAnswers.setValue(ClaimPeriodEndPage, date.toLocalDate)

    def withClaimPeriodStart(date: String): UserAnswers =
      userAnswers.setValue(ClaimPeriodStartPage, date.toLocalDate)

    def withPayMethod(method: PayMethod = Regular): UserAnswers =
      userAnswers.setValue(PayMethodPage, method)

    def withPaymentFrequency(frequency: PaymentFrequency): UserAnswers =
      userAnswers.setValue(PaymentFrequencyPage, frequency)

    def withRegularPayAmount(salary: BigDecimal): UserAnswers =
      userAnswers.setValue(RegularPayAmountPage, Salary(salary))

    def withPartialPayBeforeFurlough(pay: BigDecimal): UserAnswers =
      userAnswers.setValue(PartialPayBeforeFurloughPage, FurloughPartialPay(pay))

    def withPartialPayAfterFurlough(pay: BigDecimal): UserAnswers =
      userAnswers.setValue(PartialPayAfterFurloughPage, FurloughPartialPay(pay))

    def withAnnualPayAmount(gross: BigDecimal): UserAnswers =
      userAnswers.setValue(AnnualPayAmountPage, AnnualPayAmount(gross))

    def withEmployeeStartedOnOrBefore1Feb2019(): UserAnswers =
      userAnswers.setValue(EmployeeStartedPage, OnOrBefore1Feb2019)

    def withEmployeeStartedAfter1Feb2019(): UserAnswers =
      userAnswers.setValue(EmployeeStartedPage, After1Feb2019)

    def withToppedUpStatus(status: TopUpStatus = ToppedUp): UserAnswers =
      userAnswers.setValue(TopUpStatusPage, status)

    def withAdditionalPaymentPeriods(dates: List[String]): UserAnswers =
      userAnswers.setValue(AdditionalPaymentPeriodsPage, dates.map(_.toLocalDate))

    def withAdditionalPaymentAmount(payment: AdditionalPayment, idx: Option[Int]): UserAnswers =
      userAnswers.setValue(AdditionalPaymentAmountPage, payment, idx)

    def withTopUpPeriods(periods: List[TopUpPeriod]): UserAnswers =
      userAnswers.setValue(TopUpPeriodsPage, periods)

    def withTopUpAmount(payment: TopUpPayment, idx: Option[Int]): UserAnswers =
      userAnswers.setValue(TopUpAmountPage, payment, idx)

    def withClaimPeriodQuestion(claim: ClaimPeriodQuestion): UserAnswers =
      userAnswers.setValue(ClaimPeriodQuestionPage, claim)

    def withFurloughPeriodQuestion(furlough: FurloughPeriodQuestion): UserAnswers =
      userAnswers.setValue(FurloughPeriodQuestionPage, furlough)

    def withPayPeriodQuestion(pay: PayPeriodQuestion): UserAnswers =
      userAnswers.setValue(PayPeriodQuestionPage, pay)

    def withPayDate(dates: List[String]): UserAnswers =
      withListOfValues[String](dates, PayDatePage)

    def withLastYear(payments: List[(String, Int)]): UserAnswers =
      withListOfValues[(String, Int)](payments, LastYearPayPage)

    def withListOfValues[A](payments: List[A], page: QuestionPage[_]): UserAnswers = {
      val zipped: List[(A, Int)] = payments.zip(1 to payments.length)

      @tailrec
      def rec(userAnswers: UserAnswers, payments: List[(A, Int)]): UserAnswers =
        (payments, page) match {
          case (Nil, _) => userAnswers
          case (((d: String, amount: Int), idx) :: tail, LastYearPayPage) =>
            rec(userAnswers.setValue(LastYearPayPage, LastYearPayment(d.toLocalDate, amount.toAmount), Some(idx)), tail)
          case ((d: String, idx) :: tail, PayDatePage) =>
            rec(userAnswers.setValue(PayDatePage, d.toLocalDate, Some(idx)), tail)
        }
      rec(userAnswers, zipped)
    }
  }

  private implicit class UserAnswersHelper(val userAnswers: UserAnswers) {
    def setValue[A](page: Settable[A], value: A, idx: Option[Int] = None)(implicit writes: Writes[A]): UserAnswers =
      userAnswers.set(page, value, idx).success.value
  }
}
