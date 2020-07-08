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

import models.PaymentFrequency.{Monthly, _}
import models.{CylbDuration, FullPeriodWithPaymentDate, PartialPeriodWithPaymentDate, PaymentFrequency, Period, PeriodWithPaymentDate, Periods}

trait PreviousYearPeriod {

  def previousYearPeriod(frequency: PaymentFrequency, period: Periods): Seq[Period] = {
    val cylbDuration = CylbDuration(frequency, period)

    (cylbDuration.previousPeriodDays, cylbDuration.equivalentPeriodDays) match {
      case (0, _) => Seq(lastYearPeriod(frequency, period.period))
      case (_, 0) =>
        val lastYear = lastYearPeriod(frequency, period.period)
        val start = lastYear.start.minusDays(paymentFrequencyDays(frequency))
        val end = lastYear.end.minusDays(paymentFrequencyDays(frequency))
        Seq(Period(start, end))
      case _ =>
        val lastYear = lastYearPeriod(frequency, period.period)
        val start = lastYear.start.minusDays(paymentFrequencyDays(frequency))
        val end = lastYear.end.minusDays(paymentFrequencyDays(frequency))
        Seq(
          Period(start, end),
          lastYear
        )
    }
  }

  def cylbCutoff(frequency: PaymentFrequency, periods: Seq[PeriodWithPaymentDate]): LocalDate = {
    val periodWithPaymentDate = periods.head

    val cylbDuration = CylbDuration(frequency, periodWithPaymentDate.period)

    (cylbDuration.previousPeriodDays, cylbDuration.equivalentPeriodDays) match {
      case (0, _) =>
        lastYearPeriod(frequency, periodWithPaymentDate.period.period).start.plusDays(1)
      case _ =>
        lastYearPeriod(frequency, periodWithPaymentDate.period.period).start.minusDays(paymentFrequencyDays(frequency)).plusDays(1)
    }
  }

  private def lastYearPeriod(frequency: PaymentFrequency, period: Period): Period = frequency match {
    case Monthly => Period(period.start.minusYears(1), period.end.minusYears(1))
    case _ =>
      val start =
        if (period.start.minusDays(364).isBefore(LocalDate.of(2019, 3, 1))) period.start.minusDays(363) else period.start.minusDays(364)
      val end = if (period.end.minusDays(364).isBefore(LocalDate.of(2019, 3, 1))) period.end.minusDays(363) else period.end.minusDays(364)
      Period(start, end)
  }
}
