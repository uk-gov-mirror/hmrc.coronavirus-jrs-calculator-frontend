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

import cats.data.Validated.Valid
import cats.data.{NonEmptyChain, Validated}
import config.FrontendAppConfig
import models.EmployeeRTISubmission.No
import models.EmployeeType.Type1Employee
import models.PayMethod._
import models.UserAnswers.AnswerV
import models.requests.DataRequest
import models.{AnswerValidation, EmployeeRTISubmission, EmployeeStarted, RegularLengthEmployed}
import pages._
import play.api.Logger.logger
import uk.gov.hmrc.http.InternalServerException
import utils.LocalDateHelpers.feb1st2020

class EmployeeTypeService() {

  def isType5NewStarter()(implicit request: DataRequest[_], appConfig: FrontendAppConfig): Boolean = {

    val employeeStartDatePostCovid: Validated[NonEmptyChain[AnswerValidation], Boolean] = {
      request.userAnswers.getV(EmployeeStartDatePage).map(startDate => startDate.isAfter(appConfig.employeeStartDatePostCovid))
    }

    val employeeRTIAnswer: AnswerV[EmployeeRTISubmission] = request.userAnswers.getV(EmployeeRTISubmissionPage)

    (employeeStartDatePostCovid, employeeRTIAnswer) match {
      case (Valid(true), _)          => true
      case (Valid(false), Valid(No)) => true
      case _                         => false
    }
  }

  def regularPayResolver[T](type1EmployeeResult: Option[T],
                            type2aEmployeeResult: Option[T],
                            type2bEmployeeResult: Option[T]
                           )(implicit request: DataRequest[_]): Option[T] =
    request.userAnswers.getV(RegularLengthEmployedPage) match {
      case Valid(RegularLengthEmployed.Yes) => type1EmployeeResult
      case Valid(RegularLengthEmployed.No) =>
        request.userAnswers.getV(OnPayrollBefore30thOct2020Page) match {
          case Valid(true) => type2aEmployeeResult
          case Valid(false) => type2bEmployeeResult
        }
      case _ =>
        val logMsg = "[EmployeeTypeService][regularPayResolver] no valid answer for EmployeeStartDatePage"
        logger.warn(logMsg)
        throw new InternalServerException(logMsg)
    }

  def variablePayResolver[T](type3EmployeeResult: Option[T],
                             type4EmployeeResult: Option[T],
                             type5General: Option[T]
                             type5aEmployeeResult: Option[T],
                             type5bEmployeeResult: Option[T]
                            )(implicit request: DataRequest[_]): Option[T] = {
    request.userAnswers.getV(EmployeeStartedPage) match {
      case Valid(EmployeeStarted.OnOrBefore1Feb2019) => type3EmployeeResult
      case Valid(EmployeeStarted.After1Feb2019) =>
        (request.userAnswers.getV(EmployeeStartDatePage),
          request.userAnswers.getV(EmployeeRTISubmissionPage),
        request.userAnswers.getV()
        ) match {
          case (Valid(date), _) if date.isAfter(Type1Employee.referencePayPeriodCutoff) =>
            //5a
            //5b
            ???
          case (Valid(date), Valid(rtiAns)) if date.isBefore(feb1st2020) | rtiAns == EmployeeRTISubmission.Yes =>
            type4EmployeeResult
        }
      case _ =>
        val logMsg = "[EmployeeTypeService][variablePayResolver] no valid answer for EmployeeStartedPage"
        logger.warn(logMsg)
        throw new InternalServerException(logMsg)
    }
  }

  //noinspection ScalaStyle
  def employeeTypeResolver[T](defaultResult: T,
                              regularPayEmployeeResult: Option[T],
                              variablePayEmployeeResult: Option[T],
                              preCovidStarterResult: Option[T],
                              extension1NewStarterResult: Option[T],
                              extension2NewStarterResult: Option[T],
                              type1EmployeeResult: Option[T],
                              type2aEmployeeResult: Option[T],
                              type2bEmployeeResult: Option[T],
                              type3EmployeeResult: Option[T],
                              type4EmployeeResult: Option[T],
                              type5aEmployeeResult: Option[T],
                              type5bEmployeeResult: Option[T])(implicit request: DataRequest[_]): T =
    request.userAnswers.getV(PayMethodPage) match {
      case Valid(Regular) => ???

      case Valid(Variable) => ???
      case _ =>
        val logMsg = "[EmployeeTypeService][employeeTypeResolver] no valid answer for PayMethodPage"
        logger.warn(logMsg)
        throw new InternalServerException(logMsg)
    }

}
