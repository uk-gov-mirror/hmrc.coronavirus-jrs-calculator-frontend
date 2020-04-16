/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.VariableLengthEmployed

class VariableLengthEmployedFormProvider @Inject() extends Mappings {

  def apply(): Form[VariableLengthEmployed] =
    Form(
      "value" -> enumerable[VariableLengthEmployed]("variableLengthEmployed.error.required")
    )
}
