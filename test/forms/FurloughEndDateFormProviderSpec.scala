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

import forms.behaviours.DateBehaviours
import play.api.data.FormError

class FurloughEndDateFormProviderSpec extends DateBehaviours {

  private val startDate = LocalDate.of(2020, 3, 1)
  private val endDate = LocalDate.of(2020, 5, 1)
  private val furloughStart = startDate

  ".value" should {

    val form = new FurloughEndDateFormProvider()(endDate, furloughStart)

    val validData = datesBetween(
      min = startDate.plusDays(20),
      max = startDate.plusDays(31)
    )

    behave like dateField(form, "value", validData)

    behave like dateFieldWithMax(form, "value", endDate, FormError("value", "furloughEndDate.error.min.max"))

    behave like mandatoryDateField(form, "value")
  }
}
