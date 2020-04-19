/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.FurloughCalculations

class FurloughCalculationsFormProvider @Inject() extends Mappings {

  def apply(): Form[FurloughCalculations] =
    Form(
      "value" -> enumerable[FurloughCalculations]("furloughCalculations.error.required")
    )
}
