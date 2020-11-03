/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms

import java.time.LocalDate

import config.SchemeConfiguration
import forms.mappings.Mappings
import javax.inject.Inject
import models.Period
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationResult}
import play.api.i18n.Messages
import views.ViewUtils

class ClaimPeriodEndFormProvider @Inject()() extends Mappings with SchemeConfiguration {

  def apply(claimStart: LocalDate)(implicit messages: Messages): Form[LocalDate] =
    Form("endDate" -> localDate(invalidKey = "claimPeriodEnd.error.invalid").verifying(validEndDate(claimStart)))

  private def validEndDate(claimStart: LocalDate)(implicit messages: Messages): Constraint[LocalDate] = Constraint { claimEndDate =>
    (
      isBeforeStart(claimStart, claimEndDate),
      isDifferentCalendarMonth(claimStart, claimEndDate),
      isMoreThan14daysInFuture(claimStart, claimEndDate),
      isClaimLessThan7Days(claimStart, claimEndDate),
      isAfterPolicyEnd(claimEndDate)) match {
      case (r @ Invalid(_), _, _, _, _) => r
      case (_, r @ Invalid(_), _, _, _) => r
      case (_, _, r @ Invalid(_), _, _) => r
      case (_, _, _, r @ Invalid(_), _) => r
      case (_, _, _, _, r @ Invalid(_)) => r
      case _                            => Valid
    }
  }

  val isMoreThan14daysInFuture: (LocalDate, LocalDate) => ValidationResult = (start, end) => {
    if (start.isBefore(phaseTwoStartDate) && end.isAfter(LocalDate.now().plusDays(14))) {
      Invalid("claimPeriodEnd.cannot.be.after.14days")
    } else {
      Valid
    }
  }

  private val noStraddleMonths: Seq[Int] = Seq(7, 8, 9, 10)

  val isDifferentCalendarMonth: (LocalDate, LocalDate) => ValidationResult = (start, end) =>
    if (start.isBefore(phaseTwoStartDate) && !end.isBefore(phaseTwoStartDate)) {
      Invalid("claimPeriodEnd.cannot.be.after.july")
    } else if (noStraddleMonths.contains(start.getMonthValue) && start.getMonthValue != end.getMonthValue) {
      Invalid("claimPeriodEnd.cannot.be.of.same.month")
    } else {
      Valid
  }

  private val isBeforeStart: (LocalDate, LocalDate) => ValidationResult = (start, end) =>
    if (end.isBefore(start)) Invalid("claimPeriodEnd.cannot.be.before.claimStart") else Valid

  private def isAfterPolicyEnd(end: LocalDate)(implicit messages: Messages): ValidationResult = {
    val schemaEndDate = schemeEndDate
    if (end.isAfter(schemaEndDate)) {
      Invalid("claimPeriodEnd.cannot.be.after.policyEnd", ViewUtils.dateToString(schemaEndDate))
    } else {
      Valid
    }
  }

  private val isClaimLessThan7Days: (LocalDate, LocalDate) => ValidationResult = (start, end) => {
    val refDate = phaseTwoStartDate.minusDays(1)
    if ((
          // Allow claims that are less than 7 days long if they start at the beginning of the month.
          start.getDayOfMonth == 1 ||
          // Allow claims that are less than 7 days long if they start on the last day of the month.
          start.getDayOfMonth == start.getMonth.maxLength()
        ) && Period(start, end).countDays < 7) {
      Valid
    } else if (start.isAfter(refDate)) {
      if (end.getDayOfMonth != end.getMonth.maxLength() && Period(start, end).countDays < 7) {
        Invalid("claimPeriodEnd.cannot.be.lessThan.7days")
      } else {
        Valid
      }
    } else {
      Valid
    }
  }
}
