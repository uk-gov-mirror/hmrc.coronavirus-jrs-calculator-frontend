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

import java.time.LocalDate

import cats.data.{NonEmptyChain, Validated}
import cats.data.Validated.Valid
import config.FrontendAppConfig
import models.{AnswerValidation, EmployeeRTISubmission}
import models.EmployeeRTISubmission.No
import models.UserAnswers.AnswerV
import models.requests.DataRequest
import pages._

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

}
