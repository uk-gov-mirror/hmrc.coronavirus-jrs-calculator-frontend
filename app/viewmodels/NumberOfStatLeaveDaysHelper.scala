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

import config.FrontendAppConfig
import models.requests.DataRequest
import pages.EmployeeStartDatePage
import play.api.Logger
import play.api.i18n.Messages
import uk.gov.hmrc.http.InternalServerException
import utils.LocalDateHelpers._
import utils.{EmployeeTypeUtil, KeyDatesUtil}

class NumberOfStatLeaveDaysHelper extends EmployeeTypeUtil with KeyDatesUtil {

  def boundaryStartDate()(implicit request: DataRequest[_], appConfig: FrontendAppConfig, messages: Messages): LocalDate = {

    val employeeStartDate: Option[LocalDate] = request.userAnswers.getV(EmployeeStartDatePage).toOption

    def latestOfEmployeeStartDateOrDefaultDate(defaultDate: LocalDate): Option[LocalDate] = {
      Logger.debug(
        s"[NumberOfDaysOnStatLeaveHelper][latestOfEmployeeStartDateOrDefaultDate] " +
          s"\n - defaultDate: $defaultDate" +
          s"\n - employeeStartDate: $employeeStartDate")
      employeeStartDate.map(employeeStartDate => latestOf(defaultDate, employeeStartDate))
    }

    variablePayResolver[LocalDate](
      type3EmployeeResult = Some(apr6th2019),
      type4EmployeeResult = latestOfEmployeeStartDateOrDefaultDate(apr6th2019),
      type5aEmployeeResult = latestOfEmployeeStartDateOrDefaultDate(apr6th2020),
      type5bEmployeeResult = latestOfEmployeeStartDateOrDefaultDate(apr6th2020)
    ).fold(
      throw new InternalServerException("[NumberOfDaysOnStatLeaveHelper][boundaryStartDate] failed to resolve employee type")
    )(identity)
  }

  def boundaryEndDate()(implicit request: DataRequest[_], appConfig: FrontendAppConfig, messages: Messages): LocalDate = {

    val dayBeforeFirstFurlough = firstFurloughDate.minusDays(1)

    variablePayResolver[LocalDate](
      type3EmployeeResult = Some(earliestOf(apr5th2020, dayBeforeFirstFurlough)),
      type4EmployeeResult = Some(earliestOf(apr5th2020, dayBeforeFirstFurlough)),
      type5aEmployeeResult = Some(dayBeforeFirstFurlough),
      type5bEmployeeResult = Some(dayBeforeFirstFurlough)
    ).fold(
      throw new InternalServerException("[NumberOfDaysOnStatLeaveHelper][boundaryEndDate] failed to resolve employee type")
    )(identity)
  }

}
