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

import forms.behaviours.DoubleFieldBehaviours
import models.FullPeriod
import play.api.data.FormError
import utils.CoreTestData

class PartTimeNormalHoursFormProviderSpec extends DoubleFieldBehaviours with CoreTestData {

  private val fullPeriodOne: FullPeriod = fullPeriod("2020,7,1", "2020,7,31")
  val form = new PartTimeNormalHoursFormProvider()(fullPeriodOne)

  ".value" must {

    val fieldName = "value"
    val invalidKey = "partTimeNormalHours.error.nonNumeric"
    val requiredKey = "partTimeNormalHours.error.required"

    behave like doubleField(
      form,
      fieldName,
      error = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "validate if normal hours is more than max allowed (eg 24*2=48) for 2 days" in {
    val twoDaysPartTime: FullPeriod = fullPeriod("2020,7,1", "2020,7,2")
    val form = new PartTimeNormalHoursFormProvider()(twoDaysPartTime)
    val hours = twoDaysPartTime.period.countHours

    val data: Map[String, String] =
      Map("value" -> (hours + 1).toString)

    form.bind(data).errors.size shouldBe 1
    form.bind(data).errors.head.message shouldBe "partTimeNormalHours.error.max"
  }
}
