/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import models.VariableGrossPay
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.validation.{Constraint, Invalid, Valid}

class VariableGrossPayFormProvider @Inject() extends Mappings {

  def apply(): Form[VariableGrossPay] =
    Form(
      mapping(
        "value" -> bigDecimal(
          requiredKey = "variableGrossPay.error.required",
          nonNumericKey = "variableGrossPay.error.invalid"
        ).verifying(validSalary)
      )(VariableGrossPay.apply)(VariableGrossPay.unapply)
    )

  private def validSalary: Constraint[BigDecimal] = Constraint { value =>
    if (value >= 0) Valid else Invalid("variableGrossPay.error.negative")
  }
}
