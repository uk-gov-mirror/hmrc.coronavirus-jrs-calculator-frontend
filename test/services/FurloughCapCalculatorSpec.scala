/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.{LocalDate, Month}

import base.SpecBase
import models.Period
import models.PaymentFrequency.Monthly

class FurloughCapCalculatorSpec extends SpecBase {

  "Calculates monthly max where pay period is contained in the same month" in new FurloughCapCalculator {
    val payPeriod31 = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))
    val payPeriod30 = Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))

    furloughCap(Monthly, payPeriod31) mustBe 2500.00
    furloughCap(Monthly, payPeriod30) mustBe 2500.00
  }

  "Calculates partial furlough cap" in new FurloughCapCalculator {
    val periodOne = Period(LocalDate.of(2020, 3, 10), LocalDate.of(2020, 3, 31))
    val periodTwo = Period(LocalDate.of(2020, 3, 21), LocalDate.of(2020, 4, 10))

    partialFurloughCap(periodOne) mustBe 1774.30
    partialFurloughCap(periodTwo) mustBe 1720.55
  }

  "Calculates monthly max where pay period spans two calendar months" in new FurloughCapCalculator {
    val payPeriod = Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 4, 15))
    val payPeriodTwo = Period(LocalDate.of(2020, 4, 20), LocalDate.of(2020, 5, 20))

    furloughCap(Monthly, payPeriod) mustBe 2621.15
    furloughCap(Monthly, payPeriodTwo) mustBe 2529.74
  }

  "returns daily max for a given month" in new FurloughCapCalculator {
    dailyMax(Month.MARCH) mustBe 80.65
    dailyMax(Month.APRIL) mustBe 83.34
    dailyMax(Month.MAY) mustBe 80.65
  }
}
