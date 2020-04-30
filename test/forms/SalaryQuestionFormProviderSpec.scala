/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.BigDecimalFieldBehaviours
import play.api.data.FormError

class SalaryQuestionFormProviderSpec extends BigDecimalFieldBehaviours {

  val form = new SalaryQuestionFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "salaryQuestion.salary.error.required"
    val invalidKey = "salaryQuestion.salary.error.invalid"

    behave like bigDecimalField(
      form,
      fieldName,
      error = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
