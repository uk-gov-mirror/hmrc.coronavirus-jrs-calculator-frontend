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
import models.{FullPeriod, Hours, UsualHours}
import play.api.data.FormError
import utils.CoreTestData

class PartTimeHoursFormProviderSpec extends DoubleFieldBehaviours with CoreTestData {

  private val fullPeriodOne: FullPeriod = fullPeriod("2020,3,1", "2020,3,31")
  private val fullPeriodTwo: FullPeriod = fullPeriod("2020,4,1", "2020,4,30")

  private val usuals: Seq[UsualHours] =
    Seq(UsualHours(fullPeriodOne.period.end, Hours(160.0)), UsualHours(fullPeriodTwo.period.end, Hours(160.0)))

  val form = new PartTimeHoursFormProvider()(usuals, fullPeriodOne)

  ".value" must {

    val fieldName = "value"
    val invalidKey = "partTimeHours.error.nonNumeric"
    val requiredKey = "partTimeHours.error.required"
    val maxValue = usuals.head.hours.value

    behave like doubleField(
      form,
      fieldName,
      error = FormError(fieldName, invalidKey),
      Some(maxValue)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "validate if part time hours is more than usual hours" in {
    val data: Map[String, String] =
      Map("value" -> (usuals.head.hours.value + 1).toString)

    form.bind(data).errors.size shouldBe 1
    form.bind(data).errors.head.message shouldBe "partTimeHours.error.max"
  }
}
