/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import models.FurloughPartialPay
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.validation.{Constraint, Invalid, Valid}

class FurloughPartialPayFormProvider extends Mappings {

  def apply(): Form[FurloughPartialPay] = Form(
    mapping(
      "value" -> bigDecimal(
        requiredKey = "FurloughPartialPay.error.required",
        nonNumericKey = "FurloughPartialPay.error.invalid"
      ).verifying(validSalary)
    )(FurloughPartialPay.apply)(FurloughPartialPay.unapply)
  )

  private def validSalary: Constraint[BigDecimal] = Constraint { value =>
    if (value >= 0) Valid else Invalid("FurloughPartialPay.error.negative")
  }
}
