/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.OptionFieldBehaviours
import models.PayMethod
import play.api.data.FormError

class PayMethodFormProviderSpec extends OptionFieldBehaviours {

  val form = new PayMethodFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "payMethod.error.required"

    behave like optionsField[PayMethod](
      form,
      fieldName,
      validValues = PayMethod.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
