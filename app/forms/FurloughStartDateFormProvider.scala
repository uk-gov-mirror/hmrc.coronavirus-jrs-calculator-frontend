/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import views.ViewUtils._

class FurloughStartDateFormProvider @Inject() extends Mappings {

  def apply(claimPeriodStart: LocalDate, claimPeriodEnd: LocalDate): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey = "furloughStartDate.error.invalid",
        allRequiredKey = "furloughStartDate.error.required.all",
        twoRequiredKey = "furloughStartDate.error.required.two",
        requiredKey = "furloughStartDate.error.required"
      ).verifying(minDate(claimPeriodStart.plusDays(1), "furloughStartDate.error.minimum", dateToString(claimPeriodStart)))
        .verifying(maxDate(claimPeriodEnd.minusDays(1), "furloughStartDate.error.maximum", dateToString(claimPeriodEnd)))
    )
}
