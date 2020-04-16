/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class FurloughStartDateFormProvider @Inject() extends Mappings {

  def apply(): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey = "furloughStartDate.error.invalid",
        allRequiredKey = "furloughStartDate.error.required.all",
        twoRequiredKey = "furloughStartDate.error.required.two",
        requiredKey = "furloughStartDate.error.required"
      )
    )
}
