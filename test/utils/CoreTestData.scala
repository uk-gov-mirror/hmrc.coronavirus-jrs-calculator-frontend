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

import models.FurloughStatus.FurloughEnded
import models.PartTimeQuestion.PartTimeNo
import models.PayMethod.Variable
import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.TopUpStatus.NotToppedUp
import models.UserAnswers
import play.api.libs.json.Json

trait CoreTestData extends UserAnswersBuilder {

  def userAnswersId: String = UUID.randomUUID().toString
  def dummyUserAnswers = regularJourney()
  def emptyUserAnswers = UserAnswers(userAnswersId, Json.obj())

  def mandatoryAnswersOnRegularMonthly =
    emptyUserAnswers
      .withClaimPeriodStart("2020, 3, 1")
      .withClaimPeriodEnd("2020, 3, 31")
      .withFurloughStartDate("2020, 3, 1")
      .withFurloughStatus()
      .withPaymentFrequency(Monthly)
      .withNiCategory()
      .withPensionStatus()
      .withPayMethod()
      .withLastPayDate("2020, 3, 31")
      .withPayDate(List("2020, 2, 29", "2020, 3, 31"))

  private def regularJourney(): UserAnswers =
    mandatoryAnswersOnRegularMonthly
      .withClaimPeriodStart("2020-03-01")
      .withClaimPeriodEnd("2020-04-30")
      .withFurloughStartDate("2020-03-01")
      .withLastPayDate("2020-04-20")
      .withRegularPayAmount(2000.0)
      .withPayDate(List("2020-02-29", "2020-03-31", "2020-04-30"))

  def phaseTwoJourney(): UserAnswers =
    emptyUserAnswers
      .withClaimPeriodStart("2020, 7, 1")
      .withClaimPeriodEnd("2020, 7, 31")
      .withFurloughStartDate("2020, 3, 20")
      .withFurloughStatus()
      .withPaymentFrequency(Monthly)
      .withNiCategory()
      .withPensionStatus()
      .withPayMethod()
      .withPayDate(List("2020, 6, 30", "2020, 7, 31"))
      .withLastPayDate("2020, 7, 31")
      .withPartTimeQuestion(PartTimeNo)
      .withRegularPayAmount(2000.00)

  lazy val variablePartial =
    emptyUserAnswers
      .withPayMethod(Variable)
      .withEmployeeStartedAfter1Feb2019
      .withNiCategory()
      .withPensionStatus()

  lazy val variableMonthlyPartial: UserAnswers =
    variablePartial
      .withClaimPeriodStart("2020-03-01")
      .withClaimPeriodEnd("2020-04-30")
      .withFurloughStartDate("2020-03-10")
      .withFurloughEndDate("2020-04-20")
      .withEmployeeStartDate("2019-12-01")
      .withLastPayDate("2020-04-20")
      .withFurloughStatus(FurloughEnded)
      .withPaymentFrequency(Monthly)
      .withToppedUpStatus(NotToppedUp)
      .withAnnualPayAmount(10000.0)
      .withPartialPayBeforeFurlough(1000.0)
      .withPartialPayAfterFurlough(800.0)
      .withPayDate(List("2020-02-29", "2020-03-31", "2020-04-30"))

  lazy val variableAveragePartial: UserAnswers =
    variablePartial
      .withFurloughStatus()
      .withAnnualPayAmount(12960.0)
      .withEmployeeStartDate("2019-08-01")
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2020-03-01")
      .withClaimPeriodEnd("2020-03-31")
      .withToppedUpStatus(NotToppedUp)
      .withLastPayDate("2020-03-31")
      .withPartialPayBeforeFurlough(280.0)
      .withFurloughStartDate("2020-03-05")
      .withPayDate(List("2020-02-29", "2020-03-31"))

  private lazy val variablePartialWith10KAnnualPayment =
    variablePartial
      .withClaimPeriodStart("2020-03-01")
      .withClaimPeriodEnd("2020-03-21")
      .withFurloughStartDate("2020-03-10")
      .withFurloughEndDate("2020-03-21")
      .withEmployeeStartDate("2019-12-01")
      .withAnnualPayAmount(10000.0)
      .withToppedUpStatus(NotToppedUp)
      .withFurloughStatus(FurloughEnded)

  lazy val cylbLeapYear: UserAnswers =
    emptyUserAnswers
      .withClaimPeriodStart("2020-03-01")
      .withClaimPeriodEnd("2020-03-31")
      .withFurloughStartDate("2020-03-01")
      .withFurloughStatus()
      .withPaymentFrequency(FortNightly)
      .withPayMethod(Variable)
      .withEmployeeStartedOnOrBefore1Feb2019()
      .withPayDate(
        List(
          "2020-02-18",
          "2020-03-03",
          "2020-03-17",
          "2020-03-31"
        ))
      .withLastPayDate("2020-03-31")

  lazy val variableWeekly: UserAnswers =
    variablePartialWith10KAnnualPayment
      .withLastPayDate("2020-03-21")
      .withPaymentFrequency(Weekly)
      .withPayDate(List("2020-02-29", "2020-03-07", "2020-03-14", "2020-03-21"))

  lazy val variableFortnightly: UserAnswers =
    variablePartialWith10KAnnualPayment
      .withLastPayDate("2020-03-28")
      .withPaymentFrequency(FortNightly)
      .withPayDate(List("2020-02-29", "2020-03-14", "2020-03-28"))

  lazy val variableFourweekly: UserAnswers =
    variablePartialWith10KAnnualPayment
      .withPaymentFrequency(FourWeekly)
      .withFurloughEndDate("2020-04-26")
      .withLastPayDate("2020-04-25")
      .withFurloughStartDate("2020-03-10")
      .withPayDate(List("2020-02-29", "2020-03-28", "2020-04-25"))

  lazy val manyPeriods =
    emptyUserAnswers.withEmployeeStartedOnOrBefore1Feb2019
      .withPayMethod(Variable)
      .withFurloughStatus(FurloughEnded)
      .withPaymentFrequency(Weekly)
      .withAnnualPayAmount(31970)
      .withPartialPayBeforeFurlough(200.0)
      .withFurloughStartDate("2020-03-01")
      .withFurloughEndDate("2020-03-31")
      .withClaimPeriodStart("2020-03-01")
      .withClaimPeriodEnd("2020-03-31")
      .withLastPayDate("2020-03-31")
      .withToppedUpStatus(NotToppedUp)
      .withNiCategory()
      .withPensionStatus()
      .withLastYear(List("2019-03-05" -> 500, "2019-03-12" -> 450, "2019-03-19" -> 500, "2019-03-26" -> 550, "2019-04-02" -> 600))
      .withPayDate(List("2020-02-25", "2020-03-03", "2020-03-10", "2020-03-17", "2020-03-24", "2020-03-31"))
}
