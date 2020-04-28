/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import models.FurloughOngoing
import play.api.data.Form

class FurloughOngoingFormProvider @Inject() extends Mappings {

  def apply(): Form[FurloughOngoing] =
    Form(
      "value" -> enumerable[FurloughOngoing]("furloughOngoing.error.required")
    )
}
