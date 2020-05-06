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
import play.api.data.Form
import views.ViewUtils._

class FurloughStartDateFormProvider @Inject()(appConfig: FrontendAppConfig) extends Mappings {

  def apply(claimPeriodEnd: LocalDate): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey = "furloughStartDate.error.invalid",
        allRequiredKey = "furloughStartDate.error.required.all",
        twoRequiredKey = "furloughStartDate.error.required.two",
        requiredKey = "furloughStartDate.error.required"
      ).verifying(minDate(appConfig.schemeStartDate, "furloughStartDate.error.minimum", dateToString(appConfig.schemeStartDate)))
        .verifying(maxDate(claimPeriodEnd.minusDays(1), "furloughStartDate.error.maximum", dateToString(claimPeriodEnd)))
    )
}
