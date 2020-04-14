/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.NicCategory

class NicCategoryFormProvider @Inject() extends Mappings {

  def apply(): Form[NicCategory] =
    Form(
      "value" -> enumerable[NicCategory]("nicCategory.error.required")
    )
}
