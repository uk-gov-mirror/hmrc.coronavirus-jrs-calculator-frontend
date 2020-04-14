/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.OptionFieldBehaviours
import models.FurloughQuestion
import play.api.data.FormError

class FurloughQuestionFormProviderSpec extends OptionFieldBehaviours {

  val form = new FurloughQuestionFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "furloughQuestion.error.required"

    behave like optionsField[FurloughQuestion](
      form,
      fieldName,
      validValues = FurloughQuestion.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
