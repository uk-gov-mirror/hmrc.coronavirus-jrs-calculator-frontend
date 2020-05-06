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

package models

import java.time.LocalDate

import base.SpecBase
import models.Calculation.NicCalculationResult
import models.CalculationResult._
import play.api.libs.json.Json

class CalculationResultSpec extends SpecBase {

  "serialize/deserialize from/to json" in {
    val fullPeriodWithPaymentDate =
      FullPeriodWithPaymentDate(
        FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
        PaymentDate(LocalDate.of(2020, 3, 20)))

    val partialPeriodWithPaymentDate =
      PartialPeriodWithPaymentDate(
        PartialPeriod(
          Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)),
          Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
        PaymentDate(LocalDate.of(2020, 3, 20))
      )

    val nicCalculationResult =
      CalculationResult(
        NicCalculationResult,
        0.10,
        Seq(
          FullPeriodBreakdown(Amount(123.00), fullPeriodWithPaymentDate),
          PartialPeriodBreakdown(Amount(456.00), Amount(789.00), partialPeriodWithPaymentDate)
        )
      )

    val expectedJsValue =
      Json.parse(s"""{
                    |  "calculation": "nic",
                    |  "total": 0.1,
                    |  "payPeriodBreakdowns": [
                    |    {
                    |      "grant": "123.0",
                    |      "periodWithPaymentDate": {
                    |        "period": {
                    |          "period": {
                    |            "start": "2020-03-01",
                    |            "end": "2020-03-31"
                    |          }
                    |        },
                    |        "paymentDate": "2020-03-20"
                    |      }
                    |    },
                    |    {
                    |      "nonFurloughPay": "456.0",
                    |      "grant": "789.0",
                    |      "periodWithPaymentDate": {
                    |        "period": {
                    |          "original": {
                    |            "start": "2020-03-01",
                    |            "end": "2020-03-31"
                    |          },
                    |          "partial": {
                    |            "start": "2020-03-01",
                    |            "end": "2020-03-31"
                    |          }
                    |        },
                    |        "paymentDate": "2020-03-20"
                    |      }
                    |    }
                    |  ]
                    |}
                    |""".stripMargin)

    Json.toJson(nicCalculationResult) mustBe expectedJsValue
  }
}
