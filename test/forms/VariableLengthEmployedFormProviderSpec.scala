/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.OptionFieldBehaviours
import models.EmployeeStarted
import play.api.data.FormError

class VariableLengthEmployedFormProviderSpec extends OptionFieldBehaviours {

  val form = new VariableLengthEmployedFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "variableLengthEmployed.error.required"

    behave like optionsField[EmployeeStarted](
      form,
      fieldName,
      validValues = EmployeeStarted.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
