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
import config.FrontendAppConfig
import config.featureSwitch.ExtensionTwoNewStarterFlow
import models.PayMethod.{Regular, Variable}
import models.requests.DataRequest
import models.{EmployeeRTISubmission, EmployeeStarted, RegularLengthEmployed, UserAnswers}
import pages.{ClaimPeriodStartPage, EmployeeRTISubmissionPage, EmployeeStartDatePage, EmployeeStartedPage, OnPayrollBefore30thOct2020Page, PayMethodPage, RegularLengthEmployedPage}
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.play.test.LogCapturing
import utils.LocalDateHelpers.{feb1st2020, july1st2020, nov1st2020}

class EmployeeTypeUtilSpec extends SpecBase with EmployeeTypeUtil with LogCapturing {

  val defaultResult: String                     = "Default"
  val regularPayEmployeeResult: Option[String]  = Some("Regular default")
  val variablePayEmployeeResult: Option[String] = Some("Variable default")
  val type1EmployeeResult: Option[String]       = Some("Type 1")
  val type2aEmployeeResult: Option[String]      = Some("Type 2a")
  val type2bEmployeeResult: Option[String]      = Some("Type 2b")
  val type3EmployeeResult: Option[String]       = Some("Type 3")
  val type4EmployeeResult: Option[String]       = Some("Type 4")
  val type5aEmployeeResult: Option[String]      = Some("Type 5a")
  val type5bEmployeeResult: Option[String]      = Some("Type 5b")

  implicit val appConfig = frontendAppConfig

  def actualRegularPayResolverResult()(implicit request: DataRequest[_], appConfig: FrontendAppConfig): Option[String] =
    regularPayResolver[String](type1EmployeeResult, type2aEmployeeResult, type2bEmployeeResult)(request, appConfig)

  def actualVariablePayResolverResult()(implicit request: DataRequest[_], appConfig: FrontendAppConfig): Option[String] =
    variablePayResolver[String](type3EmployeeResult, type4EmployeeResult, type5aEmployeeResult, type5bEmployeeResult)(request, appConfig)

  def actualEmployeeTypeResolverResult()(implicit request: DataRequest[_], appConfig: FrontendAppConfig): String =
    employeeTypeResolver[String](
      defaultResult,
      regularPayEmployeeResult,
      variablePayEmployeeResult,
      type1EmployeeResult,
      type2aEmployeeResult,
      type2bEmployeeResult,
      type3EmployeeResult,
      type4EmployeeResult,
      type5aEmployeeResult,
      type5bEmployeeResult
    )(request, appConfig)

  "regularPayResolver" when {

    "user was on payroll before 19th March" must {

      "return type1EmployeeResult" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(RegularLengthEmployedPage, RegularLengthEmployed.Yes)
          .success
          .value
        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        actualRegularPayResolverResult mustBe type1EmployeeResult
      }
    }

    "user was not on payroll before 19th March" when {

      "user was on payroll before 30th October" must {

        "return type2aEmployeeResult" in {

          enable(ExtensionTwoNewStarterFlow)

          val userAnswers = UserAnswers(userAnswersId)
            .set(RegularLengthEmployedPage, RegularLengthEmployed.No)
            .success
            .value
            .set(OnPayrollBefore30thOct2020Page, true)
            .success
            .value

          implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

          actualRegularPayResolverResult mustBe type2aEmployeeResult
        }
      }

      "user was not on payroll before 30th October" must {

        "return type2bEmployeeResult" in {

          enable(ExtensionTwoNewStarterFlow)

          val userAnswers = UserAnswers(userAnswersId)
            .set(RegularLengthEmployedPage, RegularLengthEmployed.No)
            .success
            .value
            .set(OnPayrollBefore30thOct2020Page, false)
            .success
            .value

          implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

          actualRegularPayResolverResult mustBe type2bEmployeeResult
        }
      }

      "user did not answer payroll before 30th October" when {

        "ExtensionTwoNewStarterFlow is enabled" must {

          "return None" in {

            enable(ExtensionTwoNewStarterFlow)

            val userAnswers = UserAnswers(userAnswersId)
              .set(RegularLengthEmployedPage, RegularLengthEmployed.No)
              .success
              .value

            implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

            actualRegularPayResolverResult() mustBe None
          }
        }

        "ExtensionTwoNewStarterFlow is disabled" must {

          "return type2aEmployeeResult" in {

            disable(ExtensionTwoNewStarterFlow)

            val userAnswers = UserAnswers(userAnswersId)
              .set(RegularLengthEmployedPage, RegularLengthEmployed.No)
              .success
              .value

            implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

            actualRegularPayResolverResult mustBe type2aEmployeeResult
          }
        }
      }
    }
  }

  "variable pay resolver" when {

    "employee started before 1 Feb 2019" must {

      "return type 3 employee" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(EmployeeStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
          .success
          .value
        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        actualVariablePayResolverResult() mustBe type3EmployeeResult
      }
    }

    "employee started after 1 Feb 2019" when {

      "employee started before 1 Feb 2020" must {

        "return type 4 employee" in {

          val userAnswers = UserAnswers(userAnswersId)
            .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
            .success
            .value
            .set(EmployeeStartDatePage, feb1st2020.minusDays(1))
            .success
            .value
          implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

          actualVariablePayResolverResult() mustBe type4EmployeeResult
        }
      }

      "employee was on payroll before 19 March 2020" must {

        "return type 4 employee" in {

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

          actualVariablePayResolverResult() mustBe type4EmployeeResult
        }
      }

      "employee was on payroll before 30 October 2020" must {

        "return type 5a employee" in {

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
          implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

          actualVariablePayResolverResult() mustBe type5aEmployeeResult
        }
      }

      "employee was not on payroll before 30 October 2020" must {

        "return type 5b employee" in {

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
          implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

          actualVariablePayResolverResult() mustBe type5bEmployeeResult
        }
      }

      "no answer is given to on payroll before 30 October 2020" when {

        "ExtensionTwoNewStarterFlow is enabled" must {

          "journey is pre November1" must {

            "return type 1 employee result" in {

              enable(ExtensionTwoNewStarterFlow)

              val userAnswers = UserAnswers(userAnswersId)
                .set(ClaimPeriodStartPage, nov1st2020.minusDays(1))
                .success
                .value
                .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
                .success
                .value
                .set(EmployeeStartDatePage, feb1st2020.plusDays(1))
                .success
                .value

              implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

              actualRegularPayResolverResult() mustBe type1EmployeeResult
            }
          }

          "journey is not phase 1" must {

            "return type 1 employee result" in {

              enable(ExtensionTwoNewStarterFlow)

              val userAnswers = UserAnswers(userAnswersId)
                .set(ClaimPeriodStartPage, nov1st2020)
                .success
                .value
                .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
                .success
                .value
                .set(EmployeeStartDatePage, feb1st2020.plusDays(1))
                .success
                .value

              implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

              actualRegularPayResolverResult() mustBe None
            }
          }
        }

        "ExtensionTwoNewStarterFlow is disabled" must {

          "return type 5a employee result" in {

            disable(ExtensionTwoNewStarterFlow)

            val userAnswers = UserAnswers(userAnswersId)
              .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
              .success
              .value
              .set(EmployeeStartDatePage, feb1st2020.plusDays(1))
              .success
              .value
            implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

            actualVariablePayResolverResult() mustBe type5aEmployeeResult
          }
        }
      }
    }
  }

  "employeeTypeResolver" when {

    "pay type is regular" when {

      "only the default result is provided" must {

        "return the default result" in {

          val userAnswers = UserAnswers(userAnswersId)
            .set(PayMethodPage, Regular)
            .success
            .value

          implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

          employeeTypeResolver(defaultResult) mustBe defaultResult
        }
      }

      "no result is provided for a specific regular type" must {

        "return the default regular pay result" in {

          val userAnswers = UserAnswers(userAnswersId)
            .set(PayMethodPage, Regular)
            .success
            .value

          implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

          employeeTypeResolver(defaultResult, regularPayEmployeeResult) mustBe regularPayEmployeeResult.get
        }
      }

      "the regularPayResolver determines it is a type 2a result" must {

        "return the type 2a result" in {

          enable(ExtensionTwoNewStarterFlow)

          val userAnswers = UserAnswers(userAnswersId)
            .set(PayMethodPage, Regular)
            .success
            .value
            .set(RegularLengthEmployedPage, RegularLengthEmployed.No)
            .success
            .value
            .set(OnPayrollBefore30thOct2020Page, true)
            .success
            .value

          implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

          actualEmployeeTypeResolverResult() mustBe type2aEmployeeResult.get
        }
      }
    }

    "pay type is variable" when {

      "only the default result is provided" must {

        "return the default result" in {

          val userAnswers = UserAnswers(userAnswersId)
            .set(PayMethodPage, Variable)
            .success
            .value

          implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

          employeeTypeResolver(defaultResult) mustBe defaultResult
        }
      }

      "no result is provided for a specific variable type" must {

        "return the default variable pay result" in {

          val userAnswers = UserAnswers(userAnswersId)
            .set(PayMethodPage, Variable)
            .success
            .value

          implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

          employeeTypeResolver(defaultResult, regularPayEmployeeResult, variablePayEmployeeResult) mustBe variablePayEmployeeResult.get
        }
      }

      "the variablePayResolver determines it is a type 4 result" must {

        "return the type 4 result" in {

          enable(ExtensionTwoNewStarterFlow)

          val userAnswers = UserAnswers(userAnswersId)
            .set(PayMethodPage, Variable)
            .success
            .value
            .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
            .success
            .value
            .set(EmployeeStartDatePage, feb1st2020.minusDays(1))
            .success
            .value
          implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

          actualEmployeeTypeResolverResult() mustBe type4EmployeeResult.get
        }
      }
    }

    "no answer is given to PayMethodPage" must {

      "return ise and throw pagerduty" in {

        val userAnswers = UserAnswers(userAnswersId)

        implicit val request: DataRequest[_] = DataRequest(fakeDataRequest, userAnswers.id, userAnswers)

        withCaptureOfLoggingFrom(PagerDutyHelper.logger) { logs =>
          intercept[InternalServerException] {
            actualEmployeeTypeResolverResult()
            logs.exists(_.getMessage == s"${PagerDutyHelper.PagerDutyKeys.EMPLOYEE_TYPE_COULD_NOT_BE_RESOLVED} " +
              s"[EmployeeTypeService][employeeTypeResolver] no valid answer for PayMethodPage") mustBe true
          }
        }
      }
    }
  }
}
