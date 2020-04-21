/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.{CoreDataBuilder, SpecBase}
import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}

class PreviousYearCoreDataSpec extends SpecBase with CoreDataBuilder {

  "return previous year date for a given date of this year" in new PreviousYearPeriod {
    previousYearPayDate(Weekly, LocalDate.of(2020, 3, 7)) mustBe Seq(LocalDate.of(2019, 3, 2), LocalDate.of(2019, 3, 9))
    previousYearPayDate(FortNightly, LocalDate.of(2020, 3, 10)) mustBe Seq(LocalDate.of(2019, 2, 27), LocalDate.of(2019, 3, 12))
    previousYearPayDate(FourWeekly, LocalDate.of(2020, 3, 28)) mustBe Seq(LocalDate.of(2019, 3, 2), LocalDate.of(2019, 3, 30))
  }

  "return previous year date for a given date of this year where the earliest date is before 1st March 2019" in new PreviousYearPeriod {
    val thisYearPayDate = LocalDate.of(2020, 3, 10)

    previousYearPayDate(FortNightly, thisYearPayDate) mustBe Seq(LocalDate.of(2019, 2, 27), LocalDate.of(2019, 3, 12))
  }

  "return previous year date for a given date of this year and monthly frequency" in new PreviousYearPeriod {
    val thisYearPayDate = LocalDate.of(2020, 3, 7)
    val expected = LocalDate.of(2019, 3, 7)

    previousYearPayDate(Monthly, thisYearPayDate) mustBe Seq(LocalDate.of(2019, 3, 7))
  }
}
