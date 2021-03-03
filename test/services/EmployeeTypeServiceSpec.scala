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

package services

import java.time._

import base.{CoreTestDataBuilder, SpecBase}
import models.EmployeeRTISubmission.{No, Yes}
import pages._

class EmployeeTypeServiceSpec extends SpecBase with CoreTestDataBuilder {

  val service = new EmployeeTypeService()

  "EmployeeTypeServiceSpec" when {

    "calling the isType5NewStarter" when {

      "all three pages (EmployeeStartDatePage, EmployeeRTISubmissionPage = No, PreviousFurloughPeriodsPage) are answered " should {

        "return true" in {

          val userAnswers = emptyUserAnswers
            .set(EmployeeStartDatePage, LocalDate.of(2020, 1, 1))
            .success
            .value
            .set(EmployeeRTISubmissionPage, No)
            .success
            .value
            .set(PreviousFurloughPeriodsPage, true)
            .success
            .value

          val actual = service.isType5NewStarter()(fakeDataRequest(userAnswers), frontendAppConfig)
          val expected = true

          actual mustBe expected
        }
      }

      "EmployeeStartDatePage = after 19th March 2020, EmployeeRTISubmissionPage = not answered, PreviousFurloughPeriodsPage = true" should {

        "return true" in {

          val userAnswers = emptyUserAnswers
            .set(EmployeeStartDatePage, LocalDate.of(2020, 3, 20))
            .success
            .value
            .set(PreviousFurloughPeriodsPage, true)
            .success
            .value

          val actual = service.isType5NewStarter()(fakeDataRequest(userAnswers), frontendAppConfig)
          val expected = true

          actual mustBe expected
        }
      }

      "EmployeeStartDatePage = defined, EmployeeRTISubmissionPage = Yes, PreviousFurloughPeriodsPage = true" should {

        "return false" in {

          val userAnswers = emptyUserAnswers
            .set(EmployeeStartDatePage, LocalDate.of(2020, 1, 1))
            .success
            .value
            .set(EmployeeRTISubmissionPage, Yes)
            .success
            .value
            .set(PreviousFurloughPeriodsPage, true)
            .success
            .value

          val actual = service.isType5NewStarter()(fakeDataRequest(userAnswers), frontendAppConfig)
          val expected = false

          actual mustBe expected
        }
      }

      "EmployeeStartDatePage = before 19th March, EmployeeRTISubmissionPage = not answered, PreviousFurloughPeriodsPage = true" should {

        "return false" in {

          val userAnswers = emptyUserAnswers
            .set(EmployeeStartDatePage, LocalDate.of(2020, 1, 1))
            .success
            .value
            .set(PreviousFurloughPeriodsPage, true)
            .success
            .value

          val actual = service.isType5NewStarter()(fakeDataRequest(userAnswers), frontendAppConfig)
          val expected = false

          actual mustBe expected
        }
      }
    }
  }

}
