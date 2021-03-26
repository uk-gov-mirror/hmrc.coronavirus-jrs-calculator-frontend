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

import cats.data.Validated.Valid
import config.FrontendAppConfig
import models.requests.DataRequest
import pages.EmployeeStartDatePage
import play.api.Logger
import play.api.i18n.Messages
import uk.gov.hmrc.http.InternalServerException
import utils.LocalDateHelpers._
import utils.{EmployeeTypeUtil, KeyDatesUtil}
import views.ViewUtils.dateToString

class BeenOnStatutoryLeaveHelper extends EmployeeTypeUtil with KeyDatesUtil {

  def boundaryStart()(implicit request: DataRequest[_], appConfig: FrontendAppConfig, messages: Messages): String =
    variablePayResolver[String](
      type3EmployeeResult = Some(dateToString(apr6th2019)),
      type4EmployeeResult = Some(messages("hasEmployeeBeenOnStatutoryLeave.dayEmploymentStarted")),
      type5aEmployeeResult = type5BoundaryStart(),
      type5bEmployeeResult = type5BoundaryStart()
    ).fold(
      throw new InternalServerException("[BeenOnStatutoryLeaveHelper][boundaryStart] failed to resolve employee type")
    )(identity)

  def boundaryEnd()(implicit request: DataRequest[_], appConfig: FrontendAppConfig, messages: Messages): String =
    variablePayResolver[String](
      type3EmployeeResult = Some(type3And4BoundaryEnd()),
      type4EmployeeResult = Some(type3And4BoundaryEnd()),
      type5aEmployeeResult = Some(dateToString(firstFurloughDate().minusDays(1))),
      type5bEmployeeResult = Some(dateToString(firstFurloughDate().minusDays(1)))
    ).fold(
      throw new InternalServerException("[BeenOnStatutoryLeaveHelper][boundaryEnd] failed to resolve employee type")
    )(identity)

  def type5BoundaryStart()(implicit request: DataRequest[_], messages: Messages): Option[String] =
    request.userAnswers.getV(EmployeeStartDatePage) match {
      case Valid(startDate) =>
        Logger.debug(s"[BeenOnStatutoryLeaveHelper][type5BoundaryStart] start date: $startDate")
        if (startDate.isAfter(apr6th2020)) {
          Some(messages("hasEmployeeBeenOnStatutoryLeave.dayEmploymentStarted"))
        } else {
          Some(dateToString(apr6th2020))
        }
      case _ =>
        Logger.debug("[BeenOnStatutoryLeaveHelper][type5BoundaryStart] no answer for EmployeeStartDatePage")
        None
    }

  def type3And4BoundaryEnd()(implicit request: DataRequest[_], messages: Messages): String = {
    val dayBeforeFirstFurlough = firstFurloughDate().minusDays(1)
    Logger.debug(s"[BeenOnStatutoryLeaveHelper][type3And4BoundaryEnd] dayBeforeFirstFurlough: $dayBeforeFirstFurlough")
    dateToString(earliestOf(apr5th2020, dayBeforeFirstFurlough))
  }
}
