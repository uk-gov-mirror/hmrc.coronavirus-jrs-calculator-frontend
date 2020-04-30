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

class VariableGrossPayFormProvider @Inject() extends Mappings {

  def apply(): Form[VariableGrossPay] =
    Form(
      mapping(
        "value" -> bigDecimal(
          requiredKey = "variableGrossPay.error.required",
          nonNumericKey = "variableGrossPay.error.invalid"
        ).verifying(positiveValue())
          .verifying(maxTwoDecimals())
      )(VariableGrossPay.apply)(VariableGrossPay.unapply)
    )
}
