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
import models.{Amount, NonFurloughPay, Period, PeriodWithPaymentDate}

class AveragePayCalculatorSpec extends SpecBase with CoreTestDataBuilder {

  "calculates average pay for an employee" in new AveragePayCalculator {
    val employeeStartDate = LocalDate.of(2019, 12, 1)
    val furloughStartDate = LocalDate.of(2020, 3, 1)
    val nonFurloughPay = NonFurloughPay(None, Some(Amount(1000.00)))
    val grossPay = Amount(2400.00)
    val priorFurloughPeriod = Period(employeeStartDate, furloughStartDate.minusDays(1))
    val afterFurloughPeriod = fullPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 31", "2020, 3, 31")

    val afterFurloughPartial =
      partialPeriodWithPaymentDate("2020, 5, 1", "2020, 5, 31", "2020, 5, 1", "2020, 5, 15", "2020, 5, 31")

    val payPeriods: Seq[PeriodWithPaymentDate] = Seq(afterFurloughPeriod, afterFurloughPartial)

    val expected = Seq(
      paymentWithFullPeriod(817.47, afterFurloughPeriod),
      paymentWithPartialPeriod(1000.0, 395.55, afterFurloughPartial)
    )

    calculateAveragePay(nonFurloughPay, priorFurloughPeriod, payPeriods, grossPay) mustBe expected
  }

  "calculate daily average gross earning for a given pay period" in new AveragePayCalculator {
    val employeeStartDate = LocalDate.of(2019, 12, 1)
    val furloughStartDate = LocalDate.of(2020, 3, 1)
    val periodBeforeFurlough = Period(employeeStartDate, furloughStartDate.minusDays(1))

    averageDailyCalculator(periodBeforeFurlough, Amount(2400.0)) mustBe Amount(26.37)
  }
}
