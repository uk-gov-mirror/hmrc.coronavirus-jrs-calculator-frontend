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

import cats.data.Validated.Valid
import config.FrontendAppConfig
import models.EmployeeRTISubmission
import models.EmployeeRTISubmission.No
import models.UserAnswers.AnswerV
import models.requests.DataRequest
import pages.{EmployeeRTISubmissionPage, EmployeeStartDatePage, PreviousFurloughPeriodsPage}

class EmployeeTypeService() {

  def isType5NewStarter()(implicit request: DataRequest[_], appConfig: FrontendAppConfig): Boolean = {

    val startDate: AnswerV[LocalDate] = request.userAnswers.getV(EmployeeStartDatePage)
    val employeeRTI: AnswerV[EmployeeRTISubmission] = request.userAnswers.getV(EmployeeRTISubmissionPage)
    val extensionHasMultipleFurloughs: AnswerV[Boolean] = request.userAnswers.getV(PreviousFurloughPeriodsPage)

    (startDate, employeeRTI, extensionHasMultipleFurloughs) match {
      case (Valid(date), _, Valid(_)) if date.isAfter(appConfig.employeeStartDatePostCovid) => true
      case (Valid(_), Valid(No), Valid(_))                                                  => true
      case _                                                                                => false
    }
  }

}
