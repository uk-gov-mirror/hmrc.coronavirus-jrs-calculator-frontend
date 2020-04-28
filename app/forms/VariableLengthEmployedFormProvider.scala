/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import models.VariableLengthEmployed
import play.api.data.Form

class VariableLengthEmployedFormProvider @Inject() extends Mappings {

  def apply(): Form[VariableLengthEmployed] =
    Form(
      "value" -> enumerable[VariableLengthEmployed]("variableLengthEmployed.error.required")
    )
}
