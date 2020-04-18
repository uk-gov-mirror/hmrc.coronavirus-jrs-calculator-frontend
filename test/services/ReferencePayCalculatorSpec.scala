/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.SpecBase
import models.{Amount, FullPeriod, PartialPeriod, PaymentWithPeriod, Period}

class ReferencePayCalculatorSpec extends SpecBase {

  "calculates reference gross pay for an employee on variable pays" in new ReferencePayCalculator {
    val employeeStartDate = LocalDate.of(2019, 12, 1)
    val furloughStartDate = LocalDate.of(2020, 3, 1)
    val grossSalary = Amount(2400.0)
    val priorFurloughPeriod = Period(employeeStartDate, furloughStartDate.minusDays(1))
    val afterFurloughPeriod = FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)))
    val afterFurloughPeriodTwo = FullPeriod(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30)))
    val afterFurloughPartial = PartialPeriod(
      Period(LocalDate.of(2020, 5, 1), LocalDate.of(2020, 5, 31)),
      Period(LocalDate.of(2020, 5, 1), LocalDate.of(2020, 5, 15)))
    val payPeriods = Seq(afterFurloughPeriod, afterFurloughPeriodTwo, afterFurloughPartial)

    val expected = Seq(
      PaymentWithPeriod(Amount(817.47), afterFurloughPeriod),
      PaymentWithPeriod(Amount(791.10), afterFurloughPeriodTwo),
      PaymentWithPeriod(Amount(395.55), afterFurloughPartial),
    )

    calculateVariablePay(priorFurloughPeriod, payPeriods, grossSalary) mustBe expected
  }

  "calculate daily average gross earning for a given pay period" in new ReferencePayCalculator {
    val employeeStartDate = LocalDate.of(2019, 12, 1)
    val furloughStartDate = LocalDate.of(2020, 3, 1)
    val periodBeforeFurlough = Period(employeeStartDate, furloughStartDate.minusDays(1))

    averageDailyCalculator(periodBeforeFurlough, Amount(2400.0)) mustBe 26.37
  }
}
