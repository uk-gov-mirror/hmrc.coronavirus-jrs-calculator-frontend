/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.FurloughOngoing

class FurloughOngoingFormProvider @Inject() extends Mappings {

  def apply(): Form[FurloughOngoing] =
    Form(
      "value" -> enumerable[FurloughOngoing]("furloughOngoing.error.required")
    )
}
