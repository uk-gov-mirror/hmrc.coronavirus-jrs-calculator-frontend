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

  ".firstFurloughDate" should {

    "bind valid data" in {

      forAll(firstFurloughDatesGen -> "valid date") { date =>
        val data = Map(
          "firstFurloughDate.day"   -> date.getDayOfMonth.toString,
          "firstFurloughDate.month" -> date.getMonthValue.toString,
          "firstFurloughDate.year"  -> date.getYear.toString,
        )

        val result = form.bind(data)

        result.value.value shouldEqual date

      }
    }

    "fail to bind an empty date" in {
      val result = form.bind(Map.empty[String, String])

      result.errors should contain allElementsOf List(
        FormError(s"firstFurloughDate.day", LocalDateFormatter.dayBlankErrorKey),
        FormError(s"firstFurloughDate.month", LocalDateFormatter.monthBlankErrorKey),
        FormError(s"firstFurloughDate.year", LocalDateFormatter.yearBlankErrorKey),
      )
    }

    "fail with invalid dates" in {

      val data = Map(
        "firstFurloughDate.day"   -> "1",
        "firstFurloughDate.month" -> "2",
        "firstFurloughDate.year"  -> "2020",
      )

      val result = form.bind(data)

      result.errors shouldBe List(
        FormError("firstFurloughDate", "firstFurloughStartDate.error.required")
      )
    }

  }
}
