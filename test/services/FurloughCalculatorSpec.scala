/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.SpecBase
import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.{FurloughPayment, PayPeriod, PaymentDate, RegularPayment}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class FurloughCalculatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  forAll(scenarios) { (frequency, salary, expected) =>
    s"For payment frequency $frequency and salary $salary should return $expected" in new FurloughCalculator {
      calculate(frequency, salary) mustBe expected
    }
  }

  "return a list of furlough payments for a list regular payment" in new FurloughCalculator {
    val periodOne = PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31), PaymentDate(LocalDate.of(2020, 3, 31)))
    val periodTwo = PayPeriod(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30), PaymentDate(LocalDate.of(2020, 4, 30)))
    val paymentOne: RegularPayment = RegularPayment(Salary(2000.00), periodOne)
    val paymentTwo: RegularPayment = RegularPayment(Salary(2000.00), periodTwo)
    val payments: List[RegularPayment] = List(paymentOne, paymentTwo)
    val expected =
      List(FurloughPayment(1600.0, PaymentDate(LocalDate.of(2020, 3, 31))), FurloughPayment(1600.0, PaymentDate(LocalDate.of(2020, 4, 30))))

    calculateMultiple(Monthly, payments) mustBe expected
  }

  private lazy val scenarios = Table(
    ("paymentFrequency", "Salary", "expected"),
    (Monthly, Salary(2000.00), 1600.0),
    (Monthly, Salary(5000.00), 2500.0),
    (Weekly, Salary(500.00), 400.0),
    (Weekly, Salary(1000.00), 576.92),
    (FortNightly, Salary(2000.00), 1153.84),
    (FortNightly, Salary(1000.00), 800.0),
    (FourWeekly, Salary(4500.00), 2307.68),
    (FourWeekly, Salary(2000.00), 1600.00)
  )
}
