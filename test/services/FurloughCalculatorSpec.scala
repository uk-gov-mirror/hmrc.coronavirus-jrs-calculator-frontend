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
import models.{Amount, FullPeriod, FullPeriodCap, FullPeriodFurloughBreakdown, FullPeriodWithPaymentDate, FurloughCalculationResult, Hours, PartialPeriod, PartialPeriodCap, PartialPeriodFurloughBreakdown, PartialPeriodWithPaymentDate, PaymentDate, PaymentWithFullPeriod, PaymentWithPeriod, Period, PeriodSpansMonthCap, PhaseTwoFurloughCalculationResult, PhaseTwoPeriod, RegularPaymentWithPhaseTwoPeriod}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class FurloughCalculatorSpec extends SpecBase with ScalaCheckPropertyChecks with CoreTestDataBuilder {

  forAll(fullPeriodScenarios) { (frequency, payment, cap, expectedFurlough) =>
    s"Full Period: For payment frequency $frequency and payment ${payment.referencePay.value} return $expectedFurlough" in new FurloughCalculator {
      val expected = FullPeriodFurloughBreakdown(expectedFurlough, payment, cap)
      calculateFullPeriod(frequency, payment) mustBe expected
    }
  }

  forAll(partialPeriodScenarios) { (payment, cap, expectedFurlough) =>
    s"Partial Period: For gross payment: ${payment.referencePay.value} " +
      s"should return $expectedFurlough" in new FurloughCalculator {
      val expected = PartialPeriodFurloughBreakdown(expectedFurlough, payment, cap)
      calculatePartialPeriod(payment) mustBe expected
    }
  }

  "return a CalculationResult with a total and a list of furlough payments for a given list regular payment" in new FurloughCalculator {
    val paymentOne: PaymentWithFullPeriod =
      regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-20"))
    val paymentTwo: PaymentWithFullPeriod =
      regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-20"))
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
      regularPaymentWithFullPeriod(
        2000.00,
        2000.00,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
          PaymentDate(LocalDate.of(2020, 3, 31)))
      ),
      FullPeriodCap(2500.00),
      Amount(1600.00)),
    (
      Monthly,
      regularPaymentWithFullPeriod(
        5000.00,
        5000.00,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
          PaymentDate(LocalDate.of(2020, 3, 31)))
      ),
      FullPeriodCap(2500.00),
      Amount(2500.00)),
    (
      Monthly,
      regularPaymentWithFullPeriod(
        5000.00,
        5000.00,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 4, 15))),
          PaymentDate(LocalDate.of(2020, 4, 30)))
      ),
      PeriodSpansMonthCap(2621.15, 17, 3, 80.65, 15, 4, 83.34),
      Amount(2621.15)),
    (
      Weekly,
      regularPaymentWithFullPeriod(
        500.00,
        500.00,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 7))),
          PaymentDate(LocalDate.of(2020, 3, 21)))
      ),
      FullPeriodCap(576.92),
      Amount(400.00)),
    (
      Weekly,
      regularPaymentWithFullPeriod(
        1000.00,
        1000.00,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 7))),
          PaymentDate(LocalDate.of(2020, 3, 21)))
      ),
      FullPeriodCap(576.92),
      Amount(576.92)),
    (
      FortNightly,
      regularPaymentWithFullPeriod(
        1000.00,
        2000.00,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 14))),
          PaymentDate(LocalDate.of(2020, 3, 28)))
      ),
      FullPeriodCap(1153.84),
      Amount(1153.84)),
    (
      FortNightly,
      regularPaymentWithFullPeriod(
        1000.00,
        1000.00,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 14))),
          PaymentDate(LocalDate.of(2020, 3, 28)))
      ),
      FullPeriodCap(1153.84),
      Amount(800.00)),
    (
      FourWeekly,
      regularPaymentWithFullPeriod(
        5000.00,
        5000.00,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 28))),
          PaymentDate(LocalDate.of(2020, 4, 15)))
      ),
      FullPeriodCap(2307.68),
      Amount(2307.68)),
    (
      FourWeekly,
      regularPaymentWithFullPeriod(
        2000.00,
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
      regularPaymentWithPartialPeriod(
        677.42,
        1500.00,
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
      regularPaymentWithPartialPeriod(
        1580.65,
        3420.00,
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
      regularPaymentWithPartialPeriod(
        1096.77,
        2000.00,
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

  "Phase Two: apply apportioned furlough cap in the case of part time" in new FurloughCalculator {
    val payments = Seq(
      RegularPaymentWithPhaseTwoPeriod(
        Amount(800.0),
        Amount(500.0),
        PhaseTwoPeriod(fullPeriodWithPaymentDate("2020,7,1", "2020,7,7", "2020,7,7"), Some(Hours(15.0)), Some(Hours(40.0)))),
      RegularPaymentWithPhaseTwoPeriod(
        Amount(800),
        Amount(600.0),
        PhaseTwoPeriod(fullPeriodWithPaymentDate("2020,7,8", "2020,7,14", "2020,7,14"), Some(Hours(10.0)), Some(Hours(40.0)))),
    )

    val expected = 793.27

    phaseTwoFurlough(Weekly, payments).total mustBe expected
  }
}
