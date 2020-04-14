/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.{LocalDate, ZoneOffset}

import base.SpecBaseWithApplication
import forms.behaviours.DateBehaviours
import play.api.data.FormError

class ClaimPeriodEndFormProviderSpec extends SpecBaseWithApplication {

  val form = new ClaimPeriodEndFormProvider(frontendAppConfig)()
  val dateBehaviours = new DateBehaviours
  import dateBehaviours._

  ".endDate" should {

    val validData = datesBetween(
      min = LocalDate.of(2000, 1, 1),
      max = LocalDate.now(ZoneOffset.UTC)
    )

    behave like dateField(form, "endDate", validData)

    behave like mandatoryDateField(form, "endDate", "claimPeriodEnd.error.required.all")

    "bind valid data" in {

      forAll(claimPeriodDatesGen -> "valid date") { date =>
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

    "fail with invalid dates" in {

      val data = Map(
        "endDate.day"   -> "1",
        "endDate.month" -> "2",
        "endDate.year"  -> "2020",
      )

      val result = form.bind(data)

      result.errors shouldBe List(
        FormError("endDate", "claimPeriodEnd.error.outofrange", Seq("1 March 2020", "31 May 2020")),
      )
    }
  }
}
