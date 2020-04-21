/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.{CoreDataBuilder, SpecBase}
import models.PaymentFrequency.{FortNightly, FourWeekly, Weekly}
import models.Period

class PreviousYearCoreDataSpec extends SpecBase with CoreDataBuilder {

  "return previous year date for a given date of this year" in new PreviousYearPeriod {
    val thisYearPayDate = LocalDate.of(2020, 3, 7)
    val expected: Period = period("2019,3,2", "2019,3,9")

    previousYearPayDate(Weekly, thisYearPayDate) mustBe expected
    previousYearPayDate(FortNightly, LocalDate.of(2020, 3, 10)) mustBe period("2019,2,27", "2019,3,12")
    previousYearPayDate(FourWeekly, LocalDate.of(2020, 3, 28)) mustBe period("2019,3,2", "2019,3,30")
  }

  "return previous year date for a given date of this year where the earliest date is before 1st March 2019" in new PreviousYearPeriod {
    val thisYearPayDate = LocalDate.of(2020, 3, 10)
    val expected: Period = period("2019,2,27", "2019,3,12")

    previousYearPayDate(FortNightly, thisYearPayDate) mustBe expected
  }

  "return previous year date for a given date of this year and monthly frequency" in new PreviousYearPeriod {
    val thisYearPayDate = LocalDate.of(2020, 3, 7)
    val expected = LocalDate.of(2019, 3, 7)

    previousYearMonthly(thisYearPayDate) mustBe expected
  }
}
