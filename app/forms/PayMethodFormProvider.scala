/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.PayMethod

class PayMethodFormProvider @Inject() extends Mappings {

  def apply(): Form[PayMethod] =
    Form(
      "value" -> enumerable[PayMethod]("payMethod.error.required")
    )
}
