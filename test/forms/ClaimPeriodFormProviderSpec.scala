/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.DateBehaviours
import play.api.data.FormError

class ClaimPeriodFormProviderSpec extends DateBehaviours {

  val form = new ClaimPeriodFormProvider()()
  ".value" should {

    "bind valid data" in {

      forAll(claimPeriodModelGen -> "valid date") {
        model =>
          val data = Map(
            "startDateValue.day" -> model.startDate.getDayOfMonth.toString,
            "startDateValue.month" -> model.startDate.getMonthValue.toString,
            "startDateValue.year" -> model.startDate.getYear.toString,
            "endDateValue.day" -> model.endDate.getDayOfMonth.toString,
            "endDateValue.month" -> model.endDate.getMonthValue.toString,
            "endDateValue.year" -> model.endDate.getYear.toString
          )

          val result = form.bind(data)

          result.value.value shouldEqual model
      }
    }

    "fail to bind an empty date" in {

      val result = form.bind(Map.empty[String, String])

      result.errors shouldBe List(
        FormError("startDateValue", "claimPeriod.start.error.required.all"),
        FormError("endDateValue", "claimPeriod.end.error.required.all")
      )
    }
  }
}
