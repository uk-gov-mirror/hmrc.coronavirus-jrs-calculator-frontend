/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.LocalDate

import forms.behaviours.DateBehaviours
import play.api.data.FormError

class FurloughStartDateFormProviderSpec extends DateBehaviours {

  private val startDate = LocalDate.of(2020, 3, 1)
  private val endDate = LocalDate.of(2020, 5, 1)
  val form = new FurloughStartDateFormProvider()(endDate)

  ".value" should {

    val validData = datesBetween(
      min = startDate.plusDays(30),
      max = endDate.minusDays(1)
    )

    behave like dateField(form, "value", validData)

    behave like dateFieldWithMax(form, "value", endDate, FormError("value", "furloughStartDate.error.maximum", Array("1 May 2020")))

    behave like mandatoryDateField(form, "value", "furloughStartDate.error.required.all")
  }
}
