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
import models.{Amount, LastYearPayment, NonFurloughPay, RegularPayData, VariablePayData, VariablePayWithCylbData}

class ReferencePayCalculatorSpec extends SpecBase with CoreTestDataBuilder {

  "compare cylb and avg gross pay amount taking the greater" in new ReferencePayCalculator {
    val cylb = Seq(
      paymentWithFullPeriod(500.00, fullPeriodWithPaymentDate("2020,3,1", "2020,3,28", "2020, 3, 28")),
      paymentWithFullPeriod(200.00, fullPeriodWithPaymentDate("2020,3,29", "2020,4,26", "2020, 4, 26"))
    )

    val avg = Seq(
      paymentWithFullPeriod(450.0, fullPeriodWithPaymentDate("2020,3,1", "2020,3,28", "2020, 3, 28")),
      paymentWithFullPeriod(450.0, fullPeriodWithPaymentDate("2020,3,29", "2020,4,26", "2020, 4, 26"))
    )

    val expected = Seq(
      paymentWithFullPeriod(500.00, fullPeriodWithPaymentDate("2020,3,1", "2020,3,28", "2020, 3, 28")),
      paymentWithFullPeriod(450.00, fullPeriodWithPaymentDate("2020,3,29", "2020,4,26", "2020, 4, 26"))
    )

    takeGreaterGrossPay(cylb, avg) mustBe expected
  }

  "calculate reference pay for a given RegularPayData" in new ReferencePayCalculator {
    val input = RegularPayData(defaultReferencePayData, Amount(1000.0))

    val expected = Seq(
      paymentWithFullPeriod(1000.0, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-31"))
    )

    calculateReferencePay(input) mustBe expected
  }

  "calculate reference pay for a given VariablePayData" in new ReferencePayCalculator {
    val input = VariablePayData(defaultReferencePayData, Amount(10000.0), NonFurloughPay(None, None), period("2019-12-01", "2020-02-29"))

    val expected = Seq(
      paymentWithFullPeriod(3406.59, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-31"))
    )

    calculateReferencePay(input) mustBe expected
  }

  "calculate reference pay for a given VariablePayWithCylbData" in new ReferencePayCalculator {
    val cylbPaymentsOne = Seq(LastYearPayment(LocalDate.of(2019, 3, 31), Amount(1000.0)))
    val cylbPaymentsTwo = Seq(LastYearPayment(LocalDate.of(2019, 3, 31), Amount(5000.0)))

    val inputAvgGreater = VariablePayWithCylbData(
      defaultReferencePayData,
      Amount(10000.0),
      NonFurloughPay(None, None),
      period("2019-12-01", "2020-02-29"),
      cylbPaymentsOne)
    val inputCylbGreater = VariablePayWithCylbData(
      defaultReferencePayData,
      Amount(10000.0),
      NonFurloughPay(None, None),
      period("2019-12-01", "2020-02-29"),
      cylbPaymentsTwo)

    val expectedAvgGreater = Seq(
      paymentWithFullPeriod(3406.59, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-31"))
    )

    val expectedCylbGreater = Seq(
      paymentWithFullPeriod(5000.0, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-31"))
    )

    calculateReferencePay(inputAvgGreater) mustBe expectedAvgGreater
    calculateReferencePay(inputCylbGreater) mustBe expectedCylbGreater
  }
}
