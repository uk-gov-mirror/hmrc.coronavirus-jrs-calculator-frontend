/*
 * Copyright 2021 HM Revenue & Customs
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

import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import models.UserAnswers.AnswerV
import models._
import pages._
import services.{FurloughPeriodExtractor, PeriodHelper}
import utils.LocalDateHelpers._

import java.time.LocalDate

trait DataExtractor extends FurloughPeriodExtractor with PeriodHelper {

  def extractPriorFurloughPeriodV(userAnswers: UserAnswers): AnswerV[Period] = {
    val default = LocalDate.of(2019, 4, 6)

    (
      userAnswers.getV(FirstFurloughDatePage) <+> userAnswers.getV(FurloughStartDatePage),
      userAnswers.getV(EmployeeStartDatePage) <+> default.validNec[AnswerValidation],
      userAnswers.getV(ClaimPeriodStartPage)
    ).mapN { (furloughStart, employeeStartDate, claimStart) =>
      val isRtiSubmissionRequired = rtiSubmissionRequired(claimStart, employeeStartDate)
      val rtiSubmission           = userAnswers.getV(EmployeeRTISubmissionPage)
      val empStartDateToConsiderForCalc = if (isRtiSubmissionRequired && rtiSubmission.exists(_ == EmployeeRTISubmission.No)) {
        apr6th2020
      } else {
        employeeStartDate
      }
      endDateOrTaxYearEnd(Period(empStartDateToConsiderForCalc, furloughStart.minusDays(1)), claimStart)
    }
  }

  private def rtiSubmissionRequired(claimStart: LocalDate, employeeStartDate: LocalDate) =
    if (claimStart.isEqualOrAfter(nov1st2020) && employeeStartDate.isEqualOrAfter(feb1st2020) && employeeStartDate.isEqualOrBefore(
          mar19th2020)) {
      true
    } else {
      false
    }

  def extractNonFurloughV(userAnswers: UserAnswers): AnswerV[NonFurloughPay] = {
    val preFurloughPay  = userAnswers.getV(PartialPayBeforeFurloughPage).toOption
    val postFurloughPay = userAnswers.getV(PartialPayAfterFurloughPage).toOption

    NonFurloughPay(
      preFurloughPay.map(v => Amount(v.value)),
      postFurloughPay.map(v => Amount(v.value))
    ).validNec
  }

  def extractBranchingQuestionsV(userAnswers: UserAnswers): AnswerV[BranchingQuestions] =
    extractPayMethodV(userAnswers).map {
      BranchingQuestions(
        _,
        userAnswers.getV(EmployeeStartedPage).toOption,
        userAnswers.getV(EmployeeStartDatePage).toOption
      )
    }

  def extractPayMethodV(userAnswers: UserAnswers): AnswerV[PayMethod] =
    userAnswers.getV(PayMethodPage)

  def extractSalaryV(userAnswers: UserAnswers): AnswerV[Amount] =
    userAnswers.getV(RegularPayAmountPage).map(v => Amount(v.amount))

  def extractAnnualPayAmountV(userAnswers: UserAnswers): AnswerV[Amount] =
    userAnswers.getV(AnnualPayAmountPage).map(v => Amount(v.amount))

  def extractCylbPayments(userAnswers: UserAnswers): Seq[LastYearPayment] =
    userAnswers.getList(LastYearPayPage)

  def extractNicCategoryV(userAnswers: UserAnswers): AnswerV[NicCategory] =
    userAnswers.getV(NicCategoryPage)

  def extractPensionStatusV(userAnswers: UserAnswers): AnswerV[PensionStatus] =
    userAnswers.getV(PensionStatusPage)

  def extractClaimPeriodStartV(userAnswers: UserAnswers): AnswerV[LocalDate] =
    userAnswers.getV(ClaimPeriodStartPage)

  def extractClaimPeriodEndV(userAnswers: UserAnswers): AnswerV[LocalDate] =
    userAnswers.getV(ClaimPeriodEndPage)

  def extractPaymentFrequencyV(userAnswers: UserAnswers): AnswerV[PaymentFrequency] =
    userAnswers.getV(PaymentFrequencyPage)

  def extractTopUpPayment(userAnswers: UserAnswers): Seq[TopUpPayment] =
    userAnswers.getList(TopUpAmountPage)

  def extractAdditionalPayment(userAnswers: UserAnswers): Seq[AdditionalPayment] =
    userAnswers.getList(AdditionalPaymentAmountPage)

  def extractLastPayDateV(userAnswers: UserAnswers): AnswerV[LocalDate] =
    userAnswers.getV(LastPayDatePage)

  def extractFurloughStatusV(userAnswers: UserAnswers): AnswerV[FurloughStatus] =
    userAnswers.getV(FurloughStatusPage)

  def extractReferencePayDataV(userAnswers: UserAnswers): AnswerV[ReferencePayData] =
    (
      extractFurloughWithinClaimV(userAnswers),
      extractPaymentFrequencyV(userAnswers)
    ).mapN { (furloughPeriod, frequency) =>
      val payDates   = userAnswers.getList(PayDatePage)
      val periods    = generatePeriodsWithFurlough(payDates, furloughPeriod)
      val lastPayDay = determineLastPayDay(userAnswers, periods)
      val assigned   = assignPayDates(frequency, periods, lastPayDay)
      ReferencePayData(furloughPeriod, assigned, frequency)
    }

  def extractStatutoryLeaveData(userAnswers: UserAnswers): AnswerV[Option[StatutoryLeaveData]] = {
    for {
      pay  <- userAnswers.getO(StatutoryLeavePayPage)
      days <- userAnswers.getO(NumberOfStatLeaveDaysPage)
    } yield (pay, days).mapN { case (amount, days) => StatutoryLeaveData(days, amount.value) }
  }.sequence

  def extractPhaseTwoReferencePayDataV(userAnswers: UserAnswers): AnswerV[PhaseTwoReferencePayData] =
    (
      extractFurloughWithinClaimV(userAnswers),
      extractPaymentFrequencyV(userAnswers),
      extractStatutoryLeaveData(userAnswers)
    ).mapN { (furloughPeriod, frequency, statLeave) =>
      val payDates                      = userAnswers.getList(PayDatePage)
      val actuals                       = userAnswers.getList(PartTimeHoursPage)
      val usuals: Seq[UsualHours]       = userAnswers.getList(PartTimeNormalHoursPage)
      val periods                       = generatePeriodsWithFurlough(payDates, furloughPeriod)
      val lastPayDay                    = determineLastPayDay(userAnswers, periods)
      val assigned                      = assignPayDates(frequency, periods, lastPayDay)
      val phaseTwo: Seq[PhaseTwoPeriod] = assignPartTimeHours(assigned, actuals, usuals)

      PhaseTwoReferencePayData(furloughPeriod, phaseTwo, frequency, statLeave)
    }

  def determineLastPayDay(userAnswers: UserAnswers, periods: Seq[Periods]): LocalDate =
    extractLastPayDateV(userAnswers) match {
      case Valid(date) => date
      case Invalid(_)  => periods.last.period.end
    }
}
