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

import models.{AdditionalPayment, Amount, BranchingQuestions, CylbPayment, NicCategory, NonFurloughPay, PaymentFrequency, PensionStatus, Period, ReferencePayData, TopUpPayment, UserAnswers}
import pages._
import services.{FurloughPeriodExtractor, PeriodHelper}

trait DataExtractor extends FurloughPeriodExtractor with PeriodHelper {

  def extractPriorFurloughPeriod(userAnswers: UserAnswers): Option[Period] =
    for {
      furloughStart <- userAnswers.get(FurloughStartDatePage)
      employeeStartDate = userAnswers.get(EmployeeStartDatePage).getOrElse(LocalDate.of(2019, 4, 6))
    } yield endDateOrTaxYearEnd(Period(employeeStartDate, furloughStart.minusDays(1)))

  def extractNonFurlough(userAnswers: UserAnswers): NonFurloughPay = {
    val preFurloughPay = userAnswers.get(PartialPayBeforeFurloughPage)
    val postFurloughPay = userAnswers.get(PartialPayAfterFurloughPage)

    NonFurloughPay(preFurloughPay.map(v => Amount(v.value)), postFurloughPay.map(v => Amount(v.value)))
  }

  def extractBranchingQuestions(userAnswers: UserAnswers): Option[BranchingQuestions] =
    for {
      payMethod <- userAnswers.get(PayMethodPage)
      employeeStarted = userAnswers.get(EmployedStartedPage)
      employeeStartDate = userAnswers.get(EmployeeStartDatePage)
    } yield BranchingQuestions(payMethod, employeeStarted, employeeStartDate)

  def extractSalary(userAnswers: UserAnswers): Option[Amount] =
    userAnswers.get(RegularPayAmountPage).map(v => Amount(v.amount))

  def extractAnnualPayAmount(userAnswers: UserAnswers): Option[Amount] =
    userAnswers.get(AnnualPayAmountPage).map(v => Amount(v.amount))

  def extractCylbPayments(userAnswers: UserAnswers): Seq[CylbPayment] =
    userAnswers.getList(LastYearPayPage)

  def extractNicCategory(userAnswers: UserAnswers): Option[NicCategory] =
    userAnswers.get(NicCategoryPage)

  def extractPensionStatus(userAnswers: UserAnswers): Option[PensionStatus] =
    userAnswers.get(PensionStatusPage)

  def extractClaimPeriodStart(userAnswers: UserAnswers): Option[LocalDate] =
    userAnswers.get(ClaimPeriodStartPage)

  def extractClaimPeriodEnd(userAnswers: UserAnswers): Option[LocalDate] =
    userAnswers.get(ClaimPeriodEndPage)

  def extractPaymentFrequency(userAnswers: UserAnswers): Option[PaymentFrequency] =
    userAnswers.get(PaymentFrequencyPage)

  def extractTopUpPayment(userAnswers: UserAnswers): Seq[TopUpPayment] =
    userAnswers.getList(TopUpAmountPage)

  def extractAdditionalPayment(userAnswers: UserAnswers): Seq[AdditionalPayment] =
    userAnswers.getList(AdditionalPaymentAmountPage)

  def extractReferencePayData(userAnswers: UserAnswers): Option[ReferencePayData] =
    for {
      furloughPeriod <- extractFurloughWithinClaim(userAnswers)
      payDates = userAnswers.getList(PayDatePage)
      periods = generatePeriodsWithFurlough(payDates, furloughPeriod)
      frequency  <- extractPaymentFrequency(userAnswers)
      lastPayDay <- userAnswers.get(LastPayDatePage)
      assigned = assignPayDates(frequency, periods, lastPayDay)
    } yield ReferencePayData(furloughPeriod, assigned, frequency)

}
