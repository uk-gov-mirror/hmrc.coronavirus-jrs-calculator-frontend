/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.OptionFieldBehaviours
import models.FurloughDates
import play.api.data.FormError

class FurloughDatesFormProviderSpec extends OptionFieldBehaviours {

  val form = new FurloughDatesFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "furloughDates.error.required"

    behave like optionsField[FurloughDates](
      form,
      fieldName,
      validValues = FurloughDates.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
