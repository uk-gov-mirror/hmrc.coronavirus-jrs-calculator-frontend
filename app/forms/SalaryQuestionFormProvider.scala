/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import models.Salary
import play.api.data.Form
import play.api.data.Forms._

class SalaryQuestionFormProvider @Inject() extends Mappings {

  def apply(): Form[Salary] = Form(
    mapping(
      "value" -> bigDecimal(
        requiredKey = "salaryQuestion.salary.error.required",
        nonNumericKey = "salaryQuestion.salary.error.invalid"
      ).verifying(positiveValue())
        .verifying(maxTwoDecimals())
    )(Salary.apply)(Salary.unapply)
  )
}
