/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.BigDecimalFieldBehaviours
import play.api.data.FormError

class VariableLengthPartialPayFormProviderSpec extends BigDecimalFieldBehaviours {

  val requiredKey = "variableLengthPartialPay.error.required"
  val lengthKey = "variableLengthPartialPay.error.length"
  val maxLength = 100

  val form = new VariableLengthPartialPayFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "variableLengthPartialPay.error.required"
    val invalidKey = "variableLengthPartialPay.error.invalid"

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
