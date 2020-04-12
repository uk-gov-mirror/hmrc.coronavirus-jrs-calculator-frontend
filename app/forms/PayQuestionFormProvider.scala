/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.PayQuestion

class PayQuestionFormProvider @Inject() extends Mappings {

  def apply(): Form[PayQuestion] =
    Form(
      "value" -> enumerable[PayQuestion]("payQuestion.error.required")
    )
}
