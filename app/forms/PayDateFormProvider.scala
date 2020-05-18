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
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid}
import views.ViewUtils

class PayDateFormProvider @Inject() extends Mappings {

  def apply(beforeDate: Option[LocalDate] = None, afterDate: Option[LocalDate] = None): Form[LocalDate] =
    Form(
      "value" -> localDate(invalidKey = "payDate.error.invalid")
        .verifying(isBeforeIfDefined(beforeDate))
        .verifying(isAfterIfDefined(afterDate))
    )

  private def isBeforeIfDefined(beforeDate: Option[LocalDate]): Constraint[LocalDate] = Constraint { date =>
    if (beforeDate.forall(date.isBefore(_))) Valid else Invalid("payDate.error.mustBeBefore", ViewUtils.dateToString(beforeDate.get))
  }

  private def isAfterIfDefined(afterDate: Option[LocalDate]): Constraint[LocalDate] = Constraint { date =>
    if (afterDate.forall(date.isAfter(_))) Valid else Invalid("payDate.error.mustBeAfter", ViewUtils.dateToString(afterDate.get))
  }
}
