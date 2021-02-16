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

package handlers

import base.SpecBase
import cats.scalatest.ValidatedValues
import utils.CoreTestData

class LastYearPayControllerRequestHandlerSpec extends SpecBase with CoreTestData with ValidatedValues {

  "get the pay periods in previous year for monthly" in new LastYearPayControllerRequestHandler {
    val periods = getLastYearPeriods(variableMonthlyPartial).value

    periods mustBe Seq(
      period("2019, 3, 1", "2019, 3, 31"),
      period("2019, 4, 1", "2019, 4, 30")
    )
  }

  "get the pay periods in previous year for weekly" in new LastYearPayControllerRequestHandler {
    val periods = getLastYearPeriods(variableWeekly).value

    val expected = Seq(
      period("2019, 2, 24", "2019, 3, 2"),
      period("2019, 3, 3", "2019, 3, 9"),
      period("2019, 3, 10", "2019, 3, 16"),
      period("2019, 3, 17", "2019, 3, 23")
    )

    periods mustBe expected
  }

  "get the pay periods in previous year taking into account leap year" in new LastYearPayControllerRequestHandler {
    val periods = getLastYearPeriods(cylbLeapYear).value

    val expected = Seq(
      period("2019, 2, 20", "2019, 3, 5"),
      period("2019, 3, 6", "2019, 3, 19"),
      period("2019, 3, 20", "2019, 4, 2")
    )

    periods mustBe expected
  }

  "get the pay periods in previous year taking into account leap year when calculating back from March 2021" in new LastYearPayControllerRequestHandler {
    val periods = getLastYearPeriods(cylbMarch2021Year).value

    val expected = Seq(
      period("2019, 2, 21", "2019, 3, 6"),
      period("2019, 3, 7", "2019, 3, 20"),
      period("2019, 3, 21", "2019, 4, 3")
    )

    periods mustBe expected
  }

  "get the pay periods in previous year taking into account leap year when calculating back from March 2022" in new LastYearPayControllerRequestHandler {
    val periods = getLastYearPeriods(cylbMarch2022Year).value

    val expected = Seq(
      period("2019, 2, 22", "2019, 3, 7"),
      period("2019, 3, 8", "2019, 3, 21"),
      period("2019, 3, 22", "2019, 4, 4")
    )

    periods mustBe expected
  }

  "get the pay periods in previous year for fortnightly" in new LastYearPayControllerRequestHandler {
    val periods = getLastYearPeriods(variableFortnightly).value

    val expected = Seq(
      period("2019, 3, 3", "2019, 3, 16"),
      period("2019, 3, 17", "2019, 3, 30")
    )

    periods mustBe expected
  }

  "get the pay periods in previous year for fourweekly" in new LastYearPayControllerRequestHandler {
    val periods = getLastYearPeriods(variableFourweekly).value

    val expected = Seq(
      period("2019, 3, 3", "2019, 3, 30"),
      period("2019, 3, 31", "2019, 4, 27")
    )

    periods mustBe expected
  }

}
