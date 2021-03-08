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
import models.RegularLengthEmployed
import play.api.http.Status._
import play.api.libs.json.Json
import utils.{CreateRequestHelper, CustomMatchers, IntegrationSpecBase}


class RegularLengthEmployedControllerISpec extends IntegrationSpecBase with CreateRequestHelper with CustomMatchers with BaseITConstants {

  "GET /regular-length-employed" when {

    "redirect to the start page" in {

      val res = getRequest("/regular-length-employed")()

      whenReady(res) { result =>
        result should have(
          httpStatus(OK),
          titleOf(regularLengthEmployed)
        )
      }
    }
  }

  "POST /regular-length-employed" when {

    "enters a No valid answer" when {

      "redirect to claim-period-end page" in {

        val res = postRequest("/regular-length-employed",
          Json.obj("value" -> RegularLengthEmployed.No.toString)
        )()

        whenReady(res) { result =>
          result should have(
            httpStatus(SEE_OTHER),
            redirectLocation(controllers.routes.OnPayrollBefore30thOct2020Controller.onPageLoad().url)
          )
        }
      }
    }
  }

}
