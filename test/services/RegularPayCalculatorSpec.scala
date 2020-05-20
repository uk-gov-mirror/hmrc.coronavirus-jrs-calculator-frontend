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

import base.{CoreTestDataBuilder, SpecBase}
import models.Amount

class RegularPayCalculatorSpec extends SpecBase with CoreTestDataBuilder {

  "assign user entered salary to each pay period" in new RegularPayCalculator {
    val wage = Amount(1000.0)
    val periods = defaultReferencePayData.periods

    val expected = Seq(
      regularPaymentWithFullPeriod(1000.0, 1000.0, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-31"))
    )

    calculateRegularPay(wage, periods) mustBe expected
  }

  "apportion the user entered salary for partial periods" in new RegularPayCalculator {
    val wage = Amount(2000.0)
    val partial = partialPeriodWithPaymentDate("2020, 4, 1", "2020, 4, 30", "2020, 4, 1", "2020, 4, 15", "2020, 4, 30")
    val periods = defaultReferencePayData.periods :+ partial

    val expected = Seq(
      regularPaymentWithFullPeriod(2000.0, 2000.0, fullPeriodWithPaymentDate("2020,3,1", "2020,3,31", "2020, 3, 31")),
      regularPaymentWithPartialPeriod(1000.0, 2000.0, 1000.0, partial)
    )

    calculateRegularPay(wage, periods) mustBe expected
  }

}
