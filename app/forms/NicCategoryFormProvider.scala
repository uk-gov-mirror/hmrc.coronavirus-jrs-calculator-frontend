/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import models.NicCategory
import play.api.data.Form

class NicCategoryFormProvider @Inject() extends Mappings {

  def apply(): Form[NicCategory] =
    Form(
      "value" -> enumerable[NicCategory]("nicCategory.error.required")
    )
}
