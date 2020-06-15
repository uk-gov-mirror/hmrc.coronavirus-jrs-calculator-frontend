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

import config.FrontendAppConfig
import forms.mappings.Mappings
import javax.inject.Inject
import models.Period
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid}

class FurloughEndDateFormProvider @Inject()(appConfig: FrontendAppConfig) extends Mappings {

  def apply(claimPeriod: Period, furloughStart: LocalDate): Form[LocalDate] =
    Form(
      "value" -> localDate(invalidKey = "furloughEndDate.error.invalid")
        .verifying(validEndDate(claimPeriod, furloughStart))
    )

  private def validEndDate(claimPeriod: Period, furloughStart: LocalDate): Constraint[LocalDate] = Constraint {
    case end if claimPeriodPredicate(claimPeriod, end)                  => Invalid("furloughEndDate.error.claimPeriod")
    case end if twentyOneDaysPredicate(claimPeriod, furloughStart, end) => Invalid("furloughEndDate.error.min.max")
    case _                                                              => Valid
  }

  private def claimPeriodPredicate(claimPeriod: Period, furloughEnd: LocalDate) =
    furloughEnd.isBefore(claimPeriod.start) || furloughEnd.isAfter(claimPeriod.end)

  private def twentyOneDaysPredicate(claimPeriod: Period, furloughStart: LocalDate, furloughEnd: LocalDate) =
    !isPhaseTwo(claimPeriod) && furloughEnd.isBefore(furloughStart.plusDays(20))

  private def isPhaseTwo(claimPeriod: Period): Boolean =
    claimPeriod.start.getMonthValue >= appConfig.phaseTwoStartDate.getMonthValue
}
