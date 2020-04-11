/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.LocalDate

import base.SpecBase
import forms.behaviours.DateBehaviours
import models.ClaimPeriodModel
import play.api.data.FormError

class ClaimPeriodFormProviderSpec extends SpecBase {

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

    "fail with a start date that is too early" in {

      val claimPeriodModelStartTooEarlyGen = for {
        startDate <- datesBetween(frontendAppConfig.schemeStartDate.minusYears(1), frontendAppConfig.schemeStartDate)
        endDate   <- datesBetween(frontendAppConfig.schemeStartDate.plusDays(1), frontendAppConfig.schemeEndDate)
      } yield ClaimPeriodModel(startDate, endDate)

      forAll(claimPeriodModelStartTooEarlyGen -> "early start date") { model =>
        val data = Map(
          "startDateValue.day"   -> model.startDate.getDayOfMonth.toString,
          "startDateValue.month" -> model.startDate.getMonthValue.toString,
          "startDateValue.year"  -> model.startDate.getYear.toString,
          "endDateValue.day"     -> model.endDate.getDayOfMonth.toString,
          "endDateValue.month"   -> model.endDate.getMonthValue.toString,
          "endDateValue.year"    -> model.endDate.getYear.toString
        )

        val result = form().bind(data)

        result.errors should contain only FormError("startDateValue", "claimPeriod.start.error.outofrange", Seq("1 March 2020", "31 May 2020"))
      }
    }

    "fail with an end date that is too late" in {

      val claimPeriodModelStartTooEarlyGen = for {
        startDate <- datesBetween(frontendAppConfig.schemeStartDate, frontendAppConfig.schemeEndDate.minusDays(1))
        endDate   <- datesBetween(frontendAppConfig.schemeEndDate.plusDays(1), frontendAppConfig.schemeEndDate.plusYears(1))
      } yield ClaimPeriodModel(startDate, endDate)

      forAll(claimPeriodModelStartTooEarlyGen -> "late end date") { model =>
        val data = Map(
          "startDateValue.day"   -> model.startDate.getDayOfMonth.toString,
          "startDateValue.month" -> model.startDate.getMonthValue.toString,
          "startDateValue.year"  -> model.startDate.getYear.toString,
          "endDateValue.day"     -> model.endDate.getDayOfMonth.toString,
          "endDateValue.month"   -> model.endDate.getMonthValue.toString,
          "endDateValue.year"    -> model.endDate.getYear.toString
        )

        val result = form().bind(data)

        result.errors should contain only FormError("endDateValue", "claimPeriod.end.error.outofrange", Seq("1 March 2020", "31 May 2020"))
      }
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
