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

package utils

import base.SpecBase
import models.UserAnswers
import models.requests.DataRequest
import pages.{FirstFurloughDatePage, FurloughStartDatePage}
import uk.gov.hmrc.http.InternalServerException

import java.time.LocalDate

class KeyDatesUtilSpec extends SpecBase with KeyDatesUtil {

  "firstFurloughDate" when {

    val firstFurloughDateAns = LocalDate.parse("2020-09-20")
    val furloughStartDate    = LocalDate.parse("2021-01-13")

    "there is an answer to FirstFurloughDatePage" must {

      "return the answer to FirstFurloughDatePage" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(FirstFurloughDatePage, firstFurloughDateAns)
          .success
          .value
          .set(FurloughStartDatePage, furloughStartDate)
          .success
          .value
        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        firstFurloughDate() mustBe firstFurloughDateAns
      }
    }

    "there is no answer to FirstFurloughDatePage" must {

      "return the answer to FurloughStartDatePage" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(FirstFurloughDatePage, furloughStartDate)
          .success
          .value
        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        firstFurloughDate() mustBe furloughStartDate
      }
    }

    "there is no answer to either" must {

      "throw ISE" in {

        val userAnswers = emptyUserAnswers

        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        intercept[InternalServerException] {
          firstFurloughDate()
        }
      }
    }
  }
}
