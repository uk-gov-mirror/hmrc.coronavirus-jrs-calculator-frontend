/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import models.PaymentFrequency
import play.api.data.Form

class PaymentFrequencyFormProvider @Inject() extends Mappings {

  def apply(): Form[PaymentFrequency] =
    Form(
      "value" -> enumerable[PaymentFrequency]("payFrequency.error.required")
    )
}
