/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import models.Salary
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.validation.{Constraint, Invalid, Valid}

class LastYearPayFormProvider @Inject() extends Mappings {

  def apply(): Form[Salary] = Form(
    mapping(
      "value" -> bigDecimal(
        requiredKey = "lastYearPay.error.required",
        nonNumericKey = "lastYearPay.error.nonNumeric"
      ).verifying(validSalary)
    )(Salary.apply)(Salary.unapply)
  )

  private def validSalary: Constraint[BigDecimal] = Constraint { value =>
    if (value >= 0) Valid else Invalid("lastYearPay.error.negative")
  }
}
