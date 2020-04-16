/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.BigDecimalFieldBehaviours
import play.api.data.FormError

class VariableGrossPayFormProviderSpec extends BigDecimalFieldBehaviours {

  val requiredKey = "variableGrossPay.error.required"
  val lengthKey = "variableGrossPay.error.length"

  val form = new VariableGrossPayFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "variableGrossPay.error.required"
    val invalidKey = "variableGrossPay.error.invalid"

    behave like bigDecimalField(
      form,
      fieldName,
      error = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
