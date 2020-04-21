/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import base.SpecBase
import models.UserAnswers
import play.api.libs.json.Json
import utils.CoreTestData

class LastYearPayControllerRequestHandlerSpec extends SpecBase with CoreTestData {

  "get the pay dates in previous year for monthly" in new LastYearPayControllerRequestHandler {
    val userAnswers = Json.parse(variableMonthlyPartial).as[UserAnswers]

    val payDates = getPayDates(userAnswers).get

    payDates mustBe Seq(
      LocalDate.of(2019, 3, 20),
      LocalDate.of(2019, 4, 20)
    )
  }

  "get the pay dates in previous year for weekly" in new LastYearPayControllerRequestHandler {
    val userAnswers = Json.parse(variableWeekly()).as[UserAnswers]

    val payDates = getPayDates(userAnswers).get

    val expected = Seq(
      LocalDate.of(2019, 3, 2),
      LocalDate.of(2019, 3, 9),
      LocalDate.of(2019, 3, 16),
      LocalDate.of(2019, 3, 23)
    )

    payDates mustBe expected
  }

  "get the pay dates in previous year for weekly with later pay date" in new LastYearPayControllerRequestHandler {
    val userAnswers = Json.parse(variableWeekly("2020-03-28")).as[UserAnswers]

    val payDates = getPayDates(userAnswers).get

    val expected = Seq(
      LocalDate.of(2019, 3, 9),
      LocalDate.of(2019, 3, 16),
      LocalDate.of(2019, 3, 23),
      LocalDate.of(2019, 3, 30)
    )

    payDates mustBe expected
  }

  "get the pay dates in previous year for fortnightly" in new LastYearPayControllerRequestHandler {
    val userAnswers = Json.parse(variableFortnightly).as[UserAnswers]

    val payDates = getPayDates(userAnswers).get

    val expected = Seq(
      LocalDate.of(2019, 3, 2),
      LocalDate.of(2019, 3, 16),
      LocalDate.of(2019, 3, 30),
    )

    payDates mustBe expected
  }

  "get the pay dates in previous year for fourweekly" in new LastYearPayControllerRequestHandler {
    val userAnswers = Json.parse(variableFourweekly).as[UserAnswers]

    val payDates = getPayDates(userAnswers).get

    val expected = Seq(
      LocalDate.of(2019, 3, 2),
      LocalDate.of(2019, 3, 30),
      LocalDate.of(2019, 4, 27),
    )

    payDates mustBe expected
  }

}
