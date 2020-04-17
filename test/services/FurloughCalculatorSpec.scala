/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.SpecBase
import models.Calculation.FurloughCalculationResult
import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.{CalculationResult, PayPeriodBreakdown, PaymentDate, Period, PeriodWithPayDay, RegularPayment, Salary}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class FurloughCalculatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  forAll(fullPeriodScenarios) { (frequency, regularPayment, expected) =>
    s"Full Period: For payment frequency $frequency and salary ${regularPayment.salary.amount} should return $expected" in new FurloughCalculator {
      calculateFullPeriod(frequency, regularPayment) mustBe expected
    }
  }

  forAll(partialPeriodScenarios) { (regularPayment, expected) =>
    s"Partial Period: For payment with a salary ${regularPayment.salary.amount} which has been adjusted for a partial period " +
      s"should return $expected" in new FurloughCalculator {
      calculatePartialPeriod(regularPayment) mustBe expected
    }
  }

  "return a CalculationResult with a total and a list of furlough payments for a given list regular payment" in new FurloughCalculator {
    val periodOne =
      PeriodWithPayDay(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)), PaymentDate(LocalDate.of(2020, 3, 31)))
    val periodTwo =
      PeriodWithPayDay(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30)), PaymentDate(LocalDate.of(2020, 4, 30)))
    val paymentOne: RegularPayment = RegularPayment(Salary(2000.00), periodOne.payPeriod)
    val paymentTwo: RegularPayment = RegularPayment(Salary(2000.00), periodTwo.payPeriod)
    val payments: List[RegularPayment] = List(paymentOne, paymentTwo)

    val furloughPeriod = Period(periodOne.payPeriod.start, periodTwo.payPeriod.end)

    val taxYearDate = LocalDate.of(2020, 4, 20)
    val periodTwoWithNewPaymentDate = periodTwo.copy(paymentDate = PaymentDate(taxYearDate))

    val expected =
      CalculationResult(
        FurloughCalculationResult,
        3200.00,
        List(PayPeriodBreakdown(1600.0, periodOne), PayPeriodBreakdown(1600.0, periodTwoWithNewPaymentDate)))

    calculateFurlough(Monthly, payments, furloughPeriod, taxYearDate) mustBe expected
  }

  "return a pay period with new start/end dates if a given furlough period begins/ends in the pay period" in new FurloughCalculator {
    val furloughPeriodOne = Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 5, 30))
    val payPeriodOne = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))
    val expectedOne = Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 3, 31))
    val furloughPeriodTwo = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 25))
    val payPeriodTwo = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))
    val expectedTwo = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 25))
    val furloughPeriodThr = Period(LocalDate.of(2020, 3, 5), LocalDate.of(2020, 3, 28))
    val payPeriodThr = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))
    val expectedThr = Period(LocalDate.of(2020, 3, 5), LocalDate.of(2020, 3, 28))

    payPeriodFromFurloughPeriod(furloughPeriodOne, payPeriodOne) mustBe expectedOne
    payPeriodFromFurloughPeriod(furloughPeriodTwo, payPeriodTwo) mustBe expectedTwo
    payPeriodFromFurloughPeriod(furloughPeriodThr, payPeriodThr) mustBe expectedThr
  }

  private lazy val fullPeriodScenarios = Table(
    ("paymentFrequency", "regularPayment", "expected"),
    (Monthly, RegularPayment(Salary(2000.00), Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))), 1600.00),
    (Monthly, RegularPayment(Salary(5000.00), Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))), 2500.00),
    (Monthly, RegularPayment(Salary(5000.00), Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 4, 15))), 2621.15),
    (Weekly, RegularPayment(Salary(500.00), Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 7))), 400.00),
    (Weekly, RegularPayment(Salary(1000.00), Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 7))), 576.92),
    (FortNightly, RegularPayment(Salary(2000.00), Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 14))), 1153.84),
    (FortNightly, RegularPayment(Salary(1000.00), Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 14))), 800.00),
    (FourWeekly, RegularPayment(Salary(5000.00), Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 28))), 2307.68),
    (FourWeekly, RegularPayment(Salary(2000.00), Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 28))), 1600.00)
  )

  private lazy val partialPeriodScenarios = Table(
    ("regularPayment", "expected"),
    (RegularPayment(Salary(1032.32), Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 3, 31))), 825.86),
    (RegularPayment(Salary(3000.00), Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 3, 31))), 1371.05),
    (RegularPayment(Salary(3000.00), Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 3, 28))), 1129.10)
  )
}
