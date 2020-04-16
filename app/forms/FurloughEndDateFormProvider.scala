/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class FurloughEndDateFormProvider @Inject() extends Mappings {

  def apply(): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey = "furloughEndDate.error.invalid",
        allRequiredKey = "furloughEndDate.error.required.all",
        twoRequiredKey = "furloughEndDate.error.required.two",
        requiredKey = "furloughEndDate.error.required"
      )
    )
}
