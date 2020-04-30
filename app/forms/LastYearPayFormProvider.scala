/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import models.Amount
import play.api.data.Form
import play.api.data.Forms.mapping

class LastYearPayFormProvider @Inject() extends Mappings {

  def apply(): Form[Amount] = Form(
    mapping(
      "value" -> bigDecimal(
        requiredKey = "lastYearPay.error.required",
        nonNumericKey = "lastYearPay.error.nonNumeric"
      ).verifying(positiveValue())
        .verifying(maxTwoDecimals())
    )(Amount.apply)(Amount.unapply)
  )
}
