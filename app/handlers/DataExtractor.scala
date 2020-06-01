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

package handlers

import java.time.LocalDate

import models.UserAnswers.AnswerV
import models.{AdditionalPayment, Amount, BranchingQuestions, FurloughStatus, LastYearPayment, NicCategory, NonFurloughPay, PayMethod, PayPeriodQuestion, PaymentFrequency, PensionStatus, Period, ReferencePayData, TopUpPayment, UserAnswers}
import pages._
import services.{FurloughPeriodExtractor, PeriodHelper}
import cats.syntax.apply._
import cats.syntax.validated._
import play.api.libs.json.JsError
import cats.syntax.semigroupk._

trait DataExtractor extends FurloughPeriodExtractor with PeriodHelper {

  @deprecated("Use validated API instead", "1.0.0")
  def extractPriorFurloughPeriod(userAnswers: UserAnswers): Option[Period] =
    for {
      furloughStart <- userAnswers.get(FurloughStartDatePage)
      employeeStartDate = userAnswers.get(EmployeeStartDatePage).getOrElse(LocalDate.of(2019, 4, 6))
    } yield endDateOrTaxYearEnd(Period(employeeStartDate, furloughStart.minusDays(1)))

  def extractPriorFurloughPeriodV(userAnswers: UserAnswers): AnswerV[Period] = {
    val default = LocalDate.of(2019, 4, 6)

    (
      userAnswers.getV(FurloughStartDatePage),
      userAnswers.getV(EmployeeStartDatePage) <+> default.validNec[JsError]
    ).mapN { (furloughStart, employeeStartDate) =>
      endDateOrTaxYearEnd(Period(employeeStartDate, furloughStart.minusDays(1)))
    }
  }

  @deprecated("Use validated API instead", "1.0.0")
  def extractNonFurlough(userAnswers: UserAnswers): NonFurloughPay = {
    val preFurloughPay = userAnswers.get(PartialPayBeforeFurloughPage)
    val postFurloughPay = userAnswers.get(PartialPayAfterFurloughPage)

    NonFurloughPay(preFurloughPay.map(v => Amount(v.value)), postFurloughPay.map(v => Amount(v.value)))
  }

  def extractNonFurloughV(userAnswers: UserAnswers): AnswerV[NonFurloughPay] = {
    val preFurloughPay = userAnswers.getV(PartialPayBeforeFurloughPage).toOption
    val postFurloughPay = userAnswers.getV(PartialPayAfterFurloughPage).toOption

    NonFurloughPay(
      preFurloughPay.map(v => Amount(v.value)),
      postFurloughPay.map(v => Amount(v.value))
    ).validNec
  }

  @deprecated("Use validated API instead", "1.0.0")
  def extractBranchingQuestions(userAnswers: UserAnswers): Option[BranchingQuestions] =
    for {
      payMethod <- extractPayMethod(userAnswers)
      employeeStarted = userAnswers.get(EmployeeStartedPage)
      employeeStartDate = userAnswers.get(EmployeeStartDatePage)
    } yield BranchingQuestions(payMethod, employeeStarted, employeeStartDate)

  def extractBranchingQuestionsV(userAnswers: UserAnswers): AnswerV[BranchingQuestions] =
    extractPayMethodV(userAnswers).map {
      BranchingQuestions(
        _,
        userAnswers.getV(EmployeeStartedPage).toOption,
        userAnswers.getV(EmployeeStartDatePage).toOption
      )
    }

  @deprecated("Use validated API instead", "1.0.0")
  def extractPayMethod(userAnswers: UserAnswers): Option[PayMethod] =
    userAnswers.get(PayMethodPage)

  def extractPayMethodV(userAnswers: UserAnswers): AnswerV[PayMethod] =
    userAnswers.getV(PayMethodPage)

  @deprecated("Use validated API instead", "1.0.0")
  def extractSalary(userAnswers: UserAnswers): Option[Amount] =
    userAnswers.get(RegularPayAmountPage).map(v => Amount(v.amount))

  def extractSalaryV(userAnswers: UserAnswers): AnswerV[Amount] =
    userAnswers.getV(RegularPayAmountPage).map(v => Amount(v.amount))

  @deprecated("Use validated API instead", "1.0.0")
  def extractAnnualPayAmount(userAnswers: UserAnswers): Option[Amount] =
    userAnswers.get(AnnualPayAmountPage).map(v => Amount(v.amount))

  def extractAnnualPayAmountV(userAnswers: UserAnswers): AnswerV[Amount] =
    userAnswers.getV(AnnualPayAmountPage).map(v => Amount(v.amount))

  def extractCylbPayments(userAnswers: UserAnswers): Seq[LastYearPayment] =
    userAnswers.getList(LastYearPayPage)

  @deprecated("Use validated API instead", "1.0.0")
  def extractNicCategory(userAnswers: UserAnswers): Option[NicCategory] =
    userAnswers.get(NicCategoryPage)

  def extractNicCategoryV(userAnswers: UserAnswers): AnswerV[NicCategory] =
    userAnswers.getV(NicCategoryPage)

  @deprecated("Use validated API instead", "1.0.0")
  def extractPensionStatus(userAnswers: UserAnswers): Option[PensionStatus] =
    userAnswers.get(PensionStatusPage)

  def extractPensionStatusV(userAnswers: UserAnswers): AnswerV[PensionStatus] =
    userAnswers.getV(PensionStatusPage)

  @deprecated("Use validated API instead", "1.0.0")
  def extractClaimPeriodStart(userAnswers: UserAnswers): Option[LocalDate] =
    userAnswers.get(ClaimPeriodStartPage)

  def extractClaimPeriodStartV(userAnswers: UserAnswers): AnswerV[LocalDate] =
    userAnswers.getV(ClaimPeriodStartPage)

  @deprecated("Use validated API instead", "1.0.0")
  def extractClaimPeriodEnd(userAnswers: UserAnswers): Option[LocalDate] =
    userAnswers.get(ClaimPeriodEndPage)

  def extractClaimPeriodEndV(userAnswers: UserAnswers): AnswerV[LocalDate] =
    userAnswers.getV(ClaimPeriodEndPage)

  @deprecated("Use validated API instead", "1.0.0")
  def extractPaymentFrequency(userAnswers: UserAnswers): Option[PaymentFrequency] =
    userAnswers.get(PaymentFrequencyPage)

  def extractPaymentFrequencyV(userAnswers: UserAnswers): AnswerV[PaymentFrequency] =
    userAnswers.getV(PaymentFrequencyPage)

  def extractTopUpPayment(userAnswers: UserAnswers): Seq[TopUpPayment] =
    userAnswers.getList(TopUpAmountPage)

  def extractAdditionalPayment(userAnswers: UserAnswers): Seq[AdditionalPayment] =
    userAnswers.getList(AdditionalPaymentAmountPage)

  @deprecated("Use validated API instead", "1.0.0")
  def extractPayPeriodQuestion(userAnswers: UserAnswers): Option[PayPeriodQuestion] =
    userAnswers.get(PayPeriodQuestionPage)

  @deprecated("Use validated API instead", "1.0.0")
  def extractLastPayDate(userAnswers: UserAnswers): Option[LocalDate] =
    userAnswers.get(LastPayDatePage)

  def extractLastPayDateV(userAnswers: UserAnswers): AnswerV[LocalDate] =
    userAnswers.getV(LastPayDatePage)

  @deprecated("Use validated API instead", "1.0.0")
  def extractFurloughStatus(userAnswers: UserAnswers): Option[FurloughStatus] =
    userAnswers.get(FurloughStatusPage)

  def extractFurloughStatusV(userAnswers: UserAnswers): AnswerV[FurloughStatus] =
    userAnswers.getV(FurloughStatusPage)

  @deprecated("Use validated API instead", "1.0.0")
  def extractReferencePayData(userAnswers: UserAnswers): Option[ReferencePayData] =
    for {
      furloughPeriod <- extractFurloughWithinClaim(userAnswers)
      payDates = userAnswers.getList(PayDatePage)
      periods = generatePeriodsWithFurlough(payDates, furloughPeriod)
      frequency  <- extractPaymentFrequency(userAnswers)
      lastPayDay <- userAnswers.get(LastPayDatePage)
      assigned = assignPayDates(frequency, periods, lastPayDay)
    } yield ReferencePayData(furloughPeriod, assigned, frequency)

  def extractReferencePayDataV(userAnswers: UserAnswers): AnswerV[ReferencePayData] =
    (
      extractFurloughWithinClaimV(userAnswers),
      extractPaymentFrequencyV(userAnswers),
      userAnswers.getV(LastPayDatePage)
    ).mapN { (furloughPeriod, frequency, lastPayDay) =>
      val payDates = userAnswers.getList(PayDatePage)
      val periods = generatePeriodsWithFurlough(payDates, furloughPeriod)
      val assigned = assignPayDates(frequency, periods, lastPayDay)
      ReferencePayData(furloughPeriod, assigned, frequency)
    }
}
