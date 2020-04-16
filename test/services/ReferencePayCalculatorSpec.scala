/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.SpecBase
import models.{Amount, PayPeriod, RegularPayment, Salary}

class ReferencePayCalculatorSpec extends SpecBase {

  "calculates reference gross pay for an employee on variable pays" in new ReferencePayCalculator {
    val employeeStartDate = LocalDate.of(2019, 12, 1)
    val furloughStartDate = LocalDate.of(2020, 3, 1)
    val grossSalary = Amount(2400.0)
    val priorFurloughPeriod = PayPeriod(employeeStartDate, furloughStartDate.minusDays(1))
    val afterFurloughPeriod = PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))
    val afterFurloughPeriodTwo = PayPeriod(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))
    val afterFurloughPartial = PayPeriod(LocalDate.of(2020, 5, 1), LocalDate.of(2020, 5, 15))
    val payPeriods = Seq(afterFurloughPeriod, afterFurloughPeriodTwo, afterFurloughPartial)

    val expected = Seq(
      RegularPayment(Salary(817.47), afterFurloughPeriod),
      RegularPayment(Salary(791.10), afterFurloughPeriodTwo),
      RegularPayment(Salary(395.55), afterFurloughPartial),
    )

    calculateVariablePay(priorFurloughPeriod, payPeriods, grossSalary) mustBe expected
  }

  "calculate daily average gross earning for a given pay period" in new ReferencePayCalculator {
    val employeeStartDate = LocalDate.of(2019, 12, 1)
    val furloughStartDate = LocalDate.of(2020, 3, 1)
    val periodBeforeFurlough = PayPeriod(employeeStartDate, furloughStartDate.minusDays(1))

    averageDailyCalculator(periodBeforeFurlough, Amount(2400.0)) mustBe Amount(26.37)
  }
}
