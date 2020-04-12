/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.LocalDate

import config.FrontendAppConfig
import forms.mappings.Mappings
import javax.inject.Inject
import models.ClaimPeriodModel
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid}
import utils.ImplicitDateFormatter

class ClaimPeriodFormProvider @Inject()(appConfig: FrontendAppConfig) extends Mappings with ImplicitDateFormatter {

  def apply(): Form[ClaimPeriodModel] =
    Form(
      mapping(
        "startDateValue" -> localDate(
          invalidKey = "claimPeriod.start.error.invalid",
          allRequiredKey = "claimPeriod.start.error.required.all",
          twoRequiredKey = "claimPeriod.start.error.required.two",
          requiredKey = "claimPeriod.start.error.required"
        ).verifying(validStartDate),
        "endDateValue" -> localDate(
          invalidKey = "claimPeriod.end.error.invalid",
          allRequiredKey = "claimPeriod.end.error.required.all",
          twoRequiredKey = "claimPeriod.end.error.required.two",
          requiredKey = "claimPeriod.end.error.required"
        ).verifying(validEndDate)
      )(ClaimPeriodModel.apply)(ClaimPeriodModel.unapply))

  private def validStartDate: Constraint[LocalDate] = Constraint { claimStartDate =>
    if (!claimStartDate.isBefore(appConfig.schemeStartDate) &&
        !claimStartDate.isAfter(appConfig.schemeEndDate))
      Valid
    else {
      Invalid("claimPeriod.start.error.outofrange", dateToString(appConfig.schemeStartDate), dateToString((appConfig.schemeEndDate)))
    }
  }

  private def validEndDate: Constraint[LocalDate] = Constraint { claimEndDate =>
    if (!claimEndDate.isAfter(appConfig.schemeEndDate) &&
        !claimEndDate.isBefore(appConfig.schemeStartDate))
      Valid
    else {
      Invalid("claimPeriod.end.error.outofrange", dateToString(appConfig.schemeStartDate), dateToString(appConfig.schemeEndDate))
    }
  }
}
