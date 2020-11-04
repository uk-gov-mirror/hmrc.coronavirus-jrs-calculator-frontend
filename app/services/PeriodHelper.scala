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

import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.Period._
import models._
import utils.LocalDateHelpers._
import models.PaymentFrequency.paymentFrequencyDays

import scala.annotation.tailrec

trait PeriodHelper {

  def generatePeriodsWithFurlough(endDates: Seq[LocalDate], furloughPeriod: FurloughWithinClaim): Seq[Periods] =
    generatePeriods(endDates).map(p => fullOrPartialPeriod(p, furloughPeriod))

  def generatePeriods(endDates: Seq[LocalDate]): Seq[Period] = {
    @tailrec
    def generate(acc: Seq[Period], list: Seq[LocalDate]): Seq[Period] = list match {
      case Nil      => acc
      case _ :: Nil => acc
      case h :: t   => generate(acc ++ Seq(Period(h.plusDays(1), t.head)), t)
    }

    if (endDates.length == 1) {
      endDates.map(date => Period(date, date))
    } else {
      generate(Seq.empty, sortedEndDates(endDates))
    }
  }

  def generateEndDates(frequency: PaymentFrequency, firstEndDate: LocalDate, furloughPeriod: FurloughWithinClaim): Seq[LocalDate] = {
    def generate(acc: Seq[LocalDate], latest: LocalDate): Seq[LocalDate] =
      if (!latest.isBefore(furloughPeriod.end)) {
        acc ++ Seq(latest)
      } else {
        generate(acc ++ Seq(latest), latest.plusDays(paymentFrequencyDays(frequency)))
      }

    generate(Seq(firstEndDate), firstEndDate.plusDays(paymentFrequencyDays(frequency)))
  }

  def endDateOrTaxYearEnd(period: Period, claimStart: LocalDate): Period = {
    val taxYearStart = LocalDate.of(2019, 4, 6)
    val start =
      if (claimStart.isEqualOrAfter(LocalDate.of(2020, 11, 1)) && period.start.isEqual(taxYearStart)) {
        LocalDate.of(2020, 4, 6)
      } else if (period.start.isBefore(taxYearStart)) {
        taxYearStart
      } else {
        period.start
      }

    val taxYearEnd = LocalDate.of(2020, 4, 5)
    val end = if (claimStart.isBefore(LocalDate.of(2020, 11, 1)) && taxYearEnd.isBefore(period.end)) taxYearEnd else period.end

    Period(start, end)
  }

  def isFurloughStart(period: PartialPeriod): Boolean =
    period.original.start.isBefore(period.partial.start)

  def isFurloughEnd(period: PartialPeriod): Boolean =
    period.original.end.isAfter(period.partial.end)

  def fullOrPartialPeriod(period: Period, furloughPeriod: FurloughWithinClaim): Periods = {
    val start =
      if (furloughPeriod.start.isAfter(period.start) &&
          furloughPeriod.start.isEqualOrBefore(period.end)) {
        furloughPeriod.start
      } else {
        period.start
      }

    val end =
      if (furloughPeriod.end.isEqualOrAfter(period.start) &&
          furloughPeriod.end.isBefore(period.end)) {
        furloughPeriod.end
      } else {
        period.end
      }

    val partial = Period(start, end)

    if (period.countDays != partial.countDays) PartialPeriod(period, partial) else FullPeriod(period)
  }

  def periodContainsNewTaxYear(period: Period): Boolean =
    dateExistsInPayPeriod(LocalDate.of(period.start.getYear, 4, 5), period) &&
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

  def assignPartTimeHours(periods: Seq[PeriodWithPaymentDate], actuals: Seq[PartTimeHours], usuals: Seq[UsualHours]): Seq[PhaseTwoPeriod] =
    for {
      period <- periods
      actual = actuals.find(_.date == period.period.period.end).map(_.hours)
      usual = usuals.find(_.date == period.period.period.end).map(_.hours)
    } yield PhaseTwoPeriod(period, actual, usual)

}
