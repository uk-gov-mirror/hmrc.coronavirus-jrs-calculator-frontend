/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.SpecBase
import models.{FullPeriod, PartialPeriod, Period, Periods}

class PeriodHelperSpec extends SpecBase {

  "For a given list of pay period end dates, should return a List[LocalDate] in ascending order" in new PeriodHelper {
    val unsortedEndDates: List[LocalDate] = List(LocalDate.of(2020, 3, 20), LocalDate.of(2020, 3, 18), LocalDate.of(2020, 3, 19))
    sortedEndDates(unsortedEndDates) mustBe List(LocalDate.of(2020, 3, 18), LocalDate.of(2020, 3, 19), LocalDate.of(2020, 3, 20))
  }

  "Returns a Pay Period with the same start and end date if only one date is supplied" in new PeriodHelper {
    //This is not a valid scenario, just testing for safety
    val endDates: List[LocalDate] = List(LocalDate.of(2020, 2, 20))

    val expected: List[Period] = List(Period(LocalDate.of(2020, 2, 20), LocalDate.of(2020, 2, 20)))

    generatePeriodsFromEndDates(endDates) mustBe expected
  }

  "Returns a sorted List[PayPeriod] for a given List[LocalDate] that represents PayPeriod.end LocalDates" in new PeriodHelper {
    val endDates: List[LocalDate] = List(LocalDate.of(2020, 4, 20), LocalDate.of(2020, 3, 20), LocalDate.of(2020, 2, 20))
    val endDatesTwo: List[LocalDate] = List(LocalDate.of(2020, 3, 20), LocalDate.of(2020, 2, 20))

    val expected: List[Period] =
      List(Period(LocalDate.of(2020, 2, 21), LocalDate.of(2020, 3, 20)), Period(LocalDate.of(2020, 3, 21), LocalDate.of(2020, 4, 20)))

    val expectedTwo: List[Period] = List(
      Period(LocalDate.of(2020, 2, 21), LocalDate.of(2020, 3, 20))
    )

    generatePeriodsFromEndDates(endDates) mustBe expected
    generatePeriodsFromEndDates(endDatesTwo) mustBe expectedTwo
  }

  "Return periods for a given List[LocalDate] and a furloughPeriod" in new PeriodHelper {
    val endDates: List[LocalDate] = List(LocalDate.of(2020, 4, 30), LocalDate.of(2020, 3, 31), LocalDate.of(2020, 2, 29))
    val furloughPeriod: Period = Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 4, 30))

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

    generatePeriods(endDates, furloughPeriod) mustBe expected
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

    periodDaysCount(periodOne) mustBe 30
    periodDaysCount(periodTwo) mustBe 16
    periodDaysCount(periodThree) mustBe 20
    periodDaysCount(periodFour) mustBe 22
  }

  "determine if pay period spans two months" in new PeriodHelper {
    val periodOne = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))
    val periodTwo = Period(LocalDate.of(2020, 3, 20), LocalDate.of(2020, 4, 20))

    periodSpansMonth(periodOne) mustBe false
    periodSpansMonth(periodTwo) mustBe true
  }

  "determine if a period is a partial period given a furlough period" in new PeriodHelper {
    val periodOne = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))
    val furloughOne = Period(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 4, 30))
    val furloughTwo = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 4, 30))

    val expectedOne = Left(PartialPeriod(periodOne, Period(furloughOne.start, periodOne.end)))
    val expectedTwo = Right(periodOne)
  }

}
