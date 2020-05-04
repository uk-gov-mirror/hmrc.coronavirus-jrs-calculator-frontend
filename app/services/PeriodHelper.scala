/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate
import java.time.temporal.ChronoUnit

import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.{FullPeriod, FullPeriodWithPaymentDate, FurloughWithinClaim, PartialPeriod, PartialPeriodWithPaymentDate, PaymentDate, PaymentFrequency, Period, PeriodWithPaymentDate, Periods}
import utils.LocalDateHelpers._

trait PeriodHelper {

  def generatePeriods(endDates: Seq[LocalDate], furloughPeriod: FurloughWithinClaim): Seq[Periods] = {
    PeriodWithPaymentDate
    def generate(acc: Seq[Period], list: Seq[LocalDate]): Seq[Period] = list match {
      case Nil      => acc
      case _ :: Nil => acc
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

  def endDateOrTaxYearEnd(period: Period): Period = {
    val taxYearStart = LocalDate.of(2019, 4, 6)
    val start = if (period.start.isBefore(taxYearStart)) taxYearStart else period.start

    val taxYearEnd = LocalDate.of(2020, 4, 5)
    val end = if (taxYearEnd.isBefore(period.end)) taxYearEnd else period.end

    Period(start, end)
  }

  def isFurloughStart(period: PartialPeriod) =
    period.original.start.isBefore(period.partial.start)

  def isFurloughEnd(period: PartialPeriod) =
    period.original.end.isAfter((period.partial.end))

  def fullOrPartialPeriod(period: Period, furloughPeriod: FurloughWithinClaim): Periods = {
    val start =
      if (furloughPeriod.start.isAfter(period.start) && furloughPeriod.start.isEqualOrBefore(period.end)) furloughPeriod.start
      else period.start
    val end =
      if (furloughPeriod.end.isEqualOrAfter(period.start) && furloughPeriod.end.isBefore(period.end)) furloughPeriod.end else period.end

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

  def assignPayDates(frequency: PaymentFrequency, sortedPeriods: Seq[Periods], lastPayDay: LocalDate): Seq[PeriodWithPaymentDate] =
    sortedPeriods.zip(sortedPeriods.length - 1 to 0 by -1).map {
      case (p, idx) => {
        (p, frequency) match {
          case (fp: FullPeriod, Monthly)        => FullPeriodWithPaymentDate(fp, PaymentDate(lastPayDay.minusMonths(idx)))
          case (pp: PartialPeriod, Monthly)     => PartialPeriodWithPaymentDate(pp, PaymentDate(lastPayDay.minusMonths(idx)))
          case (fp: FullPeriod, FourWeekly)     => FullPeriodWithPaymentDate(fp, PaymentDate(lastPayDay.minusWeeks(idx * 4)))
          case (pp: PartialPeriod, FourWeekly)  => PartialPeriodWithPaymentDate(pp, PaymentDate(lastPayDay.minusWeeks(idx * 4)))
          case (fp: FullPeriod, FortNightly)    => FullPeriodWithPaymentDate(fp, PaymentDate(lastPayDay.minusWeeks(idx * 2)))
          case (pp: PartialPeriod, FortNightly) => PartialPeriodWithPaymentDate(pp, PaymentDate(lastPayDay.minusWeeks(idx * 2)))
          case (fp: FullPeriod, Weekly)         => FullPeriodWithPaymentDate(fp, PaymentDate(lastPayDay.minusWeeks(idx * 1)))
          case (pp: PartialPeriod, Weekly)      => PartialPeriodWithPaymentDate(pp, PaymentDate(lastPayDay.minusWeeks(idx * 1)))
        }
      }
    }

}
