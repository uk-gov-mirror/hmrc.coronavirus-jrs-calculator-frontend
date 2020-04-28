/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject
import forms.mappings.Mappings
import models.PensionContribution
import play.api.data.Form

class PensionContributionFormProvider @Inject() extends Mappings {

  def apply(): Form[PensionContribution] =
    Form(
      "value" -> enumerable[PensionContribution]("pensionContribution.error.required")
    )
}
