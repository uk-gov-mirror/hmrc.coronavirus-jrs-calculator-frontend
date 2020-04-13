/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class ReviewPayDatesFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "reviewPayDates.error.required"
  val invalidKey = "error.boolean"

  val form = new ReviewPayDatesFormProvider()()

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
