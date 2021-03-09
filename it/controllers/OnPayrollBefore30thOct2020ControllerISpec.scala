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

    "is a Variable Journey" should {

      "return correct page & title, Status: 200" in {

        val userAnswers: UserAnswers = hasEmployeeBeenFurloughedAfterNovember
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

  "POST /october-payroll" when {

    "enters a valid answer" when {

      "redirect to <next page> page" in {

        val res = postRequest(
          path = "/october-payroll",
          formJson = Json.obj("value" -> "true")
        )()


        whenReady(res) { result =>
          result should have(
            httpStatus(SEE_OTHER),
            redirectLocation(controllers.routes.SessionExpiredController.onPageLoad().url) //TODO: Update when routing is added!
          )
        }
      }
    }
  }
}
