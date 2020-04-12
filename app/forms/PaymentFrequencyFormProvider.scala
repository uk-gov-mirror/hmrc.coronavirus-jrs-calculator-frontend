/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.PaymentFrequency

class PaymentFrequencyFormProvider @Inject() extends Mappings {

  def apply(): Form[PaymentFrequency] =
    Form(
      "value" -> enumerable[PaymentFrequency]("paymentFrequency.error.required")
    )
}
