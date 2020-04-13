/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.SpecBase
import models.{PayPeriod, PaymentDate}

class MonthlyMaxCalculatorSpec extends SpecBase {

  "Calculates monthly max where pay period is contained in the same month" in new MonthlyMaxCalculator {
    val payPeriod31 = PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31), PaymentDate(LocalDate.of(2020, 3, 31)))
    val payPeriod30 = PayPeriod(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30), PaymentDate(LocalDate.of(2020, 4, 30)))

    calculate(payPeriod31) mustBe 2500.00
    calculate(payPeriod30) mustBe 2500.00
  }

  "Calculates monthly max where pay period spans two calendar months" in new MonthlyMaxCalculator {
    val payPeriod = PayPeriod(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 4, 15), PaymentDate(LocalDate.of(2020, 3, 31)))
    val payPeriodTwo = PayPeriod(LocalDate.of(2020, 4, 20), LocalDate.of(2020, 5, 20), PaymentDate(LocalDate.of(2020, 4, 30)))

    calculate(payPeriod) mustBe 2540.50
    calculate(payPeriodTwo) mustBe 2446.40
  }
}
