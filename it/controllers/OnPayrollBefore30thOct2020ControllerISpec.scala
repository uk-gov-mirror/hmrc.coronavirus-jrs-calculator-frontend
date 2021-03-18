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
import assets.PageTitles.onPayrollBefore30thOct2020
import models.PaymentFrequency.Monthly
import models.UserAnswers
import play.api.http.Status._
import play.api.libs.json.Json
import utils.{CreateRequestHelper, CustomMatchers, ITCoreTestData, IntegrationSpecBase}

class OnPayrollBefore30thOct2020ControllerISpec extends IntegrationSpecBase with CreateRequestHelper with CustomMatchers with BaseITConstants with ITCoreTestData {

  "GET /october-payroll" when {

    "is a Regular Journey" should {

      "return correct page & title, Status: 200" in {

        val userAnswers: UserAnswers = dummyUserAnswers
        setAnswers(userAnswers)

        val res = getRequestHeaders("/october-payroll")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

        whenReady(res) { result =>
          result should have(
            httpStatus(OK),
            titleOf(onPayrollBefore30thOct2020)
          )
        }
      }
    }

    "is a Variable Journey" when {

      "employee started before 1 September 2020" must {

        "return correct page & title, Status: 200" in {

          val userAnswers: UserAnswers = hasEmployeeBeenFurloughedAfterNovember.withEmployeeStartDate("2020, 8, 30")
          setAnswers(userAnswers)

          val res = getRequestHeaders("/october-payroll")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

          whenReady(res) { result =>
            result should have(
              httpStatus(SEE_OTHER),
              redirectLocation(controllers.routes.PreviousFurloughPeriodsController.onPageLoad().url)
            )
          }
        }
      }

      "employee started between 1 Setepmber 2020 and 30th October 2020" must {

        "return correct page & title, Status: 200" in {

          val userAnswers: UserAnswers = hasEmployeeBeenFurloughedAfterNovember.withEmployeeStartDate("2020, 9, 1")
          setAnswers(userAnswers)

          val res = getRequestHeaders("/october-payroll")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

          whenReady(res) { result =>
            result should have(
              httpStatus(OK),
              titleOf(onPayrollBefore30thOct2020)
            )
          }
        }
      }
    }

  }

  "POST /october-payroll" when {

    "claim period start is before 2020-11-01" when {

      "redirect to RootPageController page" in {

        val userAnswers: UserAnswers = emptyUserAnswers
          .withClaimPeriodStart("2020, 10, 31")
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

        val res = postRequestHeader(
          path = "/october-payroll",
          formJson = Json.obj("value" -> "true")
        )("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)


        whenReady(res) { result =>
          result should have(
            httpStatus(SEE_OTHER),
            redirectLocation(controllers.routes.RootPageController.onPageLoad().url)
          )
        }
      }
    }

    "pay dates is empty" when {

      "redirect to PayDate page" in {

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
          .withPayDate(List())

        setAnswers(userAnswers)

        val res = postRequestHeader(
          path = "/october-payroll",
          formJson = Json.obj("value" -> "true")
        )("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)


        whenReady(res) { result =>
          result should have(
            httpStatus(SEE_OTHER),
            redirectLocation(controllers.routes.PayDateController.onPageLoad(1).url)
          )
        }
      }
    }

    "pay dates is not an empty list" when {

      "redirect to RegularPayAmount page" in {

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
          .withPayDate(List("2020, 10, 31"))

        setAnswers(userAnswers)

        val res = postRequestHeader(
          path = "/october-payroll",
          formJson = Json.obj("value" -> "true")
        )("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)


        whenReady(res) { result =>
          result should have(
            httpStatus(SEE_OTHER),
            redirectLocation(controllers.routes.RegularPayAmountController.onPageLoad().url)
          )
        }
      }
    }
  }
}
