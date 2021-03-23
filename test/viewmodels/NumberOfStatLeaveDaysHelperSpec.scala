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

package viewmodels

import java.time.LocalDate

import models.{EmployeeRTISubmission, EmployeeStarted, UserAnswers}
import utils.LocalDateHelpers._
import models.requests.DataRequest
import pages._
import play.api.Logger
import utils.LocalDateHelpers._
import base.SpecBase
import uk.gov.hmrc.play.test.LogCapturing
import utils.LocalDateHelpers

class NumberOfStatLeaveDaysHelperSpec extends SpecBase with LocalDateHelpers with LogCapturing {

  val helper = app.injector.instanceOf[NumberOfStatLeaveDaysHelper]

  ".boundaryStartDate()" when {

    "employee is type 3" must {

      "return type3EmployeeResult" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(EmployeeStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
          .success
          .value
          .set(EmployeeStartDatePage, apr5th2020.plusDays(1))
          .success
          .value

        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        withCaptureOfLoggingFrom(Logger) { logs =>
          helper.boundaryStartDate() mustBe apr5th2020.plusDays(1)
          logs.map(_.getMessage).contains("[EmployeeTypeUtil][variablePayResolver] Type 3 Employee") mustBe true
        }
      }
    }

    "employee is type 4" must {

      "return type4EmployeeResult" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
          .success
          .value
          .set(EmployeeStartDatePage, feb1st2020.plusDays(1))
          .success
          .value
          .set(EmployeeRTISubmissionPage, EmployeeRTISubmission.Yes)
          .success
          .value
        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        withCaptureOfLoggingFrom(Logger) { logs =>
          helper.boundaryStartDate() mustBe feb1st2020.plusDays(1)
          logs.map(_.getMessage).contains("[EmployeeTypeUtil][variablePayResolver] Type 4 Employee") mustBe true
        }
      }
    }

    "employee is type 5a" must {

      "return type5aEmployeeResult" in {

        val firstFurloughDateAns = LocalDate.parse("2020-11-01")
        val furloughStartDate    = LocalDate.parse("2021-01-13")

        val userAnswers = UserAnswers(userAnswersId)
          .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
          .success
          .value
          .set(EmployeeStartDatePage, apr6th2020)
          .success
          .value
          .set(OnPayrollBefore30thOct2020Page, true)
          .success
          .value
          .set(FirstFurloughDatePage, firstFurloughDateAns)
          .success
          .value
          .set(FurloughStartDatePage, furloughStartDate)
          .success
          .value
        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        withCaptureOfLoggingFrom(Logger) { logs =>
          helper.boundaryStartDate() mustBe apr6th2020
          logs.map(_.getMessage).contains("[EmployeeTypeUtil][variablePayResolver] Type 5a Employee") mustBe true
        }
      }
    }

    "employee is type 5b" must {

      "return type5bEmployeeResult" in {

        val firstFurloughDateAns = LocalDate.parse("2021-05-01")
        val furloughStartDate    = LocalDate.parse("2021-05-13")

        val userAnswers = UserAnswers(userAnswersId)
          .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
          .success
          .value
          .set(EmployeeStartDatePage, apr6th2020.plusDays(1))
          .success
          .value
          .set(OnPayrollBefore30thOct2020Page, false)
          .success
          .value
          .set(FirstFurloughDatePage, firstFurloughDateAns)
          .success
          .value
          .set(FurloughStartDatePage, furloughStartDate)
          .success
          .value
        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        withCaptureOfLoggingFrom(Logger) { logs =>
          helper.boundaryStartDate() mustBe apr6th2020.plusDays(1)
          logs.map(_.getMessage).contains("[EmployeeTypeUtil][variablePayResolver] Type 5b Employee") mustBe true
        }
      }
    }

  }

  ".boundaryEndDate()" when {

    "employee is type 3" when {

      "only the FirstFurloughDatePage has been answered" when {

        "the first furlough date == apr1st2020, and earlier than apr5th2020" should {

          "return apr1st2020" in {

            val apr1st2020 = LocalDate.of(2020, 4, 1)

            val userAnswers = UserAnswers(userAnswersId)
              .set(FirstFurloughDatePage, apr1st2020)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
              .success
              .value

            implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

            withCaptureOfLoggingFrom(Logger) { logs =>
              helper.boundaryEndDate() mustBe apr1st2020.minusDays(1)
              logs.map(_.getMessage).contains("[EmployeeTypeUtil][variablePayResolver] Type 3 Employee") mustBe true
            }
          }
        }

        "the first furlough date == apr10th2020, and after than apr5th2020" should {

          "return apr5th2020" in {

            val apr1st2020 = LocalDate.of(2020, 4, 1)

            val userAnswers = UserAnswers(userAnswersId)
              .set(FirstFurloughDatePage, apr1st2020)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
              .success
              .value

            implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

            withCaptureOfLoggingFrom(Logger) { logs =>
              helper.boundaryEndDate() mustBe apr1st2020.minusDays(1)
              logs.map(_.getMessage).contains("[EmployeeTypeUtil][variablePayResolver] Type 3 Employee") mustBe true
            }
          }
        }

      }

    }
  }

}
