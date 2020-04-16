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
import play.api.data.validation.{Constraint, Invalid, Valid}

class SalaryQuestionFormProvider @Inject() extends Mappings {

  def apply(): Form[Salary] = Form(
    mapping(
      "salary" -> bigDecimal(
        requiredKey = "salaryQuestion.salary.error.required",
        nonNumericKey = "salaryQuestion.salary.error.invalid"
      ).verifying(validSalary)
    )(Salary.apply)(Salary.unapply)
  )

  private def validSalary: Constraint[BigDecimal] = Constraint { value =>
    if (value >= 0) Valid else Invalid("salaryQuestion.salary.error.negative")
  }
}
