/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.FurloughTopUpStatus

class FurloughTopUpFormProvider @Inject() extends Mappings {

  def apply(): Form[FurloughTopUpStatus] =
    Form(
      "value" -> enumerable[FurloughTopUpStatus]("furloughTopUp.error.required")
    )
}
