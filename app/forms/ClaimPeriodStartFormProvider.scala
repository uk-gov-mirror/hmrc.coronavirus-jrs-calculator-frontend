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
import play.api.data.validation.{Constraint, Invalid, Valid}
import utils.ImplicitDateFormatter

class ClaimPeriodStartFormProvider @Inject()(appConfig: FrontendAppConfig) extends Mappings with ImplicitDateFormatter {

  def apply(): Form[LocalDate] =
    Form(
      "startDate" -> localDate(
        invalidKey = "claimPeriodStart.error.invalid",
        allRequiredKey = "claimPeriodStart.error.required.all",
        twoRequiredKey = "claimPeriodStart.error.required.two",
        requiredKey = "claimPeriodStart.error.required"
      ).verifying(validStartDate)
    )

  private def validStartDate: Constraint[LocalDate] = Constraint { claimStartDate =>
    if (!claimStartDate.isBefore(appConfig.schemeStartDate) &&
        !claimStartDate.isAfter(appConfig.schemeEndDate))
      Valid
    else {
      Invalid("claimPeriodStart.error.outofrange", dateToString(appConfig.schemeStartDate), dateToString((appConfig.schemeEndDate)))
    }
  }
}
