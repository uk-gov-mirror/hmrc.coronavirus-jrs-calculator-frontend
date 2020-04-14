/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class PensionAutoEnrolmentFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "pensionAutoEnrolment.error.required"
  val invalidKey = "error.boolean"

  val form = new PensionAutoEnrolmentFormProvider()()

  ".value" must {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
