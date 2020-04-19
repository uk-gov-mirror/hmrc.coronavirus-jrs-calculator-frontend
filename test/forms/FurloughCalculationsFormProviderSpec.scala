/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.OptionFieldBehaviours
import models.FurloughCalculations
import play.api.data.FormError

class FurloughCalculationsFormProviderSpec extends OptionFieldBehaviours {

  val form = new FurloughCalculationsFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "furloughCalculations.error.required"

    behave like optionsField[FurloughCalculations](
      form,
      fieldName,
      validValues = FurloughCalculations.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
