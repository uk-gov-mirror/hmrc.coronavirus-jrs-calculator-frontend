/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.{LocalDate, Month}

import base.SpecBase
import models.PayPeriod
import models.PaymentFrequency.Monthly

class FurloughCapCalculatorSpec extends SpecBase {

  "Calculates monthly max where pay period is contained in the same month" in new FurloughCapCalculator {
    val payPeriod31 = PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))
    val payPeriod30 = PayPeriod(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))

    furloughCap(Monthly, payPeriod31) mustBe 2500.00
    furloughCap(Monthly, payPeriod30) mustBe 2500.00
  }

  "Calculates partial furlough cap" in new FurloughCapCalculator {
    val periodOne = PayPeriod(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 3, 31))
    val periodTwo = PayPeriod(LocalDate.of(2020, 3, 21), LocalDate.of(2020, 4, 10))

    partialFurloughCap(periodOne) mustBe 1290.40
    partialFurloughCap(periodTwo) mustBe 1639.90
  }

  "Calculates monthly max where pay period spans two calendar months" in new FurloughCapCalculator {
    val payPeriod = PayPeriod(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 4, 15))
    val payPeriodTwo = PayPeriod(LocalDate.of(2020, 4, 20), LocalDate.of(2020, 5, 20))

    furloughCap(Monthly, payPeriod) mustBe 2540.50
    furloughCap(Monthly, payPeriodTwo) mustBe 2446.40
  }

  "returns daily max for a given month" in new FurloughCapCalculator {
    dailyMax(Month.MARCH) mustBe 80.65
    dailyMax(Month.APRIL) mustBe 83.34
    dailyMax(Month.MAY) mustBe 80.65
  }
}
