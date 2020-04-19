/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.LocalDate

import forms.behaviours.DateBehaviours
import play.api.data.FormError
import views.ViewUtils

class LastPayDateFormProviderSpec extends DateBehaviours {

  lazy val form = new LastPayDateFormProvider()(minimiumDate)
  val minimiumDate = LocalDate.now()

  ".value" should {

    behave like dateFieldWithMin(
      form,
      "value",
      minimiumDate.minusDays(90),
      FormError("value", "lastPayDate.error.minimum", Array(ViewUtils.dateToString(minimiumDate.minusDays(90)))))

    behave like mandatoryDateField(form, "value", "lastPayDate.error.required.all")
  }
}
