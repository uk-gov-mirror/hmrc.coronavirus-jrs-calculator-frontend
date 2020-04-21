/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.{LocalDate, ZoneOffset}

import base.SpecBaseWithApplication
import forms.behaviours.DateBehaviours
import play.api.data.FormError

class ClaimPeriodStartFormProviderSpec extends SpecBaseWithApplication {

  val form = new ClaimPeriodStartFormProvider(frontendAppConfig)()
  val dateBehaviours = new DateBehaviours
  import dateBehaviours._

  ".startDate" should {

    val validData = datesBetween(
      min = LocalDate.of(2000, 1, 1),
      max = LocalDate.now(ZoneOffset.UTC)
    )

    behave like dateField(form, "startDate", validData)

    behave like mandatoryDateField(form, "startDate", "claimPeriodStart.error.required.all")

    "bind valid data" in {

      forAll(claimPeriodDatesGen -> "valid date") { date =>
        val data = Map(
          "startDate.day"   -> date.getDayOfMonth.toString,
          "startDate.month" -> date.getMonthValue.toString,
          "startDate.year"  -> date.getYear.toString,
        )

        val result = form.bind(data)

        result.value.value shouldEqual date
      }
    }

    "fail to bind an empty date" in {

      val result = form.bind(Map.empty[String, String])

      result.errors shouldBe List(
        FormError("startDate", "claimPeriodStart.error.required.all"),
      )
    }

    "fail with invalid dates" in {

      val data = Map(
        "startDate.day"   -> "1",
        "startDate.month" -> "2",
        "startDate.year"  -> "2020",
      )

      val result = form.bind(data)

      result.errors shouldBe List(
        FormError("startDate", "claimPeriodStart.error.outofrange", Seq("1 March 2020", "30 June 2020")),
      )
    }
  }
}
