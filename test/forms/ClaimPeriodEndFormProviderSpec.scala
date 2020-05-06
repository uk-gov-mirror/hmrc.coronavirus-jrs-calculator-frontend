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
import views.ViewUtils

class ClaimPeriodEndFormProviderSpec extends SpecBaseWithApplication {

  val dateBehaviours = new DateBehaviours
  import dateBehaviours._

  val claimStart = LocalDate.of(2020, 3, 1)

  val form = new ClaimPeriodEndFormProvider(frontendAppConfig)(claimStart)

  ".endDate" should {

    val validData = datesBetween(
      min = LocalDate.of(2020, 3, 2),
      max = LocalDate.now().plusDays(14)
    )

    behave like dateField(form, "endDate", validData)

    behave like mandatoryDateField(form, "endDate", "claimPeriodEnd.error.required.all")

    "bind valid data" in {

      val claimPeriodEndDatesGen = for {
        date <- periodDatesBetween(LocalDate.of(2020, 3, 2), LocalDate.now().plusDays(14))
      } yield date

      forAll(claimPeriodEndDatesGen -> "valid date") { date =>
        val data = Map(
          "endDate.day"   -> date.getDayOfMonth.toString,
          "endDate.month" -> date.getMonthValue.toString,
          "endDate.year"  -> date.getYear.toString,
        )

        val result = form.bind(data)

        result.value.value shouldEqual date
      }
    }

    "fail to bind an empty date" in {

      val result = form.bind(Map.empty[String, String])

      result.errors shouldBe List(
        FormError("endDate", "claimPeriodEnd.error.required.all"),
      )
    }

    "fail with invalid dates -  before claim-start" in {

      val data = Map(
        "endDate.day"   -> "1",
        "endDate.month" -> "2",
        "endDate.year"  -> "2020",
      )

      val result = form.bind(data)

      result.errors shouldBe List(FormError("endDate", "claimPeriodEnd.cannot.be.before.claimStart"))
    }

    "fail with invalid dates -  after policy end" in {

      val data = Map(
        "endDate.day"   -> "1",
        "endDate.month" -> "8",
        "endDate.year"  -> "2020",
      )

      val result = form.bind(data)

      result.errors shouldBe List(
        FormError(
          "endDate",
          "claimPeriodEnd.cannot.be.after.policyEnd",
          Seq(ViewUtils.dateToString(frontendAppConfig.schemeEndDate))
        ))
    }

    "fail with invalid dates -  more than today + 14 days" in {

      val now = LocalDate.now().plusDays(15)

      val data = Map(
        "endDate.day"   -> now.getDayOfMonth.toString,
        "endDate.month" -> now.getMonthValue.toString,
        "endDate.year"  -> now.getYear.toString,
      )

      val result = form.bind(data)

      result.errors shouldBe List(FormError("endDate", "claimPeriodEnd.cannot.be.after.14days"))
    }
  }
}
