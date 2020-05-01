/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.EmployeeStarted

class VariableLengthEmployedFormProvider @Inject() extends Mappings {

  def apply(): Form[EmployeeStarted] =
    Form(
      "value" -> enumerable[EmployeeStarted]("variableLengthEmployed.error.required")
    )
}
