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

class ClaimPeriodEndFormProvider @Inject()(appConfig: FrontendAppConfig) extends Mappings with ImplicitDateFormatter {

  def apply(): Form[LocalDate] =
    Form(
      "endDate" -> localDate(
        invalidKey = "claimPeriodEnd.error.invalid",
        allRequiredKey = "claimPeriodEnd.error.required.all",
        twoRequiredKey = "claimPeriodEnd.error.required.two",
        requiredKey = "claimPeriodEnd.error.required"
      ).verifying(validEndDate)
    )

  private def validEndDate: Constraint[LocalDate] = Constraint { claimEndDate =>
    if (!claimEndDate.isAfter(appConfig.schemeEndDate) &&
        !claimEndDate.isBefore(appConfig.schemeStartDate))
      Valid
    else {
      Invalid("claimPeriodEnd.error.outofrange", dateToString(appConfig.schemeStartDate), dateToString(appConfig.schemeEndDate))
    }
  }
}
