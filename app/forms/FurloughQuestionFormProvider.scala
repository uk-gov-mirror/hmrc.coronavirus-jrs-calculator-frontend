/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.FurloughQuestion

class FurloughQuestionFormProvider @Inject() extends Mappings {

  def apply(): Form[FurloughQuestion] =
    Form(
      "value" -> enumerable[FurloughQuestion]("furloughQuestion.error.required")
    )
}
