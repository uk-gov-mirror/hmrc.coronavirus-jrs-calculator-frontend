/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.DoubleFieldBehaviours
import play.api.data.FormError

class SalaryQuestionFormProviderSpec extends DoubleFieldBehaviours {

  val form = new SalaryQuestionFormProvider()()

  ".salary" must {

    val fieldName = "salary"
    val requiredKey = "salaryQuestion.salary.error.required"
    val invalidKey = "salaryQuestion.salary.error.invalid"

    behave like doubleField(
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
