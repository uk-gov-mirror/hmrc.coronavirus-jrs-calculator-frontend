/*
 * Copyright 2020 HM Revenue & Customs
 *
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
