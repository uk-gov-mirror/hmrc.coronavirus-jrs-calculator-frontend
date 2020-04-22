/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate
import java.time.temporal.ChronoUnit

import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.{FullPeriod, PartialPeriod, PaymentDate, PaymentFrequency, Period, PeriodWithPaymentDate, Periods}

trait PeriodHelper {

  def generatePeriods(endDates: Seq[LocalDate], furloughPeriod: Period): Seq[Periods] = {
    PeriodWithPaymentDate
    def generate(acc: Seq[Period], list: Seq[LocalDate]): Seq[Period] = list match {
      case Nil      => acc
      case h :: Nil => acc
      case h :: t   => generate(acc ++ Seq(Period(h.plusDays(1), t.head)), t)
    }

    val generated =
      if (endDates.length == 1) {
        endDates.map(date => Period(date, date))
      } else {
        generate(Seq(), sortedEndDates(endDates))
      }

    generated.map(p => fullOrPartialPeriod(p, furloughPeriod))
  }

  def periodDaysCount(period: Period): Int =
    (ChronoUnit.DAYS.between(period.start, period.end) + 1).toInt

  def endDateOrTaxYearEnd(payPeriod: Period): Period = {
    val taxYearEnd = payPeriod.end.withMonth(4).withDayOfMonth(5)
    val newEnd = if (taxYearEnd.isBefore(payPeriod.end)) taxYearEnd else payPeriod.end

    payPeriod.copy(end = newEnd)
  }

  def isFurloughStart(period: PartialPeriod) =
    period.original.start.isBefore(period.partial.start)

  def isFurloughEnd(period: PartialPeriod) =
    period.original.end.isAfter((period.partial.end))

  def fullOrPartialPeriod(period: Period, furloughPeriod: Period): Periods = {
    val start =
      if (furloughPeriod.start.isAfter(period.start) && furloughPeriod.start.isBefore(period.end)) furloughPeriod.start else period.start
    val end =
      if (furloughPeriod.end.isAfter(period.start) && furloughPeriod.end.isBefore(period.end)) furloughPeriod.end else period.end

    val partial = Period(start, end)

    if (periodDaysCount(period) != periodDaysCount(partial)) PartialPeriod(period, partial) else FullPeriod(period)
  }

  def periodContainsNewTaxYear(period: Period): Boolean =
    dateExistsInPayPeriod(LocalDate.of(period.start.getYear, 4, 6), period)

  def dateExistsInPayPeriod(date: LocalDate, period: Period): Boolean =
    (date.isAfter(period.start) || date.isEqual(period.start)) &&
      (date.isBefore(period.end) || date.isEqual(period.end))

  protected def sortedEndDates(in: Seq[LocalDate]): Seq[LocalDate] = in.sortWith((x, y) => x.isBefore(y))

  protected def periodSpansMonth(period: Period): Boolean = period.start.getMonth != period.end.getMonth

  protected def assignPayDates(
    frequency: PaymentFrequency,
    sortedPeriods: Seq[Periods],
    lastPayDay: LocalDate): Seq[PeriodWithPaymentDate] =
    sortedPeriods.zip(sortedPeriods.length - 1 to 0 by -1).map {
      case (p, idx) => {
        frequency match {
          case Monthly     => PeriodWithPaymentDate(p, PaymentDate(lastPayDay.minusMonths(idx)))
          case FourWeekly  => PeriodWithPaymentDate(p, PaymentDate(lastPayDay.minusWeeks(idx * 4)))
          case FortNightly => PeriodWithPaymentDate(p, PaymentDate(lastPayDay.minusWeeks(idx * 2)))
          case Weekly      => PeriodWithPaymentDate(p, PaymentDate(lastPayDay.minusWeeks(idx * 1)))
        }
      }
    }

}
