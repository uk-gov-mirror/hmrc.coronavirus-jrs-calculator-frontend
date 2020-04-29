/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.{CoreDataBuilder, SpecBase}
import models.PaymentFrequency.{FortNightly, Monthly, Weekly}

class PreviousYearCoreDataSpec extends SpecBase with CoreDataBuilder {

  "return previous year date for a given date of this year" in new PreviousYearPeriod {
    fullPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 7", "2020, 3, 7")

    previousYearPayDate(Weekly, fullPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 7", "2020, 3, 7")) mustBe Seq(
      LocalDate.of(2019, 3, 2),
      LocalDate.of(2019, 3, 9))
    previousYearPayDate(Weekly, partialPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 7", "2020, 3, 3", "2020, 3, 7", "2020, 3, 7")) mustBe Seq(
      LocalDate.of(2019, 3, 9))
    previousYearPayDate(Weekly, partialPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 7", "2020, 3, 1", "2020, 3, 2", "2020, 3, 7")) mustBe Seq(
      LocalDate.of(2019, 3, 2))
    previousYearPayDate(Weekly, partialPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 7", "2020, 3, 1", "2020, 3, 6", "2020, 3, 7")) mustBe Seq(
      LocalDate.of(2019, 3, 2),
      LocalDate.of(2019, 3, 9))

    previousYearPayDate(FortNightly, fullPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 14", "2020, 3, 14")) mustBe Seq(
      LocalDate.of(2019, 3, 2),
      LocalDate.of(2019, 3, 16))

    previousYearPayDate(Monthly, fullPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 31", "2020, 3, 31")) mustBe Seq(
      LocalDate.of(2019, 3, 31))
  }
}
