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

package services

import java.time.{LocalDate, Month}

import base.SpecBase
import models.{FullPeriodCap, PartialPeriodCap, Period, PeriodSpansMonthCap}
import models.PaymentFrequency.Monthly

class FurloughCapCalculatorSpec extends SpecBase {

  "Calculates monthly max where pay period is contained in the same month" in new FurloughCapCalculator {
    val payPeriod31 = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))
    val payPeriod30 = Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))

    furloughCap(Monthly, payPeriod31) mustBe FullPeriodCap(2500.00)
    furloughCap(Monthly, payPeriod30) mustBe FullPeriodCap(2500.00)
  }

  "Calculates partial furlough cap" in new FurloughCapCalculator {
    val periodOne = Period(LocalDate.of(2020, 3, 10), LocalDate.of(2020, 3, 31))
    val periodTwo = Period(LocalDate.of(2020, 3, 21), LocalDate.of(2020, 4, 10))

    partialFurloughCap(periodOne) mustBe PartialPeriodCap(1774.30, 22, 3, 80.65)
    partialFurloughCap(periodTwo) mustBe PeriodSpansMonthCap(1720.55, 11, 3, 80.65, 10, 4, 83.34)
  }

  "Calculates monthly max where pay period spans two calendar months" in new FurloughCapCalculator {
    val payPeriod = Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 4, 15))
    val payPeriodTwo = Period(LocalDate.of(2020, 4, 20), LocalDate.of(2020, 5, 20))

    furloughCap(Monthly, payPeriod) mustBe PeriodSpansMonthCap(2621.15, 17, 3, 80.65, 15, 4, 83.34)
    furloughCap(Monthly, payPeriodTwo) mustBe PeriodSpansMonthCap(2529.74, 11, 4, 83.34, 20, 5, 80.65)
  }

  "returns daily max for a given month" in new FurloughCapCalculator {
    dailyMax(Month.MARCH) mustBe 80.65
    dailyMax(Month.APRIL) mustBe 83.34
    dailyMax(Month.MAY) mustBe 80.65
  }
}
