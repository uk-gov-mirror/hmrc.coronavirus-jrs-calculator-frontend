/*
 * Copyright 2021 HM Revenue & Customs
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
import models.CylbDuration
import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}

class PreviousYearPeriodSpec extends SpecBase with CoreTestDataBuilder {

  "return 2019 periods for a given 2020 period" in new PreviousYearPeriod {
    previousYearPeriod(Weekly, fullPeriod("2020, 7, 1", "2020, 7, 7")) mustBe Seq(
      period("2019, 6, 26", "2019, 7, 2"),
      period("2019, 7, 3", "2019, 7, 9")
    )

    previousYearPeriod(Weekly, partialPeriod("2020, 7, 1" -> "2020, 7, 7", "2020, 7, 3" -> "2020, 7, 7")) mustBe Seq(
      period("2019, 7, 3", "2019, 7, 9")
    )

    previousYearPeriod(Monthly, fullPeriod("2020, 7, 1", "2020, 7, 31")) mustBe Seq(
      period("2019, 7, 1", "2019, 7, 31")
    )
  }

  def extract(duration: CylbDuration): (Int, Int, Int) =
    (duration.fullPeriodLength, duration.equivalentPeriodDays, duration.previousPeriodDays)

  "Weekly tests" when {

    "claim is before 1st March 2021" in {

      extract(CylbDuration(Weekly, fullPeriod("2020,3,1", "2020,3,7"))) mustBe Tuple3(7, 5, 2)

      extract(CylbDuration(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,3" -> "2020,3,7"))) mustBe
        Tuple3(7, 5, 0)

      extract(CylbDuration(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,4" -> "2020,3,7"))) mustBe
        Tuple3(7, 4, 0)

      extract(CylbDuration(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,2" -> "2020,3,7"))) mustBe
        Tuple3(7, 5, 1)

      extract(CylbDuration(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,1" -> "2020,3,6"))) mustBe
        Tuple3(7, 4, 2)

      extract(CylbDuration(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,1" -> "2020,3,2"))) mustBe
        Tuple3(7, 0, 2)

      extract(CylbDuration(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,1" -> "2020,3,1"))) mustBe
        Tuple3(7, 0, 2)
    }

    "claim is 1st March 2021 onward" in {

      extract(CylbDuration(Weekly, fullPeriod("2021,3,1", "2021,3,7"))) mustBe Tuple3(7, 4, 3)

      extract(CylbDuration(Weekly, partialPeriod("2021,3,1" -> "2021,3,7", "2021,3,3" -> "2021,3,7"))) mustBe
        Tuple3(7, 5, 0)

      extract(CylbDuration(Weekly, partialPeriod("2021,3,1" -> "2021,3,7", "2021,3,4" -> "2021,3,7"))) mustBe
        Tuple3(7, 4, 0)

      extract(CylbDuration(Weekly, partialPeriod("2021,3,1" -> "2021,3,7", "2021,3,2" -> "2021,3,7"))) mustBe
        Tuple3(7, 5, 1)

      extract(CylbDuration(Weekly, partialPeriod("2021,3,1" -> "2021,3,7", "2021,3,1" -> "2021,3,6"))) mustBe
        Tuple3(7, 3, 3)

      extract(CylbDuration(Weekly, partialPeriod("2021,3,1" -> "2021,3,7", "2021,3,1" -> "2021,3,2"))) mustBe
        Tuple3(7, 0, 3)

      extract(CylbDuration(Weekly, partialPeriod("2021,3,1" -> "2021,3,7", "2021,3,1" -> "2021,3,1"))) mustBe
        Tuple3(7, 0, 3)
    }
  }

  "Fortnightly tests" when {

    "claim is before 1st March 2021" in {

      extract(CylbDuration(FortNightly, fullPeriod("2020,3,1", "2020,3,14"))) mustBe
        Tuple3(14, 12, 2)

      extract(CylbDuration(FortNightly, partialPeriod("2020,3,1" -> "2020,3,14", "2020,3,3" -> "2020,3,14"))) mustBe
        Tuple3(14, 12, 0)

      extract(CylbDuration(FortNightly, partialPeriod("2020,3,1" -> "2020,3,14", "2020,3,5" -> "2020,3,14"))) mustBe
        Tuple3(14, 10, 0)

      extract(CylbDuration(FortNightly, partialPeriod("2020,3,1" -> "2020,3,14", "2020,3,2" -> "2020,3,14"))) mustBe
        Tuple3(14, 12, 1)
    }

    "claim is 1st March 2021 onward" in {

      extract(CylbDuration(FortNightly, fullPeriod("2021,3,1", "2021,3,14"))) mustBe
        Tuple3(14, 11, 3)

      extract(CylbDuration(FortNightly, partialPeriod("2021,3,1" -> "2021,3,14", "2021,3,3" -> "2021,3,14"))) mustBe
        Tuple3(14, 12, 0)

      extract(CylbDuration(FortNightly, partialPeriod("2021,3,1" -> "2021,3,14", "2021,3,5" -> "2021,3,14"))) mustBe
        Tuple3(14, 10, 0)

      extract(CylbDuration(FortNightly, partialPeriod("2021,3,1" -> "2021,3,14", "2021,3,2" -> "2021,3,14"))) mustBe
        Tuple3(14, 12, 1)
    }
  }

  "Fourweekly tests" when {

    "claim is before 1st March 2021" in {

      extract(CylbDuration(FourWeekly, fullPeriod("2020,3,1", "2020,3,28"))) mustBe
        Tuple3(28, 26, 2)

      extract(CylbDuration(FourWeekly, partialPeriod("2020,3,1" -> "2020,3,28", "2020,3,3" -> "2020,3,28"))) mustBe
        Tuple3(28, 26, 0)

      extract(CylbDuration(FourWeekly, partialPeriod("2020,3,1" -> "2020,3,28", "2020,3,9" -> "2020,3,28"))) mustBe
        Tuple3(28, 20, 0)

      extract(CylbDuration(FourWeekly, partialPeriod("2020,3,1" -> "2020,3,28", "2020,3, 2" -> "2020,3,28"))) mustBe
        Tuple3(28, 26, 1)

      extract(CylbDuration(FourWeekly, partialPeriod("2020,3,1" -> "2020,3,28", "2020,3, 1" -> "2020,3,5"))) mustBe
        Tuple3(28, 3, 2)

      extract(CylbDuration(FourWeekly, partialPeriod("2020,3,1" -> "2020,3,28", "2020,3, 1" -> "2020,3,1"))) mustBe
        Tuple3(28, 0, 2)
    }

    "claim is 1st March 2021 onwards" in {

      extract(CylbDuration(FourWeekly, fullPeriod("2021,3,1", "2021,3,28"))) mustBe
        Tuple3(28, 25, 3)

      extract(CylbDuration(FourWeekly, partialPeriod("2021,3,1" -> "2021,3,28", "2021,3,3" -> "2021,3,28"))) mustBe
        Tuple3(28, 26, 0)

      extract(CylbDuration(FourWeekly, partialPeriod("2021,3,1" -> "2021,3,28", "2021,3,9" -> "2021,3,28"))) mustBe
        Tuple3(28, 20, 0)

      extract(CylbDuration(FourWeekly, partialPeriod("2021,3,1" -> "2021,3,28", "2021,3, 2" -> "2021,3,28"))) mustBe
        Tuple3(28, 26, 1)

      extract(CylbDuration(FourWeekly, partialPeriod("2021,3,1" -> "2021,3,28", "2021,3, 1" -> "2021,3,5"))) mustBe
        Tuple3(28, 2, 3)

      extract(CylbDuration(FourWeekly, partialPeriod("2021,3,1" -> "2021,3,28", "2021,3, 1" -> "2021,3,1"))) mustBe
        Tuple3(28, 0, 3)
    }
  }

  "Monthly tests" when {

    "claim is before 1st March 2021" in {

      extract(CylbDuration(Monthly, fullPeriod("2020,3,1", "2020,3,31"))) mustBe
        Tuple3(31, 31, 0)
      extract(CylbDuration(Monthly, fullPeriod("2020,4,1", "2020,4,30"))) mustBe
        Tuple3(30, 30, 0)
      extract(CylbDuration(Monthly, partialPeriod("2020,3,1" -> "2020,3,31", "2020,3,3" -> "2020,3,31"))) mustBe
        Tuple3(31, 29, 0)
    }

    "claim is 1st March 2021 onwards" in {

      extract(CylbDuration(Monthly, fullPeriod("2021,3,1", "2021,3,31"))) mustBe
        Tuple3(31, 31, 0)
      extract(CylbDuration(Monthly, fullPeriod("2021,4,1", "2021,4,30"))) mustBe
        Tuple3(30, 30, 0)
      extract(CylbDuration(Monthly, partialPeriod("2021,3,1" -> "2021,3,31", "2021,3,3" -> "2021,3,31"))) mustBe
        Tuple3(31, 29, 0)
    }
  }

  "determine cylbCutoff date" in new PreviousYearPeriod {

    cylbCutoff(Weekly, Seq(partialPeriodWithPaymentDate("2020, 7, 1", "2020, 7, 7", "2020, 7, 3", "2020, 7, 7", "2020, 7, 7"))) mustBe LocalDate
      .of(2019, 7, 4)
    cylbCutoff(Weekly, Seq(partialPeriodWithPaymentDate("2020, 7, 1", "2020, 7, 7", "2020, 7, 3", "2020, 7, 7", "2020, 7, 14"))) mustBe LocalDate
      .of(2019, 7, 4)
    cylbCutoff(Weekly, Seq(fullPeriodWithPaymentDate("2020, 7, 1", "2020, 7, 7", "2020, 7, 7"))) mustBe LocalDate.of(2019, 6, 27)
  }

}
