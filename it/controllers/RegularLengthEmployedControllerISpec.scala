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
import assets.PageTitles._
import config.featureSwitch.{ExtensionTwoNewStarterFlow, FeatureSwitching}
import models.PaymentFrequency.Monthly
import models.{RegularLengthEmployed, UserAnswers}
import play.api.http.Status._
import play.api.libs.json.Json
import utils.{CreateRequestHelper, CustomMatchers, ITCoreTestData, IntegrationSpecBase}

class RegularLengthEmployedControllerISpec extends IntegrationSpecBase
  with CreateRequestHelper with CustomMatchers with BaseITConstants with ITCoreTestData with FeatureSwitching {

  "the ExtensionTwoNewStarterFlow is turned ON" when {

    "GET /regular-length-employed" should {

      "redirect display the correct title" in {

        enable(ExtensionTwoNewStarterFlow)

        val userAnswers: UserAnswers =
          emptyUserAnswers
            .withClaimPeriodStart("2020, 11, 1")
            .withClaimPeriodEnd("2020, 11, 30")
            .withFurloughStartDate("2020, 11, 1")
            .withFurloughStatus()
            .withPaymentFrequency(Monthly)
            .withNiCategory()
            .withPensionStatus()
            .withPayMethod()
            .withLastPayDate("2020, 10, 31")
            .withPayDate(List("2020, 10, 31"))

        setAnswers(userAnswers)

        val res = getRequestHeaders("/regular-length-employed")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

        whenReady(res) { result =>
          result should have(
            httpStatus(OK),
            titleOf(regularLengthEmployed)
          )
        }
      }
    }

    "POST /regular-length-employed" when {

      "user enters a 'Yes' answer" should {

        "redirect to RegularPayAmount page when PayDate is defined" in {

          enable(ExtensionTwoNewStarterFlow)

          val userAnswers: UserAnswers =
            emptyUserAnswers
              .withClaimPeriodStart("2020, 11, 1")
              .withClaimPeriodEnd("2020, 11, 30")
              .withFurloughStartDate("2020, 11, 1")
              .withFurloughStatus()
              .withPaymentFrequency(Monthly)
              .withNiCategory()
              .withPensionStatus()
              .withPayMethod()
              .withLastPayDate("2020, 10, 31")
              .withPayDate(List("2020, 11, 30"))

          setAnswers(userAnswers)

          val res = postRequestHeader("/regular-length-employed",
            Json.obj("value" -> RegularLengthEmployed.Yes.toString)
          )("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

          whenReady(res) { result =>
            result should have(
              httpStatus(SEE_OTHER),
              redirectLocation(controllers.routes.RegularPayAmountController.onPageLoad().url)
            )
          }
        }
      }

      "user enters a 'No' answer" should {

        "redirect to OnPayrollBefore30thOct2020 page" in {

          enable(ExtensionTwoNewStarterFlow)

          val userAnswers: UserAnswers = emptyUserAnswers
            .withClaimPeriodStart("2020, 11, 1")
            .withClaimPeriodEnd("2020, 11, 30")
            .withFurloughStartDate("2020, 11, 1")
            .withFurloughStatus()
            .withPaymentFrequency(Monthly)
            .withNiCategory()
            .withPensionStatus()
            .withPayMethod()
            .withLastPayDate("2020, 10, 31")
            .withPayDate(List("2020, 11, 30"))

          setAnswers(userAnswers)

          val res = postRequestHeader("/regular-length-employed",
            Json.obj("value" -> RegularLengthEmployed.No.toString)
          )("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

          whenReady(res) { result =>
            result should have(
              httpStatus(SEE_OTHER),
              redirectLocation(controllers.routes.OnPayrollBefore30thOct2020Controller.onPageLoad().url)
            )
          }
        }
      }

      "user enters an Invalid answer" should {

        "redirect back to the same RegularLengthEmployed page" in {

          enable(ExtensionTwoNewStarterFlow)

          val userAnswers: UserAnswers = emptyUserAnswers
            .withClaimPeriodStart("2020, 11, 1")
            .withClaimPeriodEnd("2020, 11, 30")
            .withFurloughStartDate("2020, 11, 1")
            .withFurloughStatus()
            .withPaymentFrequency(Monthly)
            .withNiCategory()
            .withPensionStatus()
            .withPayMethod()
            .withLastPayDate("2020, 10, 31")
            .withPayDate(List("2020, 11, 30"))

          setAnswers(userAnswers)

          val res = postRequestHeader("/regular-length-employed",
            Json.obj("value" -> "bleh")
          )("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

          whenReady(res) { result =>
            result should have(
              httpStatus(BAD_REQUEST)
            )
          }
        }
      }

    }
  }

  "the ExtensionTwoNewStarterFlow is turned OFF" when {

    "GET /regular-length-employed" should {

      "redirect to the start page" in {

        disable(ExtensionTwoNewStarterFlow)

        val userAnswers: UserAnswers = emptyUserAnswers
        setAnswers(userAnswers)

        val res = getRequestHeaders("/regular-length-employed")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

        whenReady(res) { result =>
          result should have(
            httpStatus(OK),
            titleOf(regularLengthEmployed)
          )
        }
      }
    }

    "POST /regular-length-employed" when {

      "user enters a 'Yes' answer" should {

        "redirect to RegularPayAmount page when PayDate is defined" in {

          disable(ExtensionTwoNewStarterFlow)

          val userAnswers: UserAnswers =
            emptyUserAnswers
              .withClaimPeriodStart("2020, 11, 1")
              .withClaimPeriodEnd("2020, 11, 30")
              .withFurloughStartDate("2020, 11, 1")
              .withFurloughStatus()
              .withPaymentFrequency(Monthly)
              .withNiCategory()
              .withPensionStatus()
              .withPayMethod()
              .withLastPayDate("2020, 10, 31")
              .withPayDate(List("2020, 11, 30"))

          setAnswers(userAnswers)


          val res = postRequestHeader("/regular-length-employed",
            Json.obj("value" -> RegularLengthEmployed.Yes.toString)
          )("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

          whenReady(res) { result =>
            result should have(
              httpStatus(SEE_OTHER),
              redirectLocation(controllers.routes.RegularPayAmountController.onPageLoad().url)
            )
          }
        }
      }

      "user enters a 'No' answer" should {

        "redirect to RegularPayAmount page" in {

          disable(ExtensionTwoNewStarterFlow)

          val userAnswers: UserAnswers = emptyUserAnswers
            .withClaimPeriodStart("2020, 11, 1")
            .withClaimPeriodEnd("2020, 11, 30")
            .withFurloughStartDate("2020, 11, 1")
            .withFurloughStatus()
            .withPaymentFrequency(Monthly)
            .withNiCategory()
            .withPensionStatus()
            .withPayMethod()
            .withLastPayDate("2020, 10, 31")
            .withPayDate(List("2020, 11, 30"))

          setAnswers(userAnswers)

          val res = postRequestHeader("/regular-length-employed",
            Json.obj("value" -> RegularLengthEmployed.No.toString)
          )("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

          whenReady(res) { result =>
            result should have(
              httpStatus(SEE_OTHER),
              redirectLocation(controllers.routes.RegularPayAmountController.onPageLoad().url)
            )
          }
        }
      }

      "user enters an Invalid answer" should {

        "redirect back to the same RegularLengthEmployed page" in {

          disable(ExtensionTwoNewStarterFlow)

          val userAnswers: UserAnswers = emptyUserAnswers
            .withClaimPeriodStart("2020, 11, 1")
            .withClaimPeriodEnd("2020, 11, 30")
            .withFurloughStartDate("2020, 11, 1")
            .withFurloughStatus()
            .withPaymentFrequency(Monthly)
            .withNiCategory()
            .withPensionStatus()
            .withPayMethod()
            .withLastPayDate("2020, 10, 31")
            .withPayDate(List("2020, 11, 30"))

          setAnswers(userAnswers)

          val res = postRequestHeader("/regular-length-employed",
            Json.obj("value" -> "bleh")
          )("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

          whenReady(res) { result =>
            result should have(
              httpStatus(BAD_REQUEST)
            )
          }
        }
      }

    }
  }

}
