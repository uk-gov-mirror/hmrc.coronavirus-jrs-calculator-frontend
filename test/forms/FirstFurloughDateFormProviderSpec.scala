/*
 * Copyright 2021 HM Revenue & Customs
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

import base.SpecBaseControllerSpecs
import forms.behaviours.DateBehaviours
import forms.mappings.LocalDateFormatter
import play.api.data.FormError

class FirstFurloughDateFormProviderSpec extends SpecBaseControllerSpecs {

  val form = new FirstFurloughDateFormProvider()()
  val dateBehaviours = new DateBehaviours
  import dateBehaviours._

  ".value" should {

    "bind valid data" in {

      forAll(firstFurloughDatesGen -> "valid date") { date =>
        val data = Map(
          "value.day"   -> date.getDayOfMonth.toString,
          "value.month" -> date.getMonthValue.toString,
          "value.year"  -> date.getYear.toString,
        )

        val result = form.bind(data)

        result.value.value shouldEqual date

      }
    }

    "fail to bind an empty date" in {
      val result = form.bind(Map.empty[String, String])

      result.errors should contain allElementsOf List(
        FormError(s"value.day", LocalDateFormatter.dayBlankErrorKey),
        FormError(s"value.month", LocalDateFormatter.monthBlankErrorKey),
        FormError(s"value.year", LocalDateFormatter.yearBlankErrorKey),
      )
    }

    "fail with invalid dates" in {

      val data = Map(
        "value.day"   -> "1",
        "value.month" -> "2",
        "value.year"  -> "2020",
      )

      val result = form.bind(data)

      result.errors shouldBe List(
        FormError("value", "firstFurloughStartDate.error.required")
      )
    }

  }
}
