/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.OptionFieldBehaviours
import models.FurloughTopUpStatus
import play.api.data.FormError

class FurloughTopUpFormProviderSpec extends OptionFieldBehaviours {

  val form = new FurloughTopUpFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "furloughTopUp.error.required"

    behave like optionsField[FurloughTopUpStatus](
      form,
      fieldName,
      validValues = FurloughTopUpStatus.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
