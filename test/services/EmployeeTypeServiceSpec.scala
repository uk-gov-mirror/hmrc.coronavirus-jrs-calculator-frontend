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

      "Employee Type is 5a New Starter Variable pay" when {

        "answers are (EmployeeStartDatePage = after 19th March 2020, EmployeeRTISubmissionPage = not answered)" should {

          "return true" in {

            val userAnswers = emptyUserAnswers
              .set(EmployeeStartDatePage, LocalDate.of(2020, 3, 20))
              .success
              .value

            val actual   = service.isType5NewStarter()(fakeDataRequest(userAnswers), frontendAppConfig)
            val expected = true

            actual mustBe expected
          }
        }

        "answers are (EmployeeStartDatePage = after 19th March 2020, EmployeeRTISubmissionPage = Yes)" should {

          "return true" in {

            val userAnswers = emptyUserAnswers
              .set(EmployeeStartDatePage, LocalDate.of(2020, 3, 20))
              .success
              .value
              .set(EmployeeRTISubmissionPage, Yes)
              .success
              .value

            val actual   = service.isType5NewStarter()(fakeDataRequest(userAnswers), frontendAppConfig)
            val expected = true

            actual mustBe expected
          }
        }

        "answers are (EmployeeStartDatePage = after 19th March 2020, EmployeeRTISubmissionPage = No)" should {

          "return true" in {

            val userAnswers = emptyUserAnswers
              .set(EmployeeStartDatePage, LocalDate.of(2020, 3, 20))
              .success
              .value
              .set(EmployeeRTISubmissionPage, No)
              .success
              .value

            val actual   = service.isType5NewStarter()(fakeDataRequest(userAnswers), frontendAppConfig)
            val expected = true

            actual mustBe expected
          }
        }

        "answers are (EmployeeStartDatePage = before 19th March 2020, EmployeeRTISubmissionPage = No)" should {

          "return true" in {

            val userAnswers = emptyUserAnswers
              .set(EmployeeStartDatePage, LocalDate.of(2020, 3, 20))
              .success
              .value
              .set(EmployeeRTISubmissionPage, No)
              .success
              .value

            val actual   = service.isType5NewStarter()(fakeDataRequest(userAnswers), frontendAppConfig)
            val expected = true

            actual mustBe expected
          }
        }
      }

      "Employee Type is Not a 5x New Starter Variable pay" when {

        "answers are (EmployeeStartDatePage = before 19th March 2020, EmployeeRTISubmissionPage = not answered)" should {

          "return false" in {

            val userAnswers = emptyUserAnswers
              .set(EmployeeStartDatePage, LocalDate.of(2020, 3, 18))
              .success
              .value

            val actual   = service.isType5NewStarter()(fakeDataRequest(userAnswers), frontendAppConfig)
            val expected = false

            actual mustBe expected
          }
        }

        "answers are (EmployeeStartDatePage = 19th March 2020, EmployeeRTISubmissionPage = not answered)" should {

          "return false" in {

            val userAnswers = emptyUserAnswers
              .set(EmployeeStartDatePage, LocalDate.of(2020, 3, 19))
              .success
              .value

            val actual   = service.isType5NewStarter()(fakeDataRequest(userAnswers), frontendAppConfig)
            val expected = false

            actual mustBe expected
          }
        }

        "answers are (EmployeeStartDatePage = before 19th March 2020, EmployeeRTISubmissionPage = Yes)" should {

          "return false" in {

            val userAnswers = emptyUserAnswers
              .set(EmployeeStartDatePage, LocalDate.of(2020, 3, 18))
              .success
              .value
              .set(EmployeeRTISubmissionPage, Yes)
              .success
              .value

            val actual   = service.isType5NewStarter()(fakeDataRequest(userAnswers), frontendAppConfig)
            val expected = false

            actual mustBe expected
          }
        }

      }
    }
  }

}
