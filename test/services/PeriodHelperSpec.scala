/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.SpecBase
import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.{FullPeriod, PartialPeriod, PaymentDate, Period, PeriodWithPaymentDate, Periods}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class PeriodHelperSpec extends SpecBase with ScalaCheckPropertyChecks {

  "For a given list of pay period end dates, should return a List[LocalDate] in ascending order" in new PeriodHelper {
    val unsortedEndDates: List[LocalDate] = List(LocalDate.of(2020, 3, 20), LocalDate.of(2020, 3, 18), LocalDate.of(2020, 3, 19))
    sortedEndDates(unsortedEndDates) mustBe List(LocalDate.of(2020, 3, 18), LocalDate.of(2020, 3, 19), LocalDate.of(2020, 3, 20))
  }

  "Returns a Pay Period with the same start and end date if only one date is supplied" in new PeriodHelper {
    //This is not a valid scenario, just testing for safety
    val endDates: List[LocalDate] = List(LocalDate.of(2020, 2, 20))
    val furloughPeriod: Period = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))

    val expected: List[Periods] = List(FullPeriod(Period(LocalDate.of(2020, 2, 20), LocalDate.of(2020, 2, 20))))

    generatePeriods(endDates, furloughPeriod) mustBe expected
  }

  "Returns a sorted List[PayPeriod] for a given List[LocalDate] that represents PayPeriod.end LocalDates" in new PeriodHelper {
    val endDates: List[LocalDate] = List(LocalDate.of(2020, 4, 20), LocalDate.of(2020, 3, 20), LocalDate.of(2020, 2, 20))
    val endDatesTwo: List[LocalDate] = List(LocalDate.of(2020, 3, 31), LocalDate.of(2020, 2, 29))

    val furloughPeriod: Period = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 4, 10))
    val furloughPeriodTwo: Period = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))

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

    generatePeriods(endDates, furloughPeriod) mustBe expected
    generatePeriods(endDatesTwo, furloughPeriodTwo) mustBe expectedTwo
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

  forAll(payDateScenarios) { (frequency, periods, lastPeriodPayDate, expected) =>
    s"For a given list of SORTED periods: $periods, the payment frequency: $frequency and the " +
      s"last period's pay date: $lastPeriodPayDate assign a pay date to each period: $expected" in new PeriodHelper {

      assignPayDates(frequency, periods, lastPeriodPayDate) map (_.paymentDate) mustBe expected
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
