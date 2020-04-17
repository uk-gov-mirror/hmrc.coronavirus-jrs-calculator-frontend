/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.{LocalDate, ZoneOffset}

import forms.behaviours.DateBehaviours
import play.api.data.FormError
import views.ViewUtils

class FurloughEndDateFormProviderSpec extends DateBehaviours {

  private val startDate = LocalDate.of(2020, 3, 1)
  private val endDate = LocalDate.of(2020, 5, 1)

  ".value" should {

    "use startDate as minimum if furlough start date is earlier than startDate" must {
      val furloughStartDate = LocalDate.of(2020, 2, 1)
      val form = new FurloughEndDateFormProvider()(startDate, endDate, furloughStartDate)

      val validData = datesBetween(
        min = startDate.plusDays(1),
        max = LocalDate.now
      )

      behave like dateField(form, "value", validData)

      behave like dateFieldWithMax(
        form,
        "value",
        LocalDate.now,
        FormError("value", "furloughEndDate.error.maximum", Array(ViewUtils.dateToString(LocalDate.now))))

      behave like dateFieldWithMin(form, "value", startDate, FormError("value", "furloughEndDate.error.minimum", Array("1 March 2020")))

      behave like mandatoryDateField(form, "value", "furloughEndDate.error.required.all")
    }

    "use furloughStartDate as minimum if furlough start date is later than startDate" must {
      val furloughStartDate = LocalDate.of(2020, 4, 1)
      val form = new FurloughEndDateFormProvider()(startDate, endDate, furloughStartDate)

      val validData = datesBetween(
        min = furloughStartDate.plusDays(1),
        max = LocalDate.now
      )

      behave like dateField(form, "value", validData)

      behave like dateFieldWithMax(
        form,
        "value",
        LocalDate.now,
        FormError("value", "furloughEndDate.error.maximum", Array(ViewUtils.dateToString(LocalDate.now))))

      behave like dateFieldWithMin(
        form,
        "value",
        furloughStartDate,
        FormError("value", "furloughEndDate.error.minimum", Array("1 April 2020")))

      behave like mandatoryDateField(form, "value", "furloughEndDate.error.required.all")
    }

  }
}
