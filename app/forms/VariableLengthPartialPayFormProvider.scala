/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject
import forms.mappings.Mappings
import models.{Salary, VariableLengthPartialPay}
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.validation.{Constraint, Invalid, Valid}

class VariableLengthPartialPayFormProvider @Inject() extends Mappings {

  def apply(): Form[VariableLengthPartialPay] = Form(
    mapping(
      "value" -> bigDecimal(
        requiredKey = "variableLengthPartialPay.error.required",
        nonNumericKey = "variableLengthPartialPay.error.invalid"
      ).verifying(validSalary)
    )(VariableLengthPartialPay.apply)(VariableLengthPartialPay.unapply)
  )

  private def validSalary: Constraint[BigDecimal] = Constraint { value =>
    if (value >= 0) Valid else Invalid("variableLengthPartialPay.error.negative")
  }
}
