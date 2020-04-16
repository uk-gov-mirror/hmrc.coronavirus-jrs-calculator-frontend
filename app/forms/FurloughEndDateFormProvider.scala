/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import views.ViewUtils.dateToString

class FurloughEndDateFormProvider @Inject() extends Mappings {

  def apply(claimPeriodStart: LocalDate, claimPeriodEnd: LocalDate, furloughStartDate: Option[LocalDate]): Form[LocalDate] = {
    val minimumDate = furloughStartDate.getOrElse(claimPeriodStart)

    Form(
      "value" -> localDate(
        invalidKey = "furloughEndDate.error.invalid",
        allRequiredKey = "furloughEndDate.error.required.all",
        twoRequiredKey = "furloughEndDate.error.required.two",
        requiredKey = "furloughEndDate.error.required"
      ).verifying(minDate(minimumDate.plusDays(1), "furloughEndDate.error.minimum", dateToString(minimumDate)))
        .verifying(maxDate(claimPeriodEnd.minusDays(1), "furloughEndDate.error.maximum", dateToString(claimPeriodEnd)))
    )
  }
}
