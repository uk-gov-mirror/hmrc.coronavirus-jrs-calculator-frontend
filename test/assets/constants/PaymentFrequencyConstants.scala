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

package assets.constants

import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

object PaymentFrequencyConstants {

  def allRadioOptions(checked: Boolean = false)(implicit messages: Messages) = Seq(
    RadioItem(
      value = Some(Weekly.toString),
      content = Text(messages(s"payFrequency.${Weekly.toString}")),
      checked = checked,
      id = Some(Weekly.toString)
    ),
    RadioItem(
      value = Some(FortNightly.toString),
      content = Text(messages(s"payFrequency.${FortNightly.toString}")),
      checked = checked,
      id = Some(FortNightly.toString)
    ),
    RadioItem(
      value = Some(FourWeekly.toString),
      content = Text(messages(s"payFrequency.${FourWeekly.toString}")),
      checked = checked,
      id = Some(FourWeekly.toString)
    ),
    RadioItem(
      value = Some(Monthly.toString),
      content = Text(messages(s"payFrequency.${Monthly.toString}")),
      checked = checked,
      id = Some(Monthly.toString)
    )
  )

}
