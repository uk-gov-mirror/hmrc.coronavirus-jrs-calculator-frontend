/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.OptionFieldBehaviours
import models.PaymentFrequency
import play.api.data.FormError

class PaymentFrequencyFormProviderSpec extends OptionFieldBehaviours {

  val form = new PaymentFrequencyFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "paymentFrequency.error.required"

    behave like optionsField[PaymentFrequency](
      form,
      fieldName,
      validValues = PaymentFrequency.values.toSeq,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
