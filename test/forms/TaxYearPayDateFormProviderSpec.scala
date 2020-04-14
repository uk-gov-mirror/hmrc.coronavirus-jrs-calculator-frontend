/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.{LocalDate, ZoneOffset}

import base.SpecBaseWithApplication
import forms.behaviours.DateBehaviours
import generators.ModelGenerators
import play.api.data.FormError

class TaxYearPayDateFormProviderSpec extends SpecBaseWithApplication with ModelGenerators {

  val form = new TaxYearPayDateFormProvider()()
  val dateBehaviours = new DateBehaviours
  import dateBehaviours._

  ".payDate" should {

    val validData = datesBetween(
      min = LocalDate.of(2000, 1, 1),
      max = LocalDate.now(ZoneOffset.UTC)
    )

    behave like dateField(form, "payDate", validData)

    behave like mandatoryDateField(form, "payDate", "taxYearPayDate.error.required.all")

    val taxYearPayDateGen = for {
      date <- periodDatesBetween(LocalDate.of(2020, 4, 7), LocalDate.of(2020, 5, 31))
    } yield date

    "bind valid data" in {

      forAll(taxYearPayDateGen -> "valid date") { date =>
        val data = Map(
          "payDate.day"   -> date.getDayOfMonth.toString,
          "payDate.month" -> date.getMonthValue.toString,
          "payDate.year"  -> date.getYear.toString,
        )

        val result = form.bind(data)

        result.value.value shouldEqual date
      }
    }

    "fail to bind an empty date" in {

      val result = form.bind(Map.empty[String, String])

      result.errors shouldBe List(
        FormError("payDate", "taxYearPayDate.error.required.all"),
      )
    }

    "fail with invalid dates" in {

      val data = Map(
        "payDate.day"   -> "1",
        "payDate.month" -> "2",
        "payDate.year"  -> "2020",
      )

      val result = form.bind(data)

      result.errors shouldBe List(
        FormError("payDate", "taxYearPayDate.error.outofrange", Seq("7 April 2020", "31 May 2020")),
      )
    }
  }
}
