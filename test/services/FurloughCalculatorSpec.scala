/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.SpecBase
import models.Calculation.FurloughCalculationResult
import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.{Amount, CalculationResult, FullPeriod, PartialPeriod, PaymentDate, PaymentWithPeriod, Period, PeriodBreakdown, PeriodWithPaymentDate}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class FurloughCalculatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  forAll(fullPeriodScenarios) { (frequency, payment, expectedFurlough) =>
    s"Full Period: For payment frequency $frequency and payment ${payment.amount.value} should return $expectedFurlough" in new FurloughCalculator {
      val period = payment.period.asInstanceOf[FullPeriod]
      val expected =
        PeriodBreakdown(payment.amount, expectedFurlough, PeriodWithPaymentDate(payment.period, PaymentDate(period.period.end)))
      calculatePeriodBreakdown(frequency, payment.amount, PeriodWithPaymentDate(payment.period, PaymentDate(period.period.end))) mustBe expected
    }
  }

  forAll(partialPeriodScenarios) { (frequency, payment, expectedFurlough) =>
    s"Partial Period: For payment with a payment ${payment.amount.value} which has been adjusted for a partial period " +
      s"should return $expectedFurlough" in new FurloughCalculator {
      val period = payment.period.asInstanceOf[PartialPeriod]
      val expected =
        PeriodBreakdown(payment.amount, expectedFurlough, PeriodWithPaymentDate(payment.period, PaymentDate(period.original.end)))
      calculatePeriodBreakdown(frequency, payment.amount, PeriodWithPaymentDate(payment.period, PaymentDate(period.original.end))) mustBe expected
    }
  }

  "return a CalculationResult with a total and a list of furlough payments for a given list regular payment" in new FurloughCalculator {
    val periodOne =
      PeriodWithPaymentDate(FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))), PaymentDate(LocalDate.of(2020, 3, 31)))
    val periodTwo =
      PeriodWithPaymentDate(FullPeriod(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))), PaymentDate(LocalDate.of(2020, 4, 30)))
    val paymentOne: PaymentWithPeriod = PaymentWithPeriod(Amount(2000.00), periodOne.period)
    val paymentTwo: PaymentWithPeriod = PaymentWithPeriod(Amount(2000.00), periodTwo.period)
    val payments: List[PaymentWithPeriod] = List(paymentOne, paymentTwo)

    val furloughPeriod = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 4, 30))

    val taxYearDate = LocalDate.of(2020, 4, 20)
    val periodTwoWithNewPaymentDate = periodTwo.copy(paymentDate = PaymentDate(taxYearDate))

    val expected =
      CalculationResult(
        FurloughCalculationResult,
        3200.00,
        List(
          PeriodBreakdown(Amount(2000.00), Amount(1600.00), periodOne),
          PeriodBreakdown(Amount(2000.00), Amount(1600.00), periodTwoWithNewPaymentDate)
        )
      )

    calculateFurloughGrant(Monthly, payments, furloughPeriod, taxYearDate) mustBe expected
  }

  private lazy val fullPeriodScenarios = Table(
    ("paymentFrequency", "payment", "expectedFurlough"),
    (Monthly, PaymentWithPeriod(Amount(2000.00), FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)))), Amount(1600.00)),
    (Monthly, PaymentWithPeriod(Amount(5000.00), FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)))), Amount(2500.00)),
    (
      Monthly,
      PaymentWithPeriod(Amount(5000.00), FullPeriod(Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 4, 15)))),
      Amount(2621.15)),
    (Weekly, PaymentWithPeriod(Amount(500.00), FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 7)))), Amount(400.00)),
    (Weekly, PaymentWithPeriod(Amount(1000.00), FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 7)))), Amount(576.92)),
    (
      FortNightly,
      PaymentWithPeriod(Amount(2000.00), FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 14)))),
      Amount(1153.84)),
    (
      FortNightly,
      PaymentWithPeriod(Amount(1000.00), FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 14)))),
      Amount(800.00)),
    (
      FourWeekly,
      PaymentWithPeriod(Amount(5000.00), FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 28)))),
      Amount(2307.68)),
    (
      FourWeekly,
      PaymentWithPeriod(Amount(2000.00), FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 28)))),
      Amount(1600.00))
  )

  private lazy val partialPeriodScenarios = Table(
    ("frequency", "payment", "expectedFurlough"),
    (
      Monthly,
      PaymentWithPeriod(
        Amount(1032.32),
        PartialPeriod(
          Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)),
          Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 3, 31)))),
      Amount(825.86)),
    (
      Monthly,
      PaymentWithPeriod(
        Amount(3000.00),
        PartialPeriod(
          Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)),
          Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 3, 31)))),
      Amount(1371.05)),
    (
      Monthly,
      PaymentWithPeriod(
        Amount(3000.00),
        PartialPeriod(
          Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)),
          Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 3, 28)))),
      Amount(1129.10))
  )
}
