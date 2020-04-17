/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate
import java.time.temporal.ChronoUnit

import models.Period

trait PayPeriodGenerator {

  def generatePayPeriods(endDates: Seq[LocalDate]): Seq[Period] = {
    def generate(acc: Seq[Period], list: Seq[LocalDate]): Seq[Period] = list match {
      case Nil      => acc
      case h :: Nil => acc
      case h :: t   => generate(acc ++ Seq(Period(h.plusDays(1), t.head)), t)
    }

    if (endDates.length == 1) {
      endDates.map(date => Period(date, date))
    } else {
      generate(Seq(), sortedEndDates(endDates))
    }
  }

  def periodDaysCount(payPeriod: Period): Int =
    (ChronoUnit.DAYS.between(payPeriod.start, payPeriod.end) + 1).toInt

  def endDateOrTaxYearEnd(payPeriod: Period): Period = {
    val taxYearEnd = payPeriod.end.withMonth(4).withDayOfMonth(5)
    val newEnd = if (taxYearEnd.isBefore(payPeriod.end)) taxYearEnd else payPeriod.end

    payPeriod.copy(end = newEnd)
  }

  def periodContainsNewTaxYear(period: Period): Boolean =
    dateExistsInPayPeriod(LocalDate.of(period.start.getYear, 4, 6), period)

  def dateExistsInPayPeriod(date: LocalDate, period: Period): Boolean =
    (date.isAfter(period.start) || date.isEqual(period.start)) &&
      (date.isBefore(period.end) || date.isEqual(period.end))

  protected def sortedEndDates(in: Seq[LocalDate]): Seq[LocalDate] = in.sortWith((x, y) => x.isBefore(y))

  protected def periodSpansMonth(period: Period): Boolean = period.start.getMonth != period.end.getMonth

}
