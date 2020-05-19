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
import models.{FullPeriod, FurloughWithinClaim, PartialPeriod, PaymentDate, Period, Periods}
import org.scalacheck.Gen.choose
import org.scalacheck.Shrink
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class PeriodHelperSpec extends SpecBase with ScalaCheckPropertyChecks with CoreTestDataBuilder {

  "For a given list of pay period end dates, should return a List[LocalDate] in ascending order" in new PeriodHelper {
    val unsortedEndDates: List[LocalDate] = List(LocalDate.of(2020, 3, 20), LocalDate.of(2020, 3, 18), LocalDate.of(2020, 3, 19))
    sortedEndDates(unsortedEndDates) mustBe List(LocalDate.of(2020, 3, 18), LocalDate.of(2020, 3, 19), LocalDate.of(2020, 3, 20))
  }

  "Returns a Pay Period with the same start and end date if only one date is supplied" in new PeriodHelper {
    //This is not a valid scenario, just testing for safety
    val endDates: List[LocalDate] = List(LocalDate.of(2020, 2, 20))
    val furloughPeriod = FurloughWithinClaim(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))

    val expected: List[Periods] = List(FullPeriod(Period(LocalDate.of(2020, 2, 20), LocalDate.of(2020, 2, 20))))

    generatePeriodsWithFurlough(endDates, furloughPeriod) mustBe expected
  }

  "Returns a sorted List[PayPeriod] for a given List[LocalDate] that represents PayPeriod.end LocalDates" in new PeriodHelper {
    val endDates: List[LocalDate] = List(LocalDate.of(2020, 4, 20), LocalDate.of(2020, 3, 20), LocalDate.of(2020, 2, 20))
    val endDatesTwo: List[LocalDate] = List(LocalDate.of(2020, 3, 31), LocalDate.of(2020, 2, 29))

    val furloughPeriod = FurloughWithinClaim(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 4, 10))
    val furloughPeriodTwo = FurloughWithinClaim(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))

    val expected: List[Periods] =
      List(
        PartialPeriod(
          Period(LocalDate.of(2020, 2, 21), LocalDate.of(2020, 3, 20)),
          Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 20))),
        PartialPeriod(
          Period(LocalDate.of(2020, 3, 21), LocalDate.of(2020, 4, 20)),
          Period(LocalDate.of(2020, 3, 21), LocalDate.of(2020, 4, 10)))
      )

    val expectedTwo: List[Periods] = List(
      FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)))
    )

    generatePeriodsWithFurlough(endDates, furloughPeriod) mustBe expected
    generatePeriodsWithFurlough(endDatesTwo, furloughPeriodTwo) mustBe expectedTwo
  }

  "Return periods for a given List[LocalDate] and a furloughPeriod" in new PeriodHelper {
    val endDates: List[LocalDate] = List(LocalDate.of(2020, 4, 30), LocalDate.of(2020, 3, 31), LocalDate.of(2020, 2, 29))
    val furloughPeriod = FurloughWithinClaim(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 4, 30))

    val originalPeriod = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))

    val expected: Seq[Periods] = Seq(
      PartialPeriod(
        originalPeriod,
        Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 3, 31))
      ),
      FullPeriod(
        Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))
      )
    )

    generatePeriodsWithFurlough(endDates, furloughPeriod) mustBe expected
  }

  "determine if a period contains the start of a new tax year" in new PeriodHelper {
    periodContainsNewTaxYear(Period(LocalDate.of(2020, 3, 20), LocalDate.of(2020, 4, 20))) mustBe true
    periodContainsNewTaxYear(Period(LocalDate.of(2020, 3, 6), LocalDate.of(2020, 4, 6))) mustBe true
    periodContainsNewTaxYear(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))) mustBe false
  }

  "determine whether a given date falls in a certain period" in new PeriodHelper {
    val period = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))

    dateExistsInPayPeriod(LocalDate.of(2020, 3, 15), period) mustBe true
    dateExistsInPayPeriod(LocalDate.of(2020, 4, 15), period) mustBe false
    dateExistsInPayPeriod(LocalDate.of(2020, 3, 31), period) mustBe true
    dateExistsInPayPeriod(LocalDate.of(2020, 3, 1), period) mustBe true
  }

  "return pay period with tax year end as the end date if tax year end is earlier than given end date" in new PeriodHelper {
    val periodOne = Period(LocalDate.of(2019, 12, 1), LocalDate.of(2020, 2, 29))
    val periodTwo = Period(LocalDate.of(2019, 12, 1), LocalDate.of(2020, 4, 29))

    val expectedOne = periodOne
    val expectedTwo = Period(LocalDate.of(2019, 12, 1), LocalDate.of(2020, 4, 5))

    endDateOrTaxYearEnd(periodOne) mustBe expectedOne
    endDateOrTaxYearEnd(periodTwo) mustBe expectedTwo
  }

  "counts days in a given period" in new PeriodHelper {
    val periodOne = Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))
    val periodTwo = Period(LocalDate.of(2020, 4, 15), LocalDate.of(2020, 4, 30))
    val periodThree = Period(LocalDate.of(2020, 5, 1), LocalDate.of(2020, 5, 20))
    val periodFour = Period(LocalDate.of(2020, 3, 10), LocalDate.of(2020, 3, 31))

    periodOne.countDays mustBe 30
    periodTwo.countDays mustBe 16
    periodThree.countDays mustBe 20
    periodFour.countDays mustBe 22
  }

  "determine if pay period spans two months" in new PeriodHelper {
    val periodOne = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))
    val periodTwo = Period(LocalDate.of(2020, 3, 20), LocalDate.of(2020, 4, 20))

    periodSpansMonth(periodOne) mustBe false
    periodSpansMonth(periodTwo) mustBe true
  }

  forAll(payDateScenarios) { (frequency, periods, lastPeriodPayDate, expected) =>
    s"For a given list of SORTED periods: $periods, the payment frequency: $frequency and the " +
      s"last period's pay date: $lastPeriodPayDate assign a pay date to each period: $expected" in new PeriodHelper {

      assignPayDates(frequency, periods, lastPeriodPayDate) map (_.paymentDate) mustBe expected
    }
  }

  "Determine the full or partial period within the furlough period" when {
    val payPeriod = period("2020,3,1", "2020,3,31")
    val furloughPeriod = FurloughWithinClaim(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))

    implicit val generatorDrivenConfig: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 30)
    implicit val noShrink: Shrink[Int] = Shrink.shrinkAny

    "furlough and period match" in new PeriodHelper {
      fullOrPartialPeriod(payPeriod, furloughPeriod) mustBe fullPeriod("2020,3,1", "2020,3,31")
    }

    "period is within furlough" in new PeriodHelper {
      val gen = for {
        startDay <- choose[Int](1, 31)
        endDay   <- choose[Int](1, 31).suchThat(_ <= startDay)
      } yield period(s"2020,3,$startDay", s"2020,3,$endDay")

      forAll(gen -> "valid values") { period =>
        fullOrPartialPeriod(period, furloughPeriod) mustBe FullPeriod(period)
      }
    }

    "furlough start and period start match and period is within furlough period" in new PeriodHelper {
      forAll(choose[Int](2, 31) -> "valid values") { endDay =>
        fullOrPartialPeriod(period("2020,3,1", s"2020,3,$endDay"), furloughPeriod) mustBe fullPeriod("2020,3,1", s"2020,3,$endDay")
      }
    }

    "period starts before furlough" in new PeriodHelper {
      val gen = for {
        startMonth <- choose[Int](1, 2)
        startDay <- if (startMonth == 1) {
                     choose[Int](1, 31)
                   } else {
                     choose[Int](1, 29)
                   }
        endDay <- choose[Int](1, 31)
      } yield (startMonth, startDay, endDay)

      forAll(gen -> "valid values") { values =>
        fullOrPartialPeriod(period(s"2020,${values._1},${values._2}", s"2020,3,${values._3}"), furloughPeriod) mustBe partialPeriod(
          s"2020,${values._1},${values._2}" -> s"2020,3,${values._3}",
          "2020,3,1"                        -> s"2020,3,${values._3}")
      }

      // Specific boundary case
      fullOrPartialPeriod(period(s"2020,2,29", "2020,3,20"), furloughPeriod) mustBe partialPeriod(
        s"2020,2,29" -> "2020,3,20",
        "2020,3,1"   -> "2020,3,20")
    }

    "period ends after furlough" in new PeriodHelper {
      val gen = for {
        endMonth <- choose[Int](4, 5)
        endDay <- if (endMonth == 4) {
                   choose[Int](1, 30)
                 } else {
                   choose[Int](1, 31)
                 }
        startDay <- choose[Int](3, 31)
      } yield (endMonth, endDay, startDay)

      forAll(gen -> "valid values") { values =>
        fullOrPartialPeriod(period(s"2020,3,${values._3}", s"2020,${values._1},${values._2}"), furloughPeriod) mustBe partialPeriod(
          s"2020,3,${values._3}" -> s"2020,${values._1},${values._2}",
          s"2020,3,${values._3}" -> "2020,3,31")
      }

      // Specific boundary case
      fullOrPartialPeriod(period("2020,3,31", "2020,4,1"), furloughPeriod) mustBe partialPeriod(
        "2020,3,31" -> "2020,4,1",
        "2020,3,31" -> "2020,3,31")
    }
  }

  private lazy val payDateScenarios = Table(
    ("frequency", "periods", "lastPeriodPayDate", "expected"),
    (
      Monthly,
      Seq(
        FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
        FullPeriod(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30)))),
      LocalDate.of(2020, 4, 20),
      Seq(
        PaymentDate(LocalDate.of(2020, 3, 20)),
        PaymentDate(LocalDate.of(2020, 4, 20))
      )
    ),
    (
      Monthly,
      Seq(
        FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
        FullPeriod(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30)))),
      LocalDate.of(2020, 5, 20),
      Seq(
        PaymentDate(LocalDate.of(2020, 4, 20)),
        PaymentDate(LocalDate.of(2020, 5, 20))
      )
    ),
    (
      FourWeekly,
      Seq(
        FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 28))),
        FullPeriod(Period(LocalDate.of(2020, 3, 29), LocalDate.of(2020, 4, 25)))),
      LocalDate.of(2020, 4, 25),
      Seq(
        PaymentDate(LocalDate.of(2020, 3, 28)),
        PaymentDate(LocalDate.of(2020, 4, 25))
      )
    ),
    (
      FortNightly,
      Seq(
        FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 14))),
        FullPeriod(Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 3, 28)))),
      LocalDate.of(2020, 4, 4),
      Seq(
        PaymentDate(LocalDate.of(2020, 3, 21)),
        PaymentDate(LocalDate.of(2020, 4, 4))
      )
    ),
    (
      Weekly,
      Seq(
        FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 7))),
        FullPeriod(Period(LocalDate.of(2020, 3, 8), LocalDate.of(2020, 3, 14)))),
      LocalDate.of(2020, 3, 28),
      Seq(
        PaymentDate(LocalDate.of(2020, 3, 21)),
        PaymentDate(LocalDate.of(2020, 3, 28))
      )
    ),
    (
      Weekly,
      Seq(
        PartialPeriod(
          Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 7)),
          Period(LocalDate.of(2020, 3, 4), LocalDate.of(2020, 3, 7))),
        FullPeriod(Period(LocalDate.of(2020, 3, 8), LocalDate.of(2020, 3, 14)))
      ),
      LocalDate.of(2020, 3, 28),
      Seq(
        PaymentDate(LocalDate.of(2020, 3, 21)),
        PaymentDate(LocalDate.of(2020, 3, 28))
      )
    )
  )

}
