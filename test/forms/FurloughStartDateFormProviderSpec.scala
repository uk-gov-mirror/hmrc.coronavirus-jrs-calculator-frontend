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

import base.SpecBaseWithApplication
import forms.behaviours.DateBehaviours
import play.api.data.FormError

class FurloughStartDateFormProviderSpec extends SpecBaseWithApplication {

  val dateBehaviours = new DateBehaviours
  import dateBehaviours._

  private val startDate = LocalDate.of(2020, 3, 1)
  private val endDate = LocalDate.of(2020, 6, 1)
  val form = new FurloughStartDateFormProvider(frontendAppConfig)(endDate)

  ".value" should {

    val validData = datesBetween(
      min = startDate,
      max = endDate.minusDays(1)
    )

    behave like dateField(form, "value", validData)

    behave like dateFieldWithMin(form, "value", startDate, FormError("value", "furloughStartDate.error.minimum", Array("1 March 2020")))

    behave like dateFieldWithMax(form, "value", endDate, FormError("value", "furloughStartDate.error.maximum", Array("1 May 2020")))

    behave like mandatoryDateField(form, "value")
  }
}
