/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.OptionFieldBehaviours
import models.PayQuestion
import play.api.data.FormError

class PayQuestionFormProviderSpec extends OptionFieldBehaviours {

  val form = new PayQuestionFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "payQuestion.error.required"

    behave like optionsField[PayQuestion](
      form,
      fieldName,
      validValues = PayQuestion.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
