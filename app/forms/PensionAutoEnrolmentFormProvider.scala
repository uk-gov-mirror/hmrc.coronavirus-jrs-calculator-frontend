/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import javax.inject.Inject
import forms.mappings.Mappings
import models.PensionStatus
import play.api.data.Form

class PensionAutoEnrolmentFormProvider @Inject() extends Mappings {

  def apply(): Form[PensionStatus] =
    Form(
      "value" -> enumerable[PensionStatus]("pensionAutoEnrolment.error.required")
    )
}
