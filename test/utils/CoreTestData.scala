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

import java.util.UUID

import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.UserAnswers
import play.api.libs.json.Json

trait CoreTestData extends UserAnswersBuilder {

  def userAnswersId: String = UUID.randomUUID().toString
  def dummyUserAnswers = userAnswersJson()
  def emptyUserAnswers = UserAnswers(userAnswersId, Json.obj())

  lazy val mandatoryAnswers = emptyUserAnswers
    .withClaimPeriodStart("2020, 3, 1")
    .withClaimPeriodEnd("2020, 3, 31")
    .withPaymentFrequency(Monthly)
    .withNi
    .withPension
    .withRegularPayMethod()
    .withOngoingFurlough
    .withFurloughStartDate("2020, 3, 1")
    .withLastPayDate("2020, 3, 31")
    .withPayDate(List("2020, 2, 29", "2020, 3, 31"))

  def userAnswersJson(): UserAnswers =
    emptyUserAnswers
      .withFurloughStartDate("2020-03-01")
      .withLastPayDate("2020-04-20")
      .withOngoingFurlough
      .withRegularPayMethod
      .withRegularPayAmount(2000.0)
      .withPaymentFrequency(Monthly)
      .withPension
      .withNi
      .withClaimPeriodStart("2020-03-01")
      .withClaimPeriodEnd("2020-04-30")
      .withPayDate(List("2020-02-29", "2020-03-31", "2020-04-30"))

  lazy val answersWithPartialPeriod: UserAnswers =
    emptyUserAnswers
      .withFurloughStartDate("2020-03-10")
      .withOngoingFurlough
      .withPaymentFrequency(Monthly)
      .withRegularPayAmount(3500)
      .withRegularPayMethod
      .withClaimPeriodStart("2020-03-01")
      .withClaimPeriodEnd("2020-03-31")
      .withPension
      .withNi
      .withLastPayDate("2020-03-31")
      .withPayDate(List("2020-02-29", "2020-03-31"))

  lazy val variableMonthlyPartial: UserAnswers =
    emptyUserAnswers.withEndedFurlough.withEmployeeStartedAfter1Feb2019
      .withNotToppedUp()
      .withFurloughStartDate("2020-03-10")
      .withFurloughEndDate("2020-04-20")
      .withEmployeeStartDate("2019-12-01")
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2020-03-01")
      .withVariablePayMethod
      .withAnnualPayAmount(10000.0)
      .withPartialPayBeforeFurlough(1000.0)
      .withPartialPayAfterFurlough(800.0)
      .withLastPayDate("2020-04-20")
      .withClaimPeriodStart("2020-03-10")
      .withClaimPeriodEnd("2020-04-30")
      .withNi
      .withPension
      .withPayDate(List("2020-02-29", "2020-03-31", "2020-04-30"))

  lazy val variableAveragePartial: UserAnswers =
    emptyUserAnswers.withOngoingFurlough
      .withAnnualPayAmount(12960.0)
      .withEmployeeStartedAfter1Feb2019
      .withEmployeeStartDate("2019-08-01")
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2020-03-01")
      .withClaimPeriodEnd("2020-03-31")
      .withNotToppedUp
      .withLastPayDate("2020-03-31")
      .withPartialPayBeforeFurlough(280.0)
      .withFurloughStartDate("2020-03-05")
      .withVariablePayMethod
      .withNi
      .withPension
      .withPayDate(List("2020-02-29", "2020-03-31"))

  def variableWeekly(lastPayDate: String = "2020-03-21"): UserAnswers =
    emptyUserAnswers.withEndedFurlough.withVariablePayMethod
      .withAnnualPayAmount(10000.0)
      .withEmployeeStartedAfter1Feb2019
      .withEmployeeStartDate("2019-12-01")
      .withFurloughStartDate("2020-03-10")
      .withFurloughEndDate("2020-03-21")
      .withPaymentFrequency(Weekly)
      .withClaimPeriodStart("2020-03-01")
      .withClaimPeriodEnd("2020-03-21")
      .withNotToppedUp()
      .withNi
      .withPension
      .withLastPayDate(lastPayDate)
      .withPayDate(List("2020-02-29", "2020-03-07", "2020-03-14", "2020-03-21"))

  lazy val variableFortnightly: UserAnswers =
    emptyUserAnswers.withEndedFurlough.withVariablePayMethod
      .withPaymentFrequency(FortNightly)
      .withAnnualPayAmount(10000.0)
      .withEmployeeStartedAfter1Feb2019
      .withEmployeeStartDate("2019-12-01")
      .withFurloughStartDate("2020-03-10")
      .withFurloughEndDate("2020-03-21")
      .withClaimPeriodStart("2020-03-01")
      .withClaimPeriodEnd("2020-03-21")
      .withLastPayDate("2020-03-28")
      .withNotToppedUp
      .withNi
      .withPension
      .withPayDate(List("2020-02-29", "2020-03-14", "2020-03-28"))

  lazy val variableFourweekly: UserAnswers =
    emptyUserAnswers.withEndedFurlough
      .withVariablePayMethod()
      .withAnnualPayAmount(10000.0)
      .withEmployeeStartedAfter1Feb2019
      .withPaymentFrequency(FourWeekly)
      .withEmployeeStartDate("2019-12-01")
      .withFurloughEndDate("2020-04-26")
      .withClaimPeriodStart("2020-03-01")
      .withClaimPeriodEnd("2020-03-21")
      .withLastPayDate("2020-04-25")
      .withFurloughStartDate("2020-03-10")
      .withNi
      .withPension
      .withNotToppedUp
      .withPayDate(List("2020-02-29", "2020-03-28", "2020-04-25"))

  lazy val manyPeriods =
    emptyUserAnswers.withEndedFurlough
      .withPaymentFrequency(Weekly)
      .withVariablePayMethod
      .withAnnualPayAmount(31970)
      .withPartialPayBeforeFurlough(200.0)
      .withEmployeeStartedOnOrBefore1Feb2019
      .withFurloughStartDate("2020-03-01")
      .withFurloughEndDate("2020-03-31")
      .withClaimPeriodStart("2020-03-01")
      .withClaimPeriodEnd("2020-03-31")
      .withLastPayDate("2020-03-31")
      .withNotToppedUp
      .withNi
      .withPension
      .withLastYear(List("2019-03-05" -> 500, "2019-03-12" -> 450, "2019-03-19" -> 500, "2019-03-26" -> 550, "2019-04-02" -> 600))
      .withPayDate(List("2020-02-25", "2020-03-03", "2020-03-10", "2020-03-17", "2020-03-24", "2020-03-31"))
}
