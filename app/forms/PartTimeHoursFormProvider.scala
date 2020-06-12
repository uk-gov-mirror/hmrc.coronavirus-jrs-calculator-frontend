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

import forms.mappings.Mappings
import javax.inject.Inject
import models.{Hours, Periods, UsualHours}
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid}

class PartTimeHoursFormProvider @Inject() extends Mappings {

  def apply(usuals: Seq[UsualHours], partTimePeriod: Periods): Form[Hours] =
    Form(
      mapping(
        "value" -> double(
          requiredKey = "partTimeHours.error.required",
          nonNumericKey = "partTimeHours.error.nonNumeric"
        ).verifying(minimumValue(0.0, "partTimeHours.error.min"))
          .verifying(moreThanUsualHours(usuals, partTimePeriod))
      )(Hours.apply)(Hours.unapply))

  def moreThanUsualHours(usuals: Seq[UsualHours], partTimePeriod: Periods): Constraint[Double] = Constraint { input =>
    usuals
      .find(_.date.isEqual(partTimePeriod.period.end))
      .map { usualHours =>
        if (input <= usualHours.hours.value) {
          Valid
        } else {
          Invalid("partTimeHours.error.max")
        }
      }
      .getOrElse(Valid)
  }

}
