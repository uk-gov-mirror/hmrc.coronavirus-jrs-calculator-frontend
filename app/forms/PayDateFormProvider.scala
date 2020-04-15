/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid}
import views.ViewUtils

class PayDateFormProvider @Inject() extends Mappings {

  def apply(beforeDate: Option[LocalDate] = None, afterDate: Option[LocalDate] = None): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey = "payDate.error.invalid",
        allRequiredKey = "payDate.error.required.all",
        twoRequiredKey = "payDate.error.required.two",
        requiredKey = "payDate.error.required"
      ).verifying(isBeforeIfDefined(beforeDate))
        .verifying(isAfterIfDefined(afterDate))
    )

  private def isBeforeIfDefined(beforeDate: Option[LocalDate]): Constraint[LocalDate] = Constraint { date =>
    if (beforeDate.forall(date.isBefore(_))) Valid else Invalid("payDate.error.mustBeBefore", ViewUtils.dateToString(beforeDate.get))
  }

  private def isAfterIfDefined(afterDate: Option[LocalDate]): Constraint[LocalDate] = Constraint { date =>
    if (afterDate.forall(date.isAfter(_))) Valid else Invalid("payDate.error.mustBeAfter", ViewUtils.dateToString(afterDate.get))
  }
}
