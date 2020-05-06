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

import base.SpecBase
import models.PaymentFrequency.Monthly
import models.{Amount, FullPeriod, FullPeriodBreakdown, FullPeriodWithPaymentDate, PaymentDate, Period}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class PensionCalculatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  forAll(fullPeriodScenarios) { (frequency, furloughPayment, period, paymentDate, expectedGrant) =>
    s"Calculate pension grant for a full period with Payment Frequency: $frequency, " +
      s"a Payment Date: $paymentDate and a Furlough Grant: ${furloughPayment.value}" in new PensionCalculator {
      val expected = FullPeriodBreakdown(expectedGrant, FullPeriodWithPaymentDate(period, paymentDate))

      calculateFullPeriodPension(frequency, furloughPayment, period, paymentDate) mustBe expected
    }
  }

  private lazy val fullPeriodScenarios = Table(
    ("frequency", "furloughPayment", "period", "paymentDate", "expectedGrant"),
    (
      Monthly,
      Amount(1600.00),
      FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
      PaymentDate(LocalDate.of(2020, 3, 31)),
      Amount(32.64)),
    (
      Monthly,
      Amount(600.00),
      FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
      PaymentDate(LocalDate.of(2020, 3, 31)),
      Amount(2.64))
  )

}
