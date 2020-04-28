/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import models.FurloughCalculations
import play.api.data.Form

class FurloughCalculationsFormProvider @Inject() extends Mappings {

  def apply(): Form[FurloughCalculations] =
    Form(
      "value" -> enumerable[FurloughCalculations]("furloughCalculations.error.required")
    )
}
