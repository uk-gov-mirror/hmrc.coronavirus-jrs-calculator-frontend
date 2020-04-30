/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.LocalDate

import forms.mappings.Mappings
import play.api.data.Form

class FurloughEndDateFormProvider extends Mappings {

  def apply(claimPeriodEnd: LocalDate, furloughStartDate: LocalDate): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey = "furloughEndDate.error.invalid",
        allRequiredKey = "furloughEndDate.error.required.all",
        twoRequiredKey = "furloughEndDate.error.required.two",
        requiredKey = "furloughEndDate.error.required"
      ).verifying(minDate(furloughStartDate.plusDays(20), "furloughEndDate.error.min.max"))
        .verifying(maxDate(claimPeriodEnd, "furloughEndDate.error.min.max"))
    )
}
