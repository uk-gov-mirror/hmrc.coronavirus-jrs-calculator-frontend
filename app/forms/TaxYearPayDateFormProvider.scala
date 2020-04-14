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

class TaxYearPayDateFormProvider @Inject() extends Mappings with ImplicitDateFormatter {

  private val start = LocalDate.of(2020, 4, 7)
  private val end = LocalDate.of(2020, 5, 31)

  def apply(): Form[LocalDate] =
    Form(
      "payDate" -> localDate(
        invalidKey = "taxYearPayDate.error.invalid",
        allRequiredKey = "taxYearPayDate.error.required.all",
        twoRequiredKey = "taxYearPayDate.error.required.two",
        requiredKey = "taxYearPayDate.error.required"
      ).verifying(validPayDate)
    )

  private def validPayDate: Constraint[LocalDate] = Constraint { date =>
    if (!date.isBefore(start) && !date.isAfter(end))
      Valid
    else {
      Invalid("taxYearPayDate.error.outofrange", dateToString(start), dateToString(end))
    }
  }
}
