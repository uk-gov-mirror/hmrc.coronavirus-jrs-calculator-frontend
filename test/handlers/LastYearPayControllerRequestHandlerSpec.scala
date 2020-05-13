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

package handlers

import java.time.LocalDate

import base.SpecBase
import utils.CoreTestData

class LastYearPayControllerRequestHandlerSpec extends SpecBase with CoreTestData {

  "get the pay dates in previous year for monthly" in new LastYearPayControllerRequestHandler {
    val userAnswers = variableMonthlyPartial

    val payDates = getPayDates(userAnswers).get

    payDates mustBe Seq(
      LocalDate.of(2019, 3, 20),
      LocalDate.of(2019, 4, 20)
    )
  }

  "get the pay dates in previous year for weekly" in new LastYearPayControllerRequestHandler {
    val userAnswers = variableWeekly()

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
    val userAnswers = variableWeekly("2020-03-28")

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
    val userAnswers = variableFortnightly
    val payDates = getPayDates(userAnswers).get

    val expected = Seq(
      LocalDate.of(2019, 3, 16),
      LocalDate.of(2019, 3, 30),
    )

    payDates mustBe expected
  }

  "get the pay dates in previous year for fourweekly" in new LastYearPayControllerRequestHandler {
    val userAnswers = variableFourweekly

    val payDates = getPayDates(userAnswers).get

    val expected = Seq(
      LocalDate.of(2019, 3, 30),
      LocalDate.of(2019, 4, 27),
    )

    payDates mustBe expected
  }

}
