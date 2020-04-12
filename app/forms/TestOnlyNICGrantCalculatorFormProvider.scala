/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.LocalDate

import config.FrontendAppConfig
import forms.mappings.Mappings
import javax.inject.Inject
import models.{PaymentFrequency, TestOnlyNICGrantModel}
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid}
import utils.ImplicitDateFormatter

class TestOnlyNICGrantCalculatorFormProvider @Inject()(appConfig: FrontendAppConfig)
    extends Mappings with ImplicitDateFormatter {

  def apply(): Form[TestOnlyNICGrantModel] =
    Form(
      mapping(
        "startDate" -> localDate(
          invalidKey = "testOnlyNICGrantCalculator.startDate.error.invalid",
          allRequiredKey = "testOnlyNICGrantCalculator.startDate.error.required.all",
          twoRequiredKey = "testOnlyNICGrantCalculator.startDate.error.required.two",
          requiredKey = "testOnlyNICGrantCalculator.startDate.error.required"
        ).verifying(validStartDate),
        "endDate" -> localDate(
          invalidKey = "testOnlyNICGrantCalculator.endDate.error.invalid",
          allRequiredKey = "testOnlyNICGrantCalculator.endDate.error.required.all",
          twoRequiredKey = "testOnlyNICGrantCalculator.endDate.error.required.two",
          requiredKey = "testOnlyNICGrantCalculator.endDate.error.required"
        ).verifying(validEndDate),
        "furloughedAmount" -> double(
          requiredKey = "testOnlyNICGrantCalculator.furloughedAmount.error.required",
          nonNumericKey = "testOnlyNICGrantCalculator.furloughedAmount.error.invalid"
        ).verifying(validDouble),
        "value" -> enumerable(
          requiredKey = "testOnlyNICGrantCalculator.frequency.required",
          invalidKey = "testOnlyNICGrantCalculator.frequency.invalid"
        )(PaymentFrequency.enumerable),
        "payDate" -> localDate(
          invalidKey = "testOnlyNICGrantCalculator.payDate.error.invalid",
          allRequiredKey = "testOnlyNICGrantCalculator.payDate.error.required.all",
          twoRequiredKey = "testOnlyNICGrantCalculator.payDate.error.required.two",
          requiredKey = "testOnlyNICGrantCalculator.payDate.error.required"
        ).verifying(validEndDate)
      )(TestOnlyNICGrantModel.apply)(TestOnlyNICGrantModel.unapply))

  private def validStartDate: Constraint[LocalDate] = Constraint { claimStartDate =>
    if (!claimStartDate.isBefore(appConfig.schemeStartDate) &&
        !claimStartDate.isAfter(appConfig.schemeEndDate))
      Valid
    else {
      Invalid(
        "testOnlyNICGrantCalculator.start.error.outofrange",
        dateToString(appConfig.schemeStartDate),
        dateToString((appConfig.schemeEndDate)))
    }
  }

  private def validEndDate: Constraint[LocalDate] = Constraint { claimEndDate =>
    if (!claimEndDate.isAfter(appConfig.schemeEndDate) &&
        !claimEndDate.isBefore(appConfig.schemeStartDate))
      Valid
    else {
      Invalid(
        "testOnlyNICGrantCalculator.end.error.outofrange",
        dateToString(appConfig.schemeStartDate),
        dateToString(appConfig.schemeEndDate))
    }
  }

  private def validDouble: Constraint[Double] = Constraint { value =>
    if (value >= 0) Valid else Invalid("testOnlyNICGrantCalculator.furloughedAmount.error.negative")
  }

}
