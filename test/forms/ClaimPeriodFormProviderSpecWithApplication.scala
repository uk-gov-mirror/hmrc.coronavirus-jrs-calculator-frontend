/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import base.SpecBaseWithApplication
import forms.behaviours.DateBehaviours
import play.api.data.FormError

class ClaimPeriodFormProviderSpecWithApplication extends SpecBaseWithApplication {

  val form = new ClaimPeriodFormProvider(frontendAppConfig)
  val dateBehaviours = new DateBehaviours

  import dateBehaviours._

  ".value" should {

    "bind valid data" in {

      forAll(claimPeriodModelGen -> "valid date") { model =>
        val data = Map(
          "startDateValue.day"   -> model.startDate.getDayOfMonth.toString,
          "startDateValue.month" -> model.startDate.getMonthValue.toString,
          "startDateValue.year"  -> model.startDate.getYear.toString,
          "endDateValue.day"     -> model.endDate.getDayOfMonth.toString,
          "endDateValue.month"   -> model.endDate.getMonthValue.toString,
          "endDateValue.year"    -> model.endDate.getYear.toString
        )

        val result = form().bind(data)

        result.value.value shouldEqual model
      }
    }

    "fail to bind an empty date" in {

      val result = form().bind(Map.empty[String, String])

      result.errors shouldBe List(
        FormError("startDateValue", "claimPeriod.start.error.required.all"),
        FormError("endDateValue", "claimPeriod.end.error.required.all")
      )
    }

    "fail with invalid dates" in {

      val data = Map(
        "startDateValue.day"   -> "1",
        "startDateValue.month" -> "2",
        "startDateValue.year"  -> "2020",
        "endDateValue.day"     -> "1",
        "endDateValue.month"   -> "6",
        "endDateValue.year"    -> "2020"
      )

      val result = form().bind(data)

      result.errors shouldBe List(
        FormError("startDateValue", "claimPeriod.start.error.outofrange", Seq("1 March 2020", "31 May 2020")),
        FormError("endDateValue", "claimPeriod.end.error.outofrange", Seq("1 March 2020", "31 May 2020"))
      )
    }
  }
}
