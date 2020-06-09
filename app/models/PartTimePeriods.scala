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
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import views.ViewUtils._

object PartTimePeriods {

  def options(form: Form[_], periods: List[Periods])(implicit messages: Messages): Seq[CheckboxItem] = periods.zipWithIndex.map { value =>
    val periodEnd = value._1.period.end

    val content: Text = value._1 match {
      case fp: FullPeriod =>
        Text(messages("partTimePeriods.fullPeriod", dateToStringWithoutYear(fp.period.start), dateToString(fp.period.end)))
      case pp: PartialPeriod =>
        if(pp.partial.countDays == 1) {
          Text(messages("partTimePeriods.singleDay", dateToString(pp.partial.end)))
        } else {
          Text(messages("partTimePeriods.partialPeriod", dateToStringWithoutYear(pp.partial.start), dateToString(pp.partial.end)))
        }
    }

    CheckboxItem(
      name = Some(s"value[${value._2}]"),
      id = Some(s"part-time-period_${value._2.toString}"),
      value = periodEnd,
      content = content,
      checked = form.data.values.contains(periodEnd.toString)
    )
  }

}
