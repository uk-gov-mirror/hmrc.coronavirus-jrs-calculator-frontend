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

package forms

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.Messages
import utils.LocalDateHelpers._
import views.ViewUtils

class EmployeeStartDateFormProvider @Inject() extends Mappings {

  val feb2nd2019: LocalDate = LocalDate.of(2019, 2, 2)
  val march19th2020 = LocalDate.of(2020, 3, 19)
  val oct30th2020 = LocalDate.of(2020, 10, 30)
  val nov1st2020 = LocalDate.of(2020, 11, 1)

  def apply(furloughStart: LocalDate, claimStart: LocalDate)(implicit messages: Messages): Form[LocalDate] = {

    val cutoff = if (claimStart.isEqualOrAfter(nov1st2020)) oct30th2020 else march19th2020

    val maxValidStart = earliestOf(furloughStart.minusDays(1), cutoff)

    Form(
      "value" -> localDate(invalidKey = "employeeStartDate.error.invalid")
        .verifying(minDate(feb2nd2019, "employeeStartDate.error.min", ViewUtils.dateToString(feb2nd2019)))
        .verifying(maxDate(maxValidStart, "employeeStartDate.error.max", ViewUtils.dateToString(cutoff)))
    )
  }
}
