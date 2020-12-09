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

package controllers

import assets.BaseITConstants
import models.PaymentFrequency._
import models._
import play.api.test.Helpers._
import utils.{CreateRequestHelper, CustomMatchers, ITCoreTestData, IntegrationSpecBase}

class ConfirmationControllerISpec extends IntegrationSpecBase with CreateRequestHelper with CustomMatchers
  with BaseITConstants with ITCoreTestData {

  val scenarios: Seq[(UserAnswers, BigDecimal)] = Seq(
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 1")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020, 11, 28")
      .withPaymentFrequency(FourWeekly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-31", "2020-11-28"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(2000.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(FullPeriod(Period("2020, 11, 1".toLocalDate, "2020, 11, 28".toLocalDate))))
      .withUsualHours(List(UsualHours("2020, 11, 28".toLocalDate, Hours(148.0))))
      .withPartTimeHours(List(PartTimeHours("2020, 11, 28".toLocalDate, Hours(40.0))))
      -> 1167.57,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 1")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020, 11, 28")
      .withPaymentFrequency(FourWeekly)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-04", "2020-11-01", "2020-11-29"))
      .withRegularPayAmount(3300.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      -> 2333.52,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 28")
      .withFurloughStartDate("2020, 11, 1")
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FourWeekly)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-31", "2020-11-28"))
      .withRegularPayAmount(3300.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      -> 2307.68,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 03, 01")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020, 11, 30")
      .withPaymentFrequency(FourWeekly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-31", "2020-11-28", "2020-12-26"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(3500.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(FullPeriod(Period("2020, 11, 1".toLocalDate, "2020, 11, 28".toLocalDate)),
        PartialPeriod(
          Period("2020, 11, 29".toLocalDate, "2020, 12, 26".toLocalDate),
          Period("2020, 11, 29".toLocalDate, "2020, 11, 30".toLocalDate))))
      .withUsualHours(List(UsualHours("2020, 11, 28".toLocalDate, Hours(148.0)),
        UsualHours("2020, 12, 26".toLocalDate, Hours(15.86))))
      .withPartTimeHours(List(PartTimeHours("2020, 11, 28".toLocalDate, Hours(40.0)),
        PartTimeHours("2020, 12, 26".toLocalDate, Hours(1.86))))
      -> 1831.11,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 30")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 29")
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FourWeekly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-29", "2020-12-27"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(2200.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        PartialPeriod(
          Period("2020, 11, 30".toLocalDate, "2020, 12, 27".toLocalDate),
          Period("2020, 11, 30".toLocalDate, "2020, 11, 30".toLocalDate))))
      .withUsualHours(List(UsualHours("2020, 12, 27".toLocalDate, Hours(148.0))))
      .withPartTimeHours(List(PartTimeHours("2020, 12, 27".toLocalDate, Hours(25.0))))
      -> 52.24,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 01")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020, 11, 30")
      .withPaymentFrequency(FourWeekly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-31", "2020-11-28", "2020-12-26"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(3500.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        PartialPeriod(
          Period("2020, 11, 29".toLocalDate, "2020, 12, 26".toLocalDate),
          Period("2020, 11, 29".toLocalDate, "2020, 11, 30".toLocalDate))))
      .withUsualHours(List(UsualHours("2020, 12, 26".toLocalDate, Hours(15.86))))
      .withPartTimeHours(List(PartTimeHours("2020, 12, 26".toLocalDate, Hours(1.86))))
      -> 2454.81,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 29")
      .withFurloughStartDate("2020, 11, 01")
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FourWeekly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-31", "2020-11-28", "2020-12-26"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(3500.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        PartialPeriod(
          Period("2020, 11, 29".toLocalDate, "2020, 12, 26".toLocalDate),
          Period("2020, 11, 29".toLocalDate, "2020, 11, 29".toLocalDate))))
      .withUsualHours(List(UsualHours("2020, 12, 26".toLocalDate, Hours(15.86))))
      .withPartTimeHours(List(PartTimeHours("2020, 12, 26".toLocalDate, Hours(1.86))))
      -> 2381.25,
  )

  "GET /confirmation" should {

    "show the page" when {

      scenarios.zipWithIndex.foreach {
        case ((scenario, outcome), index) =>

          s"the user has answered the questions for scenario $index" in {
            val userAnswers: UserAnswers = scenario

            setAnswers(userAnswers)

            val res = getRequestHeaders("/confirmation")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

            whenReady(res) { result =>
              result should have(
                httpStatus(OK),
                titleOf("What you can claim for this employee - Job Retention Scheme calculator - GOV.UK"),
                contentExists(s"${outcome.setScale(2).toString()}"),
              )
            }
          }
      }

      "the user has answered the questions for regular journey" in {

        val userAnswers: UserAnswers = dummyUserAnswers

        setAnswers(userAnswers)

        val res = getRequestHeaders("/confirmation")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

        whenReady(res) { result =>
          result should have(
            httpStatus(OK),
            titleOf("What you can claim for this employee - Job Retention Scheme calculator - GOV.UK"),
            contentExists(s"Total furlough grant for pay period = £")
          )
        }
      }
      "the user has answered the questions for dummyUserAnswersNoLastPayDate" in {

        val userAnswers: UserAnswers = dummyUserAnswersNoLastPayDate

        setAnswers(userAnswers)

        val res = getRequestHeaders("/confirmation")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

        whenReady(res) { result =>
          result should have(
            httpStatus(OK),
            titleOf("What you can claim for this employee - Job Retention Scheme calculator - GOV.UK")
          )
        }
      }
      "the user has answered the questions for variableMonthlyPartial" in {

        val userAnswers: UserAnswers = variableMonthlyPartial

        setAnswers(userAnswers)

        val res = getRequestHeaders("/confirmation")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

        whenReady(res) { result =>
          result should have(
            httpStatus(OK),
            titleOf("What you can claim for this employee - Job Retention Scheme calculator - GOV.UK")
          )
        }
      }
      "the user has answered the questions for phase 2" in {

        val userAnswers: UserAnswers = phaseTwoJourney()

        setAnswers(userAnswers)

        val res = getRequestHeaders("/confirmation")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

        whenReady(res) { result =>
          result should have(
            httpStatus(OK),
            titleOf("What you can claim for this employee - Job Retention Scheme calculator - GOV.UK")
          )
        }
      }
    }


    "redirect to another page" when {

      "the user has not answered the questions" in {

        val userAnswers: UserAnswers = emptyUserAnswers

        setAnswers(userAnswers)

        val res = getRequest("/confirmation")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

        //TODO Should redirect to reset or start again page
        whenReady(res) { result =>
          result should have(
            httpStatus(SEE_OTHER),
            redirectLocation("/job-retention-scheme-calculator/error")
          )
        }
      }
    }
  }
}
