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

import cats.data.Validated.Valid
import config.FrontendAppConfig
import config.featureSwitch.{ExtensionTwoNewStarterFlow, FeatureSwitching}
import models.PayMethod.{Regular, Variable}
import models.requests.DataRequest
import models.{EmployeeRTISubmission, EmployeeStarted, RegularLengthEmployed}
import pages._
import play.api.Logger.logger
import uk.gov.hmrc.http.InternalServerException
import utils.LocalDateHelpers.{feb1st2020, july1st2020, nov1st2020}
import utils.PagerDutyHelper.PagerDutyKeys.EMPLOYEE_TYPE_COULD_NOT_BE_RESOLVED

trait EmployeeTypeUtil extends FeatureSwitching {

  def regularPayResolver[T](
    type1EmployeeResult: Option[T] = None,
    type2aEmployeeResult: Option[T] = None,
    type2bEmployeeResult: Option[T] = None)(implicit request: DataRequest[_], appConfig: FrontendAppConfig): Option[T] =
    (request.userAnswers.getV(RegularLengthEmployedPage), request.userAnswers.getV(ClaimPeriodStartPage)) match {
      case (Valid(RegularLengthEmployed.Yes), _) =>
        logger.debug("[EmployeeTypeUtil][regularPayResolver] Type 1 Employee")
        type1EmployeeResult
      case (Valid(RegularLengthEmployed.No), _) =>
        request.userAnswers.getV(OnPayrollBefore30thOct2020Page) match {
          case Valid(true) =>
            logger.debug("[EmployeeTypeUtil][regularPayResolver] Type 2a Employee")
            type2aEmployeeResult
          case Valid(false) =>
            logger.debug("[EmployeeTypeUtil][regularPayResolver] Type 2b Employee")
            type2bEmployeeResult
          case _ =>
            if (isEnabled(ExtensionTwoNewStarterFlow)) {
              val logMsg = "[EmployeeTypeService][regularPayResolver] no valid answer for OnPayrollBefore30thOct2020Page"
              logger.debug(logMsg)
              None
            } else {
              logger.debug("[EmployeeTypeUtil][regularPayResolver] Type 2a Employee - ExtensionTwoNewStarterFlow disabled")
              type2aEmployeeResult
            }
        }
      case (_, Valid(startDate)) if startDate.isBefore(nov1st2020) =>
        val logMsg = "[EmployeeTypeService][regularPayResolver] pre November journeys do not see RegularLengthEmployedPage"
        logger.debug(logMsg)
        type1EmployeeResult

      case _ =>
        val logMsg = "[EmployeeTypeService][regularPayResolver] no valid answer for RegularLengthEmployedPage"
        logger.debug(logMsg)
        None
    }

  def variablePayResolver[T](
    type3EmployeeResult: Option[T] = None,
    type4EmployeeResult: Option[T] = None,
    type5aEmployeeResult: Option[T] = None,
    type5bEmployeeResult: Option[T] = None)(implicit request: DataRequest[_], appConfig: FrontendAppConfig): Option[T] =
    request.userAnswers.getV(EmployeeStartedPage) match {
      case Valid(EmployeeStarted.OnOrBefore1Feb2019) => type3EmployeeResult
      case Valid(EmployeeStarted.After1Feb2019) =>
        (request.userAnswers.getV(EmployeeStartDatePage),
         request.userAnswers.getV(EmployeeRTISubmissionPage),
         request.userAnswers.getV(OnPayrollBefore30thOct2020Page)) match {
          case (Valid(startDate), rtiAns, _) if startDate.isBefore(feb1st2020) | rtiAns == Valid(EmployeeRTISubmission.Yes) =>
            logger.debug("[EmployeeTypeUtil][variablePayResolver] Type 4 Employee")
            type4EmployeeResult
          case (Valid(_), _, Valid(true)) =>
            logger.debug("[EmployeeTypeUtil][variablePayResolver] Type 5a Employee")
            type5aEmployeeResult
          case (Valid(_), _, Valid(false)) =>
            logger.debug("[EmployeeTypeUtil][variablePayResolver] Type 5b Employee")
            type5bEmployeeResult
          case _ =>
            if (isEnabled(ExtensionTwoNewStarterFlow)) {
              val logMsg = "[EmployeeTypeService][variablePayResolver] variable pay employee type cannot be resolved"
              logger.warn(logMsg)
              None
            } else {
              logger.debug("[EmployeeTypeUtil][variablePayResolver] Type 5a Employee - ExtensionTwoNewStarterFlow disabled")
              type5aEmployeeResult
            }
        }
      case _ =>
        val logMsg = "[EmployeeTypeService][variablePayResolver] no valid answer for EmployeeStartedPage"
        logger.debug(logMsg)
        None
    }

  //noinspection ScalaStyle
  def employeeTypeResolver[T](defaultResult: T,
                              regularPayEmployeeResult: Option[T] = None,
                              variablePayEmployeeResult: Option[T] = None,
                              type1EmployeeResult: Option[T] = None,
                              type2aEmployeeResult: Option[T] = None,
                              type2bEmployeeResult: Option[T] = None,
                              type3EmployeeResult: Option[T] = None,
                              type4EmployeeResult: Option[T] = None,
                              type5aEmployeeResult: Option[T] = None,
                              type5bEmployeeResult: Option[T] = None)(implicit request: DataRequest[_], appConfig: FrontendAppConfig): T = {
    val defaultRegularResult: T  = regularPayEmployeeResult.getOrElse(defaultResult)
    val defaultVariableResult: T = variablePayEmployeeResult.getOrElse(defaultResult)
    request.userAnswers.getV(PayMethodPage) match {
      case Valid(Regular) =>
        regularPayResolver(type1EmployeeResult, type2aEmployeeResult, type2bEmployeeResult).getOrElse(defaultRegularResult)
      case Valid(Variable) =>
        variablePayResolver(type3EmployeeResult, type4EmployeeResult, type5aEmployeeResult, type5bEmployeeResult).getOrElse(
          defaultVariableResult)
      case _ =>
        val logMsg = "[EmployeeTypeService][employeeTypeResolver] no valid answer for PayMethodPage"
        PagerDutyHelper.alert(EMPLOYEE_TYPE_COULD_NOT_BE_RESOLVED, Some(logMsg))
        throw new InternalServerException(logMsg)
    }
  }
}
