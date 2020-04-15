/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.OptionFieldBehaviours
import models.PensionStatus
import play.api.data.FormError

class PensionAutoEnrolmentFormProviderSpec extends OptionFieldBehaviours {

  val form = new PensionAutoEnrolmentFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "pensionAutoEnrolment.error.required"

    behave like optionsField[PensionStatus](
      form,
      fieldName,
      validValues = PensionStatus.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
