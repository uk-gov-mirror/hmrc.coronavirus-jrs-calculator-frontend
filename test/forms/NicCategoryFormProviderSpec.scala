/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.OptionFieldBehaviours
import models.NicCategory
import play.api.data.FormError

class NicCategoryFormProviderSpec extends OptionFieldBehaviours {

  val form = new NicCategoryFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "nicCategory.error.required"

    behave like optionsField[NicCategory](
      form,
      fieldName,
      validValues = NicCategory.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
