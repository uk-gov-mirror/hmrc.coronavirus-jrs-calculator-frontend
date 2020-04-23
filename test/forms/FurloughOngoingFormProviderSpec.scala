/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.OptionFieldBehaviours
import models.FurloughOngoing
import play.api.data.FormError

class FurloughOngoingFormProviderSpec extends OptionFieldBehaviours {

  val form = new FurloughOngoingFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "furloughOngoing.error.required"

    behave like optionsField[FurloughOngoing](
      form,
      fieldName,
      validValues = FurloughOngoing.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
