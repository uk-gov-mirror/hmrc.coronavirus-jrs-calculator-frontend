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

import cats.data.Writer
import config.FrontendAppConfig
import models.requests.DataRequest
import pages.EmployeeStartDatePage
import play.api.Logger
import play.api.i18n.Messages
import uk.gov.hmrc.http.InternalServerException
import utils.LocalDateHelpers._
import utils.{EmployeeTypeUtil, KeyDatesUtil}
import views.ViewUtils.dateToString

class NumberOfStatLeaveDaysHelper extends EmployeeTypeUtil with KeyDatesUtil {

  def boundaryStartDate()(implicit request: DataRequest[_], appConfig: FrontendAppConfig, messages: Messages): Writer[String, LocalDate] = {

    val employeeStartDate: Option[LocalDate] = request.userAnswers.getV(EmployeeStartDatePage).toOption

    def latestOfEmployeeStartDateOrDefaultDate(defaultDate: LocalDate): Option[LocalDate] = {
      Logger.debug(
        s"[NumberOfDaysOnStatLeaveHelper][latestOfEmployeeStartDateOrDefaultDate] " +
          s"\n - defaultDate: $defaultDate" +
          s"\n - employeeStartDate: $employeeStartDate")
      employeeStartDate.map(employeeStartDate => latestOf(defaultDate, employeeStartDate))
    }

    lazy val type4Date: Option[Writer[String, LocalDate]] = latestOfEmployeeStartDateOrDefaultDate(apr6th2019).map((startDate: LocalDate) =>
      Writer(l = messages("hasEmployeeBeenOnStatutoryLeave.dayEmploymentStarted"), v = startDate))

    lazy val type5Date: Option[Writer[String, LocalDate]] = latestOfEmployeeStartDateOrDefaultDate(apr6th2020).map(
      (startDate: LocalDate) =>
        Writer(
          l = if (startDate.isAfter(apr6th2020)) {
            messages("hasEmployeeBeenOnStatutoryLeave.dayEmploymentStarted")
          } else {
            dateToString(apr6th2020)
          },
          v = startDate
      ))

    variablePayResolver[Writer[String, LocalDate]](
      type3EmployeeResult = Option(Writer(dateToString(apr6th2019), apr6th2019)),
      type4EmployeeResult = type4Date,
      type5aEmployeeResult = type5Date,
      type5bEmployeeResult = type5Date
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
