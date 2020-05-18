package forms

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class $className$FormProvider @Inject() extends Mappings {

  def apply(): Form[LocalDate] = Form("value" -> localDate(invalidKey = "$className;format="decap"$.error.invalid"))
}
