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

import base.SpecBase
import cats.scalatest.ValidatedValues
import models.ClaimPeriodQuestion.{ClaimOnDifferentPeriod, ClaimOnSamePeriod}
import models.FurloughPeriodQuestion.{FurloughedOnDifferentPeriod, FurloughedOnSamePeriod}
import models.PayPeriodQuestion.{UseDifferentPayPeriod, UseSamePayPeriod}
import models.PaymentFrequency.Monthly
import models.{FurloughStatus, UserAnswers}
import pages._
import play.api.libs.json.{JsObject, Json}
import utils.CoreTestData

class FastJourneyUserAnswersHandlerSpec extends SpecBase with CoreTestData with ValidatedValues {

  "delete all data from the DB if answer is `No` to claim period question excluding session id" in new FastJourneyUserAnswersHandler {
    val userAnswers: UserAnswers = dummyUserAnswers.withClaimPeriodQuestion(ClaimOnDifferentPeriod)
    val actualUserAnswers: UserAnswersState = updateJourney(userAnswers).toOption.value

    userAnswers.data.value.values.size must be > 1
    actualUserAnswers.updated.id mustBe actualUserAnswers.original.id
    actualUserAnswers.original.data mustBe userAnswers.data
    actualUserAnswers.updated.data mustBe Json.obj()
  }

  "delete nothing from the DB if answer is `Yes` to claim period question" in new FastJourneyUserAnswersHandler {
    val userAnswers: UserAnswers = dummyUserAnswers.withClaimPeriodQuestion(ClaimOnSamePeriod)
    val actual: UserAnswersState = updateJourney(userAnswers).toOption.value

    userAnswers.data.value.values.size must be > 1
    actual.original.data mustBe userAnswers.data
    actual.updated.id mustBe userAnswers.id
    actual.updated.data mustBe userAnswers.data
  }

  "delete all from the DB if answer is `No` to furlough period question excluding claim period" in new FastJourneyUserAnswersHandler {
    val userAnswers: UserAnswers = dummyUserAnswers
      .withClaimPeriodQuestion(ClaimOnSamePeriod)
      .withFurloughPeriodQuestion(FurloughedOnDifferentPeriod)

    val expectedUserAnswersData: JsObject = emptyUserAnswers
      .copy(id = userAnswers.id)
      .withClaimPeriodStart(userAnswers.getV(ClaimPeriodStartPage).value.toString)
      .withClaimPeriodEnd(userAnswers.getV(ClaimPeriodEndPage).value.toString)
      .data

    val actual: UserAnswersState = updateJourney(userAnswers).toOption.value

    userAnswers.data.value.values.size must be > 2
    actual.updated.id mustBe userAnswers.id
    actual.original.data mustBe userAnswers.data
    actual.updated.data mustBe expectedUserAnswersData
  }

  "delete nothing from the DB if answer is `Yes` to furlough period question" in new FastJourneyUserAnswersHandler {
    val userAnswers: UserAnswers = dummyUserAnswers
      .withClaimPeriodQuestion(ClaimOnSamePeriod)
      .withFurloughPeriodQuestion(FurloughedOnSamePeriod)

    val actual: UserAnswersState = updateJourney(userAnswers).toOption.value

    userAnswers.data.value.values.size must be > 2
    actual.updated.id mustBe userAnswers.id
    actual.original.data mustBe userAnswers.data
    actual.updated.data mustBe userAnswers.data
  }

  "delete all from the DB if answer is `No` to pay period, keeping claim and furlough period" in new FastJourneyUserAnswersHandler {
    val userAnswers: UserAnswers = dummyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-3-31")
      .withClaimPeriodQuestion(ClaimOnSamePeriod)
      .withFurloughPeriodQuestion(FurloughedOnSamePeriod)
      .withPayPeriodQuestion(UseDifferentPayPeriod)

    val expectedUserAnswersData: JsObject = emptyUserAnswers
      .copy(id = userAnswers.id)
      .withClaimPeriodStart(userAnswers.getV(ClaimPeriodStartPage).value.toString)
      .withClaimPeriodEnd(userAnswers.getV(ClaimPeriodEndPage).value.toString)
      .withFurloughStartDate(userAnswers.getV(FurloughStartDatePage).value.toString)
      .withFurloughEndDate(userAnswers.getV(FurloughEndDatePage).value.toString)
      .withFurloughStatus(userAnswers.getV(FurloughStatusPage).value)
      .data

    val actualUserAnswer: UserAnswersState = updateJourney(userAnswers).toOption.value

    userAnswers.data.value.values.size must be > 2
    actualUserAnswer.updated.id mustBe userAnswers.id
    actualUserAnswer.original.data mustBe userAnswers.data
    actualUserAnswer.updated.data mustBe expectedUserAnswersData
  }

  "delete data from the DB if answer is `Yes` to pay period question excluding Claim, Furlough," +
    "pay periods, pay question, pay frequency, furlough status and last pay date" in new FastJourneyUserAnswersHandler {
    val userAnswers: UserAnswers = dummyUserAnswers
      .withFurloughEndDate("2020-3-31")
      .withClaimPeriodQuestion(ClaimOnSamePeriod)
      .withFurloughPeriodQuestion(FurloughedOnSamePeriod)
      .withPayPeriodQuestion(UseSamePayPeriod)

    val expectedUserAnswersData: JsObject = emptyUserAnswers
      .copy(data = Json.obj())
      .withClaimPeriodStart(userAnswers.getV(ClaimPeriodStartPage).value.toString)
      .withClaimPeriodEnd(userAnswers.getV(ClaimPeriodEndPage).value.toString)
      .withFurloughStartDate(userAnswers.getV(FurloughStartDatePage).value.toString)
      .withFurloughEndDate(userAnswers.getV(FurloughEndDatePage).value.toString)
      .withPayDate(userAnswers.getList(PayDatePage).map(_.toString).toList)
      .withPaymentFrequency(Monthly)
      .withFurloughStatus()
      .withLastPayDate(dummyUserAnswers.getV(LastPayDatePage).value.toString)
      .data

    val actual: UserAnswersState = updateJourney(userAnswers).toOption.value

    userAnswers.data.value.values.size must be > 2
    actual.updated.id mustBe userAnswers.id
    actual.updated.data mustBe expectedUserAnswersData
  }

  "handle scenario where lastPayDate is empty" in new FastJourneyUserAnswersHandler {
    val userAnswers: UserAnswers = dummyUserAnswersNoLastPayDate
      .withClaimPeriodQuestion(ClaimOnSamePeriod)
      .withFurloughPeriodQuestion(FurloughedOnSamePeriod)
      .withPayPeriodQuestion(UseSamePayPeriod)

    val expectedUserAnswersData: JsObject = emptyUserAnswers
      .copy(data = Json.obj())
      .withClaimPeriodStart(userAnswers.getV(ClaimPeriodStartPage).value.toString)
      .withClaimPeriodEnd(userAnswers.getV(ClaimPeriodEndPage).value.toString)
      .withFurloughStartDate(userAnswers.getV(FurloughStartDatePage).value.toString)
      .withPayDate(userAnswers.getList(PayDatePage).map(_.toString).toList)
      .withPaymentFrequency(Monthly)
      .withFurloughStatus()
      .data

    val actual: UserAnswersState = updateJourney(userAnswers).toOption.value

    userAnswers.data.value.values.size must be > 2
    actual.updated.id mustBe userAnswers.id
    actual.updated.data mustBe expectedUserAnswersData
  }
}
