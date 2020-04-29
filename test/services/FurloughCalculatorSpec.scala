/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.SpecBase
import models.Calculation.FurloughCalculationResult
import models.PayQuestion.Regularly
import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.{Amount, CalculationResult, FullPeriod, FullPeriodBreakdown, FullPeriodWithPaymentDate, PartialPeriod, PartialPeriodBreakdown, PartialPeriodWithPaymentDate, PaymentDate, PaymentWithPeriod, Period, PeriodWithPaymentDate}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class FurloughCalculatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  forAll(fullPeriodScenarios) { (frequency, payment, expectedFurlough) =>
    val fullPeriod: FullPeriod = payment.period.period.asInstanceOf[FullPeriod]

    s"Full Period: For payment frequency $frequency and payment ${payment.furloughPayment.value} return $expectedFurlough" in new FurloughCalculator {
      calculateFullPeriod(frequency, payment, fullPeriod) mustBe expectedFurlough
    }
  }

  forAll(partialPeriodScenarios) { (payment, expectedFurlough) =>
    s"Partial Period: For gross payment: ${payment.furloughPayment.value} " +
      s"should return $expectedFurlough" in new FurloughCalculator {
      val period = payment.period.period.asInstanceOf[PartialPeriod]
      val expected =
        PartialPeriodBreakdown(payment.nonFurloughPay, expectedFurlough, payment.period.asInstanceOf[PartialPeriodWithPaymentDate])
      calculatePartialPeriod(payment, period, payment.period.paymentDate) mustBe expected
    }
  }

  "return a CalculationResult with a total and a list of furlough payments for a given list regular payment" in new FurloughCalculator {
    val periodOne =
      FullPeriodWithPaymentDate(
        FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
        PaymentDate(LocalDate.of(2020, 3, 31)))
    val periodTwo =
      FullPeriodWithPaymentDate(
        FullPeriod(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))),
        PaymentDate(LocalDate.of(2020, 4, 30)))
    val paymentOne: PaymentWithPeriod = PaymentWithPeriod(Amount(0.0), Amount(2000.00), periodOne, Regularly)
    val paymentTwo: PaymentWithPeriod = PaymentWithPeriod(Amount(0.0), Amount(2000.00), periodTwo, Regularly)
    val payments: List[PaymentWithPeriod] = List(paymentOne, paymentTwo)

    val expected =
      CalculationResult(
        FurloughCalculationResult,
        3200.00,
        List(
          FullPeriodBreakdown(Amount(1600.00), periodOne),
          FullPeriodBreakdown(Amount(1600.00), periodTwo)
        )
      )

    calculateFurloughGrant(Monthly, payments) mustBe expected
  }

  private lazy val fullPeriodScenarios = Table(
    ("paymentFrequency", "payment", "expectedFurlough"),
    (
      Monthly,
      PaymentWithPeriod(
        Amount(0.0),
        Amount(2000.00),
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
          PaymentDate(LocalDate.of(2020, 3, 31))),
        Regularly
      ),
      Amount(1600.00)),
    (
      Monthly,
      PaymentWithPeriod(
        Amount(0.0),
        Amount(5000.00),
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
          PaymentDate(LocalDate.of(2020, 3, 31))),
        Regularly
      ),
      Amount(2500.00)),
    (
      Monthly,
      PaymentWithPeriod(
        Amount(0.0),
        Amount(5000.00),
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 4, 15))),
          PaymentDate(LocalDate.of(2020, 4, 30))),
        Regularly
      ),
      Amount(2621.15)),
    (
      Weekly,
      PaymentWithPeriod(
        Amount(0.0),
        Amount(500.00),
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 7))),
          PaymentDate(LocalDate.of(2020, 3, 21))),
        Regularly
      ),
      Amount(400.00)),
    (
      Weekly,
      PaymentWithPeriod(
        Amount(0.0),
        Amount(1000.00),
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 7))),
          PaymentDate(LocalDate.of(2020, 3, 21))),
        Regularly
      ),
      Amount(576.92)),
    (
      FortNightly,
      PaymentWithPeriod(
        Amount(0.0),
        Amount(2000.00),
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 14))),
          PaymentDate(LocalDate.of(2020, 3, 28))),
        Regularly
      ),
      Amount(1153.84)),
    (
      FortNightly,
      PaymentWithPeriod(
        Amount(0.0),
        Amount(1000.00),
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 14))),
          PaymentDate(LocalDate.of(2020, 3, 28))),
        Regularly
      ),
      Amount(800.00)),
    (
      FourWeekly,
      PaymentWithPeriod(
        Amount(0.0),
        Amount(5000.00),
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 28))),
          PaymentDate(LocalDate.of(2020, 4, 15))),
        Regularly
      ),
      Amount(2307.68)),
    (
      FourWeekly,
      PaymentWithPeriod(
        Amount(0.0),
        Amount(2000.00),
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 28))),
          PaymentDate(LocalDate.of(2020, 4, 15))),
        Regularly
      ),
      Amount(1600.00))
  )

  private lazy val partialPeriodScenarios = Table(
    ("payment", "expectedFurlough"),
    (
      PaymentWithPeriod(
        Amount(677.42),
        Amount(1500.00),
        PartialPeriodWithPaymentDate(
          PartialPeriod(
            Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)),
            Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 3, 31))),
          PaymentDate(LocalDate.of(2020, 3, 31))
        ),
        Regularly
      ),
      Amount(658.06)),
    (
      PaymentWithPeriod(
        Amount(1580.65),
        Amount(3500.00),
        PartialPeriodWithPaymentDate(
          PartialPeriod(
            Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)),
            Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 3, 31))),
          PaymentDate(LocalDate.of(2020, 3, 31))
        ),
        Regularly
      ),
      Amount(1371.05)),
    (
      PaymentWithPeriod(
        Amount(1096.77),
        Amount(2000.00),
        PartialPeriodWithPaymentDate(
          PartialPeriod(
            Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)),
            Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 3, 28))),
          PaymentDate(LocalDate.of(2020, 3, 31))
        ),
        Regularly
      ),
      Amount(722.58))
  )
}
