/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class PayDateFormProvider @Inject() extends Mappings {

  def apply(): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey = "payDate.error.invalid",
        allRequiredKey = "payDate.error.required.all",
        twoRequiredKey = "payDate.error.required.two",
        requiredKey = "payDate.error.required"
      )
    )
}
