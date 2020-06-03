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
import forms.mappings.LocalDateFormatter
import play.api.data.FormError
import play.api.data.validation.{Invalid, Valid}
import views.ViewUtils

class ClaimPeriodEndFormProviderSpec extends SpecBaseWithApplication {

  val dateBehaviours = new DateBehaviours
  import dateBehaviours._

  val claimStart = LocalDate.of(2020, 3, 1)

  val form = new ClaimPeriodEndFormProvider(frontendAppConfig)(claimStart)

  ".endDate" should {

    "bind valid data" in {

      val claimPeriodEndDatesGen = for {
        date <- periodDatesBetween(LocalDate.of(2020, 3, 2), LocalDate.of(2020, 3, 2).plusDays(14))
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

      result.errors should contain allElementsOf List(
        FormError("endDate.day", LocalDateFormatter.dayBlankErrorKey),
        FormError("endDate.month", LocalDateFormatter.monthBlankErrorKey),
        FormError("endDate.year", LocalDateFormatter.yearBlankErrorKey),
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

      val form = new ClaimPeriodEndFormProvider(frontendAppConfig)(LocalDate.of(2020, 8, 1))

      val data = Map(
        "endDate.day"   -> "2",
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

    "fail with invalid dates -  less than 7 days after phase two start date and not at the start or end of a month" in {

      val form = new ClaimPeriodEndFormProvider(frontendAppConfig)(LocalDate.of(2020, 7, 2))

      val now = frontendAppConfig.phaseTwoStartDate.plusDays(5)

      val data = Map(
        "endDate.day"   -> now.getDayOfMonth.toString,
        "endDate.month" -> now.getMonthValue.toString,
        "endDate.year"  -> now.getYear.toString
      )

      val result = form.bind(data)

      result.errors shouldBe List(FormError("endDate", "claimPeriodEnd.cannot.be.lessThan.7days"))
    }

    "fail with invalid dates - claim can be less than 7 days in phase two if start or end lines up with month boundary" in {
      val form1 = new ClaimPeriodEndFormProvider(frontendAppConfig)(LocalDate.of(2020, 7, 1))
      val form2 = new ClaimPeriodEndFormProvider(frontendAppConfig)(LocalDate.of(2020, 7, 27))

      val end1 = LocalDate.of(2020, 7, 6)
      val end2 = LocalDate.of(2020, 7, 31)

      val data1 = Map(
        "endDate.day"   -> end1.getDayOfMonth.toString,
        "endDate.month" -> end1.getMonthValue.toString,
        "endDate.year"  -> end1.getYear.toString
      )

      val data2 = Map(
        "endDate.day"   -> end2.getDayOfMonth.toString,
        "endDate.month" -> end2.getMonthValue.toString,
        "endDate.year"  -> end2.getYear.toString
      )

      val result1 = form1.bind(data1)
      val result2 = form2.bind(data2)

      result1.errors shouldBe List()
      result2.errors shouldBe List()
    }

    "fail with invalid dates - if start and end are not of the same calendar month" in {

      val now = LocalDate.of(2020, 6, 15)

      val form = new ClaimPeriodEndFormProvider(frontendAppConfig)(now)

      val data = Map(
        "endDate.day"   -> now.getDayOfMonth.toString,
        "endDate.month" -> now.plusDays(30).getMonthValue.toString,
        "endDate.year"  -> now.getYear.toString
      )

      val result = form.bind(data)

      result.errors shouldBe List(FormError("endDate", "claimPeriodEnd.cannot.be.of.same.month"))
    }
  }

  "start and end should be of the same calendar month" in {
    val form = new ClaimPeriodEndFormProvider(frontendAppConfig)

    form.isDifferentCalendarMonth(LocalDate.of(2020, 7, 1), LocalDate.of(2020, 7, 31)) mustBe Valid

    form.isDifferentCalendarMonth(LocalDate.of(2020, 7, 1), LocalDate.of(2020, 8, 1)) must matchPattern {
      case Invalid(_) =>
    }
  }
}
