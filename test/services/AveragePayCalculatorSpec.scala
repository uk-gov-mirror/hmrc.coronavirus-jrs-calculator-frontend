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
import models.{Amount, AveragePaymentWithPhaseTwoPeriod, Hours, NonFurloughPay, Period, PeriodWithPaymentDate, PhaseTwoPeriod}

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
      averagePaymentWithFullPeriod(817.47, afterFurloughPeriod, grossPay.value, priorFurloughPeriod),
      averagePaymentWithPartialPeriod(1000.0, 395.55, afterFurloughPartial, grossPay.value, priorFurloughPeriod)
    )

    calculateAveragePay(nonFurloughPay, priorFurloughPeriod, payPeriods, grossPay) mustBe expected
  }

  "calculate daily average gross earning for a given pay period" in new AveragePayCalculator {
    val employeeStartDate = LocalDate.of(2019, 12, 1)
    val furloughStartDate = LocalDate.of(2020, 3, 1)
    val periodBeforeFurlough = Period(employeeStartDate, furloughStartDate.minusDays(1))

    averageDailyCalculator(periodBeforeFurlough, Amount(2400.0)) mustBe Amount(26.37)
  }

  "Phase Two: assign user entered salary if full period and not part time" in new AveragePayCalculator {
    val annualPay = Amount(20000.0)
    val priorFurloughPeriod = period("2019,8,1", "2020,3,19")
    val periods = Seq(
      PhaseTwoPeriod(fullPeriodWithPaymentDate("2020,7,1", "2020,7,7", "2020,7,7"), None, None)
    )
    val expected = Seq(
      AveragePaymentWithPhaseTwoPeriod(Amount(603.47), Amount(20000.0), priorFurloughPeriod, periods.head)
    )

    phaseTwoAveragePay(annualPay, priorFurloughPeriod, periods) mustBe expected
  }

  "Phase Two: assign user entered salary if full period and not part time but does have hours specified" in new AveragePayCalculator {
    val annualPay = Amount(20000.0)
    val priorFurloughPeriod = period("2019,8,1", "2020,3,19")
    val periods = Seq(
      PhaseTwoPeriod(fullPeriodWithPaymentDate("2020,7,1", "2020,7,7", "2020,7,7"), Some(Hours(40.0)), Some(Hours(40.0)))
    )
    val expected = Seq(
      AveragePaymentWithPhaseTwoPeriod(Amount(603.47), Amount(20000.0), priorFurloughPeriod, periods.head)
    )

    phaseTwoAveragePay(annualPay, priorFurloughPeriod, periods) mustBe expected
  }

  "Phase Two: assign user entered salary if partial period and not part time" in new AveragePayCalculator {
    val annualPay = Amount(20000.0)
    val priorFurloughPeriod = period("2019,8,1", "2020,3,19")
    val periods = Seq(
      PhaseTwoPeriod(partialPeriodWithPaymentDate("2020,7,1", "2020,7,7", "2020,7,1", "2020,7,5", "2020,7,7"), None, None)
    )
    val expected = Seq(
      AveragePaymentWithPhaseTwoPeriod(Amount(431.05), Amount(20000.0), priorFurloughPeriod, periods.head)
    )

    phaseTwoAveragePay(annualPay, priorFurloughPeriod, periods) mustBe expected
  }

  "Phase Two: assign user entered salary if full period and part time" in new AveragePayCalculator {
    val annualPay = Amount(20000.0)
    val priorFurloughPeriod = period("2019,8,1", "2020,3,19")
    val periods = Seq(
      PhaseTwoPeriod(fullPeriodWithPaymentDate("2020,7,1", "2020,7,7", "2020,7,7"), Some(Hours(24.0)), Some(Hours(40.0)))
    )
    val expected = Seq(
      AveragePaymentWithPhaseTwoPeriod(Amount(241.39), Amount(20000.0), priorFurloughPeriod, periods.head)
    )

    phaseTwoAveragePay(annualPay, priorFurloughPeriod, periods) mustBe expected
  }

  "Phase Two: assign user entered salary if partial period and part time" in new AveragePayCalculator {
    val annualPay = Amount(20000.0)
    val priorFurloughPeriod = period("2019,8,1", "2020,3,19")
    val periods = Seq(
      PhaseTwoPeriod(
        partialPeriodWithPaymentDate("2020,7,1", "2020,7,7", "2020,7,1", "2020,7,5", "2020,7,7"),
        Some(Hours(10.0)),
        Some(Hours(40.0)))
    )
    val expected = Seq(
      AveragePaymentWithPhaseTwoPeriod(Amount(323.29), Amount(20000.0), priorFurloughPeriod, periods.head)
    )

    phaseTwoAveragePay(annualPay, priorFurloughPeriod, periods) mustBe expected
  }
}
