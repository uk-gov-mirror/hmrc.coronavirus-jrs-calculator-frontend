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
import models.Amount._
import models.NicCategory.{Nonpayable, Payable}
import models.PaymentFrequency.{FourWeekly, Monthly}
import models.{AdditionalPayment, Amount, FullPeriodCap, PartialPeriodCap, PartialPeriodNicBreakdown, TopUpPayment}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class NicCalculatorSpec extends SpecBase with ScalaCheckPropertyChecks with CoreTestDataBuilder {

  "Calculate Nic including additional and top up payments" in new NicCalculator {
    val breakDowns = Seq(
      fullPeriodFurloughBreakdown(
        2500.00,
        regularPaymentWithFullPeriod(3500.00, 3500.00, fullPeriodWithPaymentDate("2020,3,1", "2020, 3, 31", "2020, 3, 31")),
        FullPeriodCap(2500.00)),
      partialPeriodFurloughBreakdown(
        1250.0,
        regularPaymentWithPartialPeriod(
          1750.0,
          3500.0,
          1750.0,
          partialPeriodWithPaymentDate("2020,4,1", "2020, 4, 30", "2020,4,1", "2020, 4, 15", "2020, 4, 30")),
        PartialPeriodCap(1250.0, 15, 4, 83.34)
      )
    )

    val additionals = Seq(
      AdditionalPayment(LocalDate.of(2020, 3, 31), 200.0.toAmount),
      AdditionalPayment(LocalDate.of(2020, 4, 30), 200.0.toAmount)
    )

    val topUps = Seq(
      TopUpPayment(LocalDate.of(2020, 3, 31), 300.0.toAmount),
      TopUpPayment(LocalDate.of(2020, 4, 30), 300.0.toAmount)
    )

    calculateNicGrant(Payable, Monthly, breakDowns, additionals, topUps).total mustBe 435.07
  }

  forAll(partialPeriodScenarios) { (frequency, furloughGrant, payment, expectedGrant) =>
    s"Calculate grant for a partial period with Payment Frequency: $frequency," +
      s"a PaymentDate: ${payment.periodWithPaymentDate.paymentDate} and a Furlough Grant: ${furloughGrant.value}" in new NicCalculator {
      val expected = PartialPeriodNicBreakdown(expectedGrant, Amount(0.0), Amount(0.0), payment)

      calculatePartialPeriodNic(Payable, frequency, furloughGrant, payment, None, None) mustBe expected
    }
  }

  "For a partial period and variable average daily pay of £50.00 calculate nic grant" in new NicCalculator {
    val payment =
      regularPaymentWithPartialPeriod(
        950.0,
        1400.0,
        450.0,
        partialPeriodWithPaymentDate("2020-03-01", "2020-03-28", "2020-03-20", "2020-03-28", "2020-03-28"))

    val expected = partialPeriodNicBreakdown(28.66, 0.00, 0.00, payment)

    calculatePartialPeriodNic(Payable, FourWeekly, Amount(360.0), payment, None, None) mustBe expected
  }

  "calculates Nic with additional payment and 0.0 top up for a partial period" in new NicCalculator {
    val payment =
      regularPaymentWithPartialPeriod(
        2200.0,
        3100.0,
        900.0,
        partialPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-23", "2020-03-31", "2020-03-31"))

    calculatePartialPeriodNic(Payable, Monthly, Amount(720.0), payment, Some(Amount(300.00)), None).grant mustBe Amount(99.36)
  }

  "calculates Nic with top up and 0.0 additional payment for a partial period" in new NicCalculator {
    val payment =
      regularPaymentWithPartialPeriod(
        2200.0,
        3100.0,
        900.0,
        partialPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-23", "2020-03-31", "2020-03-31"))

    calculatePartialPeriodNic(Payable, Monthly, Amount(720.0), payment, None, Some(Amount(200.0))).grant mustBe Amount(75.28)
  }

  "calculates Nic with additional payment plus top up for a partial period" in new NicCalculator {
    val payment =
      regularPaymentWithPartialPeriod(
        2200.0,
        3100.0,
        900.0,
        partialPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-23", "2020-03-31", "2020-03-31"))

    calculatePartialPeriodNic(Payable, Monthly, Amount(720.0), payment, Some(Amount(300.0)), Some(Amount(200.0))).grant mustBe Amount(84.69)
  }

  "calculates Nic Grant and enforces cap based on furlough grant" in new NicCalculator {
    val payment =
      regularPaymentWithFullPeriod(2750.0, 2750.0, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-31"))

    calculateFullPeriodNic(Payable, Monthly, Amount(2200.0), payment, Some(Amount(2000.0)), Some(Amount(300.0))).grant mustBe Amount(303.60)
  }

  "calculates Nic plus top up for a full period" in new NicCalculator {
    val payment =
      regularPaymentWithFullPeriod(2750.0, 2750.0, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-31"))

    calculateFullPeriodNic(Payable, Monthly, Amount(2200.0), payment, None, Some(Amount(300.0))).grant mustBe Amount(216.29)
  }

  "Returns 0.00 for Nic grant if not eligible for Nic grant full period" in new NicCalculator {
    val payment =
      regularPaymentWithFullPeriod(2750.0, 2750.0, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-31"))

    calculateFullPeriodNic(Nonpayable, Monthly, Amount(2200.0), payment, None, Some(Amount(300.0))).grant mustBe Amount(0.00)
  }

  "Returns 0.00 for Nic grant if not eligible for Nic grant partial period" in new NicCalculator {
    val payment =
      regularPaymentWithPartialPeriod(
        2200.0,
        3100.0,
        900.0,
        partialPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-23", "2020-03-31", "2020-03-31"))

    calculatePartialPeriodNic(Nonpayable, Monthly, Amount(720.0), payment, None, None).grant mustBe Amount(0.00)
  }

  private lazy val partialPeriodScenarios = Table(
    ("frequency", "furloughGrant", "payment", "expectedGrant"),
    (
      Monthly,
      Amount(960.00),
      regularPaymentWithPartialPeriod(
        1200.00,
        2400.00,
        1200.00,
        partialPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-16", "2020-04-30", "2020-04-30")),
      Amount(98.53)
    ),
    (
      Monthly,
      Amount(1774.30),
      regularPaymentWithPartialPeriod(
        1016.13,
        3516.13,
        2500.0,
        partialPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-10", "2020-03-31", "2020-03-31")),
      Amount(202.83)
    ),
    (
      Monthly,
      Amount(496.0),
      regularPaymentWithPartialPeriod(
        180.0,
        800.0,
        620.0,
        partialPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-10", "2020-03-31", "2020-03-31")),
      Amount(0.00)
    )
  )

}
