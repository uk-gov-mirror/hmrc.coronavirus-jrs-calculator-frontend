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

package forms

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import utils.ImplicitDateFormatter
import utils.LocalDateHelpers.earliestOf

class EmployeeStartDateFormProvider @Inject() extends Mappings with ImplicitDateFormatter {

  val feb2nd2019: LocalDate = LocalDate.of(2019, 2, 2)
  val march19th2020 = LocalDate.of(2020, 3, 19)

  def apply(furloughStart: LocalDate): Form[LocalDate] = {

    val maxValidStart = earliestOf(furloughStart.minusDays(1), march19th2020)

    Form(
      "value" -> localDate(
        invalidKey = "employeeStartDate.error.invalid",
        allRequiredKey = "employeeStartDate.error.required.all",
        twoRequiredKey = "employeeStartDate.error.required.two",
        requiredKey = "employeeStartDate.error.required"
      ).verifying(minDate(feb2nd2019, "employeeStartDate.error.min", dateToString(feb2nd2019)))
        .verifying(maxDate(maxValidStart, "employeeStartDate.error.max"))
    )
  }
}
