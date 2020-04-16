/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.{LocalDate, ZoneOffset}

import forms.behaviours.DateBehaviours
import play.api.data.FormError

class FurloughEndDateFormProviderSpec extends DateBehaviours {

  private val startDate = LocalDate.of(2020, 3, 1)
  private val endDate = LocalDate.of(2020, 5, 1)
  private val furloughEndDate = LocalDate.of(2020, 4, 1)

  ".value" should {

    "use startDate as minimum if furlough start date is not present" must {
      val form = new FurloughEndDateFormProvider()(startDate, endDate, None)

      val validData = datesBetween(
        min = startDate.plusDays(1),
        max = endDate.minusDays(1)
      )

      behave like dateField(form, "value", validData)

      behave like dateFieldWithMax(form, "value", endDate, FormError("value", "furloughEndDate.error.maximum", Array("1 May 2020")))

      behave like dateFieldWithMin(form, "value", startDate, FormError("value", "furloughEndDate.error.minimum", Array("1 March 2020")))

      behave like mandatoryDateField(form, "value", "furloughEndDate.error.required.all")
    }

    "use furloughEndDate as minimum if furlough start date is present" must {
      val form = new FurloughEndDateFormProvider()(startDate, endDate, Some(furloughEndDate))

      val validData = datesBetween(
        min = furloughEndDate.plusDays(1),
        max = endDate.minusDays(1)
      )

      behave like dateField(form, "value", validData)

      behave like dateFieldWithMax(form, "value", endDate, FormError("value", "furloughEndDate.error.maximum", Array("1 May 2020")))

      behave like dateFieldWithMin(form, "value", startDate, FormError("value", "furloughEndDate.error.minimum", Array("1 April 2020")))

      behave like mandatoryDateField(form, "value", "furloughEndDate.error.required.all")
    }

  }
}
