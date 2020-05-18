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

import java.time.LocalDate

import base.{CoreTestDataBuilder, SpecBase}
import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.{Amount, FullPeriod, FullPeriodCap, FullPeriodFurloughBreakdown, FullPeriodWithPaymentDate, FurloughCalculationResult, PartialPeriod, PartialPeriodCap, PartialPeriodFurloughBreakdown, PartialPeriodWithPaymentDate, PaymentDate, PaymentWithFullPeriod, PaymentWithPeriod, Period, PeriodSpansMonthCap}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class FurloughCalculatorSpec extends SpecBase with ScalaCheckPropertyChecks with CoreTestDataBuilder {

  forAll(fullPeriodScenarios) { (frequency, payment, cap, expectedFurlough) =>
    s"Full Period: For payment frequency $frequency and payment ${payment.furloughPayment.value} return $expectedFurlough" in new FurloughCalculator {
      val expected = FullPeriodFurloughBreakdown(expectedFurlough, payment, cap)
      calculateFullPeriod(frequency, payment) mustBe expected
    }
  }

  forAll(partialPeriodScenarios) { (payment, cap, expectedFurlough) =>
    s"Partial Period: For gross payment: ${payment.furloughPayment.value} " +
      s"should return $expectedFurlough" in new FurloughCalculator {
      val expected = PartialPeriodFurloughBreakdown(expectedFurlough, payment, cap)
      calculatePartialPeriod(payment) mustBe expected
    }
  }

  "return a CalculationResult with a total and a list of furlough payments for a given list regular payment" in new FurloughCalculator {
    val paymentOne: PaymentWithFullPeriod =
      paymentWithFullPeriod(2000.00, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-20"))
    val paymentTwo: PaymentWithFullPeriod =
      paymentWithFullPeriod(2000.00, fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-20"))
    val payments: List[PaymentWithPeriod] = List(paymentOne, paymentTwo)

    val expected =
      FurloughCalculationResult(
        3200.00,
        Seq(
          fullPeriodFurloughBreakdown(1600.00, paymentOne, FullPeriodCap(2500.00)),
          fullPeriodFurloughBreakdown(1600.00, paymentTwo, FullPeriodCap(2500.00))
        )
      )

    calculateFurloughGrant(Monthly, payments) mustBe expected
  }

  private lazy val fullPeriodScenarios = Table(
    ("paymentFrequency", "payment", "cap", "expectedFurlough"),
    (
      Monthly,
      paymentWithFullPeriod(
        2000,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
          PaymentDate(LocalDate.of(2020, 3, 31)))
      ),
      FullPeriodCap(2500.00),
      Amount(1600.00)),
    (
      Monthly,
      paymentWithFullPeriod(
        5000.00,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
          PaymentDate(LocalDate.of(2020, 3, 31)))
      ),
      FullPeriodCap(2500.00),
      Amount(2500.00)),
    (
      Monthly,
      paymentWithFullPeriod(
        5000.00,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 4, 15))),
          PaymentDate(LocalDate.of(2020, 4, 30)))
      ),
      PeriodSpansMonthCap(2621.15, 17, 3, 80.65, 15, 4, 83.34),
      Amount(2621.15)),
    (
      Weekly,
      paymentWithFullPeriod(
        500.00,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 7))),
          PaymentDate(LocalDate.of(2020, 3, 21)))
      ),
      FullPeriodCap(576.92),
      Amount(400.00)),
    (
      Weekly,
      paymentWithFullPeriod(
        1000.00,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 7))),
          PaymentDate(LocalDate.of(2020, 3, 21)))
      ),
      FullPeriodCap(576.92),
      Amount(576.92)),
    (
      FortNightly,
      paymentWithFullPeriod(
        2000.00,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 14))),
          PaymentDate(LocalDate.of(2020, 3, 28)))
      ),
      FullPeriodCap(1153.84),
      Amount(1153.84)),
    (
      FortNightly,
      paymentWithFullPeriod(
        1000.00,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 14))),
          PaymentDate(LocalDate.of(2020, 3, 28)))
      ),
      FullPeriodCap(1153.84),
      Amount(800.00)),
    (
      FourWeekly,
      paymentWithFullPeriod(
        5000.00,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 28))),
          PaymentDate(LocalDate.of(2020, 4, 15)))
      ),
      FullPeriodCap(2307.68),
      Amount(2307.68)),
    (
      FourWeekly,
      paymentWithFullPeriod(
        2000.00,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 28))),
          PaymentDate(LocalDate.of(2020, 4, 15)))
      ),
      FullPeriodCap(2307.68),
      Amount(1600.00))
  )

  private lazy val partialPeriodScenarios = Table(
    ("payment", "cap", "expectedFurlough"),
    (
      paymentWithPartialPeriod(
        677.42,
        822.58,
        PartialPeriodWithPaymentDate(
          PartialPeriod(
            Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)),
            Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 3, 31))),
          PaymentDate(LocalDate.of(2020, 3, 31))
        )
      ),
      PartialPeriodCap(1371.05, 17, 3, 80.65),
      Amount(658.06)),
    (
      paymentWithPartialPeriod(
        1580.65,
        1919.35,
        PartialPeriodWithPaymentDate(
          PartialPeriod(
            Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)),
            Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 3, 31))),
          PaymentDate(LocalDate.of(2020, 3, 31))
        )
      ),
      PartialPeriodCap(1371.05, 17, 3, 80.65),
      Amount(1371.05)),
    (
      paymentWithPartialPeriod(
        1096.77,
        903.23,
        PartialPeriodWithPaymentDate(
          PartialPeriod(
            Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)),
            Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 3, 28))),
          PaymentDate(LocalDate.of(2020, 3, 31))
        )
      ),
      PartialPeriodCap(1129.1, 14, 3, 80.65),
      Amount(722.58))
  )
}
