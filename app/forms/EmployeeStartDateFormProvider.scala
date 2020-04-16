/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid}
import utils.ImplicitDateFormatter

class EmployeeStartDateFormProvider @Inject() extends Mappings with ImplicitDateFormatter {

  val validStart = LocalDate.of(1900, 1, 1)

  def apply(): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey = "employeeStartDate.error.invalid",
        allRequiredKey = "employeeStartDate.error.required.all",
        twoRequiredKey = "employeeStartDate.error.required.two",
        requiredKey = "employeeStartDate.error.required"
      ).verifying(validStartDate)
    )

  private def validStartDate: Constraint[LocalDate] = Constraint { date =>
    if (!date.isBefore(validStart) &&
        !date.isAfter(LocalDate.now)) {
      Valid
    } else {
      Invalid("employeeStartDate.error.outofrange", dateToString(validStart), dateToString((LocalDate.now)))
    }
  }
}
