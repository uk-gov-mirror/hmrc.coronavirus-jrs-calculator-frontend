/*
 * Copyright 2020 HM Revenue & Customs
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

package models

import java.time.LocalDate

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait FurloughStatus

object FurloughStatus extends Enumerable.Implicits {

  case object FurloughEnded extends WithName("ended") with FurloughStatus
  case object FlexibleFurlough extends WithName("flexible") with FurloughStatus
  case object FurloughOngoing extends WithName("ongoing") with FurloughStatus

  val values: Seq[FurloughStatus] = Seq(
    FurloughEnded,
    FlexibleFurlough,
    FurloughOngoing
  )

  def conditionalValues(claimStart: LocalDate): Seq[FurloughStatus] =
    if (claimStart.getMonthValue < 7) {
      Seq(
        FurloughEnded,
        FurloughOngoing
      )
    } else {
      Seq(
        FurloughOngoing,
        FlexibleFurlough,
        FurloughEnded
      )
    }

  def options(form: Form[_], claimStart: LocalDate)(implicit messages: Messages): Seq[RadioItem] = conditionalValues(claimStart).map {
    value =>
      RadioItem(
        value = Some(value.toString),
        content = Text(messages(conditionalContent(claimStart, value))),
        checked = form("value").value.contains(value.toString)
      )
  }

  private def conditionalContent(claimStart: LocalDate, value: FurloughStatus): String =
    if (claimStart.getMonthValue < 7) {
      s"furloughOngoing.${value.toString}"
    } else {
      s"furloughOngoing.phaseTwo.${value.toString}"
    }

  implicit val enumerable: Enumerable[FurloughStatus] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
