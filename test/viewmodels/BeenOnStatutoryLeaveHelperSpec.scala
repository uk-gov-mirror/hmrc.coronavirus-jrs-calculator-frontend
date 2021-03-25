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

import assets.messages.BeenOnStatutoryLeaveMessages
import base.SpecBase
import models.requests.DataRequest
import models.{EmployeeRTISubmission, EmployeeStarted, UserAnswers}
import pages._
import play.api.Logger
import uk.gov.hmrc.play.test.LogCapturing
import utils.LocalDateHelpers
import utils.LocalDateHelpers._
import views.ViewUtils.dateToString

import java.time.LocalDate

class BeenOnStatutoryLeaveHelperSpec extends SpecBase with LocalDateHelpers with LogCapturing {

  val helper = app.injector.instanceOf[BeenOnStatutoryLeaveHelper]

  "boundaryStart" when {

    "employee is type 3" must {

      "return type3EmployeeResult" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(EmployeeStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
          .success
          .value
        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        withCaptureOfLoggingFrom(Logger) { logs =>
          helper.boundaryStart() mustBe dateToString(apr6th2019)
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
          helper.boundaryStart() mustBe BeenOnStatutoryLeaveMessages.dayEmploymentStarted
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
          .set(EmployeeStartDatePage, apr6th2020.minusDays(1))
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
          helper.boundaryStart() mustBe dateToString(apr6th2020)
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
          helper.boundaryStart() mustBe BeenOnStatutoryLeaveMessages.dayEmploymentStarted
          logs.map(_.getMessage).contains("[EmployeeTypeUtil][variablePayResolver] Type 5b Employee") mustBe true
        }
      }
    }

  }

  "boundaryEnd" when {

    "employee is type 3" must {

      "return type3EmployeeResult" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(EmployeeStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
          .success
          .value
          .set(FirstFurloughDatePage, apr5th2020.plusDays(2))
          .success
          .value
        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        withCaptureOfLoggingFrom(Logger) { logs =>
          helper.boundaryEnd() mustBe dateToString(apr5th2020)
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
          .set(EmployeeStartDatePage, feb1st2020.minusDays(1))
          .success
          .value
          .set(FirstFurloughDatePage, apr5th2020)
          .success
          .value
        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        withCaptureOfLoggingFrom(Logger) { logs =>
          helper.boundaryEnd() mustBe dateToString(apr5th2020.minusDays(1))
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
          .set(EmployeeStartDatePage, feb1st2020.plusDays(1))
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
          helper.boundaryEnd() mustBe dateToString(firstFurloughDateAns.minusDays(1))
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
          .set(EmployeeStartDatePage, feb1st2020.plusDays(1))
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
          helper.boundaryEnd() mustBe dateToString(firstFurloughDateAns.minusDays(1))
          logs.map(_.getMessage).contains("[EmployeeTypeUtil][variablePayResolver] Type 5b Employee") mustBe true
        }
      }
    }

  }

  "type5BoundaryStart" when {

    s"EmployeeStart is after $apr6th2020" must {

      "return dayEmploymentStarted text" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(EmployeeStartDatePage, apr6th2020.plusDays(1))
          .success
          .value
        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        helper.type5BoundaryStart mustBe Some(BeenOnStatutoryLeaveMessages.dayEmploymentStarted)
      }
    }

    s"EmployeeStart is equal to $apr6th2020" must {

      "return dayEmploymentStarted text" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(EmployeeStartDatePage, apr6th2020)
          .success
          .value
        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        helper.type5BoundaryStart mustBe Some(dateToString(apr6th2020))
      }
    }

    s"EmployeeStart is before to $apr6th2020" must {

      "return dayEmploymentStarted text" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(EmployeeStartDatePage, apr6th2020.minusDays(1))
          .success
          .value
        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        helper.type5BoundaryStart mustBe Some(dateToString(apr6th2020))
      }
    }

    s"no answer to EmployeeStartDatePage" must {

      "return None" in {

        val userAnswers                      = emptyUserAnswers
        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        helper.type5BoundaryStart mustBe None
      }
    }
  }

  "type3And4BoundaryEnd" when {

    s"dayBeforeFirstFurlough is before $apr5th2020" must {

      "return dayBeforeFirstFurlough" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(FirstFurloughDatePage, apr5th2020)
          .success
          .value
        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        helper.type3And4BoundaryEnd mustBe dateToString(apr5th2020.minusDays(1))
      }
    }

    s"dayBeforeFirstFurlough is equal to $apr5th2020" must {

      s"return $apr5th2020" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(FirstFurloughDatePage, apr5th2020.plusDays(1))
          .success
          .value
        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        helper.type3And4BoundaryEnd mustBe dateToString(apr5th2020)
      }
    }

    s"dayBeforeFirstFurlough is after to $apr5th2020" must {

      s"return $apr5th2020" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(FirstFurloughDatePage, apr5th2020.plusDays(2))
          .success
          .value
        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        helper.type3And4BoundaryEnd mustBe dateToString(apr5th2020)
      }
    }
  }

}
