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
import models.PaymentFrequency.{FourWeekly, Monthly, Weekly}
import models.{AdditionalPayment, Amount, FullPeriodBreakdown, PartialPeriod, PartialPeriodBreakdown, PartialPeriodWithPaymentDate, PaymentDate, Period, TopUpPayment}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class NicCalculatorSpec extends SpecBase with ScalaCheckPropertyChecks with CoreTestDataBuilder {

  "Calculate Nic including additional and top up payments" in new NicCalculator {
    val breakDowns = Seq(
      FullPeriodBreakdown(2500.0.toAmount, fullPeriodWithPaymentDate("2020,3,1", "2020, 3, 31", "2020, 3, 31")),
      PartialPeriodBreakdown(
        1250.0.toAmount,
        1250.0.toAmount,
        partialPeriodWithPaymentDate("2020,4,1", "2020, 4, 30", "2020,4,1", "2020, 4, 15", "2020, 4, 30"))
    )
    val additionals = Seq(
      AdditionalPayment(LocalDate.of(2020, 3, 31), 200.0.toAmount),
      AdditionalPayment(LocalDate.of(2020, 4, 30), 200.0.toAmount)
    )
    val topUps = Seq(
      TopUpPayment(LocalDate.of(2020, 3, 31), 300.0.toAmount),
      TopUpPayment(LocalDate.of(2020, 4, 30), 300.0.toAmount)
    )

    calculateNicGrant(Monthly, breakDowns, additionals, topUps).total mustBe 407.25
  }

  "Calculate Nic including additional and top up payments where only some partial periods get additional and/or top ups" in new NicCalculator {
    val breakDowns = Seq(
      PartialPeriodBreakdown(
        200.0.toAmount,
        200.0.toAmount,
        partialPeriodWithPaymentDate("2020,4,1", "2020, 4, 23", "2020,4,1", "2020, 4, 7", "2020, 4, 7")),
      PartialPeriodBreakdown(
        200.0.toAmount,
        200.0.toAmount,
        partialPeriodWithPaymentDate("2020,4,1", "2020, 4, 23", "2020,4,8", "2020, 4, 15", "2020, 4, 15")),
      PartialPeriodBreakdown(
        200.0.toAmount,
        200.0.toAmount,
        partialPeriodWithPaymentDate("2020,4,1", "2020, 4, 23", "2020,4,16", "2020, 4, 23", "2020, 4, 23"))
    )
    val additionals = Seq(
      AdditionalPayment(LocalDate.of(2020, 4, 7), 30.0.toAmount)
    )
    val topUps = Seq(
      TopUpPayment(LocalDate.of(2020, 4, 7), 20.0.toAmount),
      TopUpPayment(LocalDate.of(2020, 4, 23), 25.0.toAmount),
    )

    calculateNicGrant(Weekly, breakDowns, additionals, topUps).total mustBe 31.40
  }

  forAll(partialPeriodScenarios) { (frequency, grossPay, furloughPayment, period, paymentDate, expectedGrant) =>
    s"Calculate grant for a partial period with Payment Frequency: $frequency," +
      s"a PaymentDate: $paymentDate and a Gross Pay: ${grossPay.value}" in new NicCalculator {
      val expected = PartialPeriodBreakdown(grossPay, expectedGrant, PartialPeriodWithPaymentDate(period, paymentDate))

      calculatePartialPeriodNic(frequency, grossPay, furloughPayment, period, paymentDate, None, None) mustBe expected
    }
  }

  "For a partial period and variable pay calculate nic grant" in new NicCalculator {
    val period = PartialPeriod(
      Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 28)),
      Period(LocalDate.of(2020, 3, 20), LocalDate.of(2020, 3, 28)))
    val paymentDate: PaymentDate = PaymentDate(LocalDate.of(2020, 3, 28))

    val expected = PartialPeriodBreakdown(Amount(1124.23), Amount(39.30), PartialPeriodWithPaymentDate(period, paymentDate))

    calculatePartialPeriodNic(FourWeekly, Amount(1124.23), Amount(426.02), period, paymentDate, None, None) mustBe expected
  }

  "calculates Nic with additional payment and 0.0 top up for a partial period" in new NicCalculator {
    val additionalPayment = Some(Amount(300))
    val topUp = None
    val nonFurlough = Amount(2200.0)
    val furlough = Amount(720.0)
    val pp = partialPeriod("2020,3,1" -> "2020,3,31", "2020,3,23" -> "2020, 3, 31")

    calculatePartialPeriodNic(Monthly, nonFurlough, furlough, pp, PaymentDate(LocalDate.of(2020, 3, 31)), additionalPayment, topUp).grant mustBe Amount(
      100.20)
  }

  "calculates Nic with top up and 0.0 additional payment for a partial period" in new NicCalculator {
    val additionalPayment = None
    val topUp = Some(Amount(200.0))
    val nonFurlough = Amount(2200.0)
    val furlough = Amount(720.0)
    val pp = partialPeriod("2020,3,1" -> "2020,3,31", "2020,3,23" -> "2020, 3, 31")

    calculatePartialPeriodNic(Monthly, nonFurlough, furlough, pp, PaymentDate(LocalDate.of(2020, 3, 31)), additionalPayment, topUp).grant mustBe Amount(
      75.28)
  }

  "calculates Nic with additional payment plus top up for a partial period" in new NicCalculator {
    val additionalPayment = Some(Amount(300))
    val topUp = Some(Amount(200))
    val nonFurlough = Amount(2200.0)
    val furlough = Amount(720.0)
    val pp = partialPeriod("2020,3,1" -> "2020,3,31", "2020,3,23" -> "2020, 3, 31")

    calculatePartialPeriodNic(Monthly, nonFurlough, furlough, pp, PaymentDate(LocalDate.of(2020, 3, 31)), additionalPayment, topUp).grant mustBe Amount(
      84.69)
  }

  "calculates Nic plus top up for a full period" in new NicCalculator {
    val additionalPayment = None
    val topUp = Some(Amount(300))
    val furlough = Amount(2200.0)
    val fp = fullPeriod("2020,3,1", "2020, 3, 31")

    calculateFullPeriodNic(Monthly, furlough, fp, PaymentDate(LocalDate.of(2020, 3, 31)), additionalPayment, topUp).grant mustBe Amount(
      216.29)
  }

  private lazy val partialPeriodScenarios = Table(
    ("frequency", "grossPay", "furloughPayment", "period", "paymentDate", "expectedGrant"),
    (
      Monthly,
      Amount(1200.0),
      Amount(960.00),
      partialPeriod("2020,4,1" -> "2020,4, 30", "2020,4, 16" -> "2020,4,30"),
      PaymentDate(LocalDate.of(2020, 4, 30)),
      Amount(98.53)
    ),
    (
      Monthly,
      Amount(1016.13),
      Amount(1774.30),
      partialPeriod("2020, 3, 1" -> "2020, 3, 31", "2020, 3, 10" -> "2020, 3, 31"),
      PaymentDate(LocalDate.of(2020, 3, 31)),
      Amount(202.83)
    ),
    (
      Monthly,
      Amount(180.0),
      Amount(496.0),
      partialPeriod("2020, 3, 1" -> "2020, 3, 31", "2020, 3, 10" -> "2020, 3, 31"),
      PaymentDate(LocalDate.of(2020, 3, 31)),
      Amount(0.00)
    )
  )

}
