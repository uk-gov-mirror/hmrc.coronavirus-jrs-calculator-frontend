/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.LocalDate

import config.FrontendAppConfig
import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import views.ViewUtils._

class FurloughStartDateFormProvider @Inject()(appConfig: FrontendAppConfig) extends Mappings {

  def apply(claimPeriodEnd: LocalDate): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey = "furloughStartDate.error.invalid",
        allRequiredKey = "furloughStartDate.error.required.all",
        twoRequiredKey = "furloughStartDate.error.required.two",
        requiredKey = "furloughStartDate.error.required"
      ).verifying(minDate(appConfig.schemeStartDate, "furloughStartDate.error.minimum", dateToString(appConfig.schemeStartDate)))
        .verifying(maxDate(claimPeriodEnd.minusDays(1), "furloughStartDate.error.maximum", dateToString(claimPeriodEnd)))
    )
}
