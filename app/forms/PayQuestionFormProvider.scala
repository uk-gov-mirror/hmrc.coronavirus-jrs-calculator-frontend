/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import models.PayQuestion
import play.api.data.Form

class PayQuestionFormProvider @Inject() extends Mappings {

  def apply(): Form[PayQuestion] =
    Form(
      "value" -> enumerable[PayQuestion]("payQuestion.error.required")
    )
}
