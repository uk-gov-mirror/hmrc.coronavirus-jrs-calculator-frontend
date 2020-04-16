/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.FurloughDates

class FurloughDatesFormProvider @Inject() extends Mappings {

  def apply(): Form[FurloughDates] =
    Form(
      "value" -> enumerable[FurloughDates]("furloughDates.error.required")
    )
}
