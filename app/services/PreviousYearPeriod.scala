/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import models.PaymentFrequency.{FortNightly, FourWeekly, Weekly}
import models.{FrequencyWithPreviousYearDaysCount, Period}

trait PreviousYearPeriod {

  def previousYearPayDate(paymentFrequency: FrequencyWithPreviousYearDaysCount, thisYear: LocalDate): Period = {
    val lastYear = thisYear.minusDays(364)
    val period = previousYear(paymentFrequency, lastYear)

    if (isBeforeMarch(period)) period.copy(start = period.start.plusDays(1))
    else period
  }

  def previousYearMonthly(thisYear: LocalDate): LocalDate = thisYear.minusYears(1)

  private def previousYear(paymentFrequency: FrequencyWithPreviousYearDaysCount, lastYear: LocalDate): Period =
    paymentFrequency match {
      case Weekly      => Period(lastYear.minusDays(7), lastYear)
      case FortNightly => Period(lastYear.minusDays(14), lastYear)
      case FourWeekly  => Period(lastYear.minusDays(28), lastYear)
    }

  private def isBeforeMarch(period: Period) =
    period.start.isBefore(LocalDate.of(2019, 3, 1))
}
