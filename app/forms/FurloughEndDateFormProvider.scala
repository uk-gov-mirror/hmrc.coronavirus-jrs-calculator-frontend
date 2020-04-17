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

  def apply(claimPeriodStart: LocalDate, claimPeriodEnd: LocalDate, furloughStartDate: LocalDate): Form[LocalDate] = {
    val minimumDate = if (furloughStartDate.isAfter(claimPeriodStart)) furloughStartDate else claimPeriodStart
    val maximumDate = LocalDate.now()

    Form(
      "value" -> localDate(
        invalidKey = "furloughEndDate.error.invalid",
        allRequiredKey = "furloughEndDate.error.required.all",
        twoRequiredKey = "furloughEndDate.error.required.two",
        requiredKey = "furloughEndDate.error.required"
      ).verifying(minDate(minimumDate.plusDays(1), "furloughEndDate.error.minimum", dateToString(minimumDate)))
        .verifying(maxDate(maximumDate.plusDays(1), "furloughEndDate.error.maximum", dateToString(maximumDate)))
    )
  }
}
