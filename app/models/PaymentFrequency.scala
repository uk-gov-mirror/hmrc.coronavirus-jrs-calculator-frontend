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
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait FrequencyOperator
case object Multiplier extends FrequencyOperator
case object Divider extends FrequencyOperator

sealed trait PaymentFrequency

object PaymentFrequency extends Enumerable.Implicits {

  case object Weekly extends WithName("weekly") with PaymentFrequency
  case object FortNightly extends WithName("fortnightly") with PaymentFrequency
  case object FourWeekly extends WithName("fourweekly") with PaymentFrequency
  case object Monthly extends WithName("monthly") with PaymentFrequency

  val values: Set[PaymentFrequency] = Set(
    Weekly,
    FortNightly,
    FourWeekly,
    Monthly
  )

  val paymentFrequencyDays: Map[PaymentFrequency, Int] = Map(
    Weekly      -> 7,
    FortNightly -> 14,
    FourWeekly  -> 28
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.toSeq.map { value =>
    RadioItem(
      value = Some(value.toString),
      content = Text(messages(s"payFrequency.${value.toString}")),
      checked = form("value").value.contains(value.toString)
    )
  }

  implicit val enumerable: Enumerable[PaymentFrequency] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
