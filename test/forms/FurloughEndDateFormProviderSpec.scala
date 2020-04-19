/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.LocalDate

import forms.behaviours.DateBehaviours
import play.api.data.FormError

class FurloughEndDateFormProviderSpec extends DateBehaviours {

  private val startDate = LocalDate.of(2020, 3, 1)
  private val endDate = LocalDate.of(2020, 5, 1)
  private val furloughStart = startDate

  ".value" should {

    val form = new FurloughEndDateFormProvider()(endDate, furloughStart)

    val validData = datesBetween(
      min = startDate.plusDays(20),
      max = startDate.plusDays(31)
    )

    behave like dateField(form, "value", validData)

    behave like dateFieldWithMax(form, "value", endDate, FormError("value", "furloughEndDate.error.min.max"))

    behave like mandatoryDateField(form, "value", "furloughEndDate.error.required.all")
  }
}
