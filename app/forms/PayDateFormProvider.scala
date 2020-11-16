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

import forms.mappings.Mappings
import javax.inject.Inject
import models.PaymentFrequency
import models.PaymentFrequency.Monthly
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.i18n.Messages
import utils.LocalDateHelpers.LocalDateHelper
import views.ViewUtils.dateToString

class PayDateFormProvider @Inject()() extends Mappings {

  def apply(beforeDate: Option[LocalDate] = None, afterDate: Option[LocalDate] = None, paymentFrequency: Option[PaymentFrequency] = None)(
    implicit messages: Messages): Form[LocalDate] =
    Form(
      "value" -> localDate(invalidKey = "payDate.error.invalid")
        .verifying(isBeforeIfDefined(beforeDate))
        .verifying(isAfterIfDefined(afterDate))
        .verifying(isValidAsPerPayFrequency(paymentFrequency, beforeDate))
    )

  private def isBeforeIfDefined(beforeDate: Option[LocalDate])(implicit messages: Messages): Constraint[LocalDate] = Constraint { date =>
    if (beforeDate.forall(date.isBefore(_))) Valid else Invalid("payDate.error.mustBeBefore", dateToString(beforeDate.get))
  }

  private def isAfterIfDefined(afterDate: Option[LocalDate])(implicit messages: Messages): Constraint[LocalDate] = Constraint { date =>
    if (afterDate.forall(date.isAfter(_))) Valid else Invalid("payDate.error.mustBeAfter", dateToString(afterDate.get))
  }

  def isValidAsPerPayFrequency(paymentFrequency: Option[PaymentFrequency], beforeDate: Option[LocalDate])(
    implicit messages: Messages): Constraint[LocalDate] =
    Constraint { inputDate =>
      (beforeDate, paymentFrequency) match {
        case (Some(effectiveStartDate), Some(pf)) => //beforeDate exists meaning we are on pay-date/1 page, so validate the date for meaningful value as per lookback

          val daysToLookBack = pf match {
            case Monthly => 31
            case _ =>
              PaymentFrequency.paymentFrequencyDays(pf)
          }

          val minDate = effectiveStartDate.minusDays(daysToLookBack)

          if (inputDate.isEqualOrAfter(minDate)) Valid
          else Invalid("payDate.error.must.be.as.per.paymentFrequency", dateToString(effectiveStartDate), dateToString(minDate))

        case _ => Valid
      }
    }
}
