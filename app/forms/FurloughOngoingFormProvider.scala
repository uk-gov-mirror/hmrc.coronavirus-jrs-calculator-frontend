/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.FurloughStatus

class FurloughOngoingFormProvider @Inject() extends Mappings {

  def apply(): Form[FurloughStatus] =
    Form(
      "value" -> enumerable[FurloughStatus]("furloughOngoing.error.required")
    )
}
