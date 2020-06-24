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
import models.{CylbDuration, FullPeriodWithPaymentDate, PartialPeriodWithPaymentDate, PaymentFrequency, Period, PeriodWithPaymentDate}

trait PreviousYearPeriod {

  def previousYearPayDate(paymentFrequency: PaymentFrequency, withPaymentDate: PeriodWithPaymentDate): Seq[LocalDate] = {
    val cylbDuration = CylbDuration(paymentFrequency, withPaymentDate.period)

    (cylbDuration.previousPeriodDays, cylbDuration.equivalentPeriodDays) match {
      case (0, _) => Seq(lastYear(paymentFrequency, withPaymentDate.paymentDate.value))
      case (_, 0) =>
        Seq(lastYear(paymentFrequency, withPaymentDate.paymentDate.value).minusDays(paymentFrequencyDays(paymentFrequency)))
      case _ => calculateDatesForPreviousYear(paymentFrequency, withPaymentDate.paymentDate.value)
    }
  }

  def cylbCutoff(frequency: PaymentFrequency, periods: Seq[PeriodWithPaymentDate]): LocalDate = {
    val periodWithPaymentDate = periods.head

    val cylbDuration = CylbDuration(frequency, periodWithPaymentDate.period)

    (cylbDuration.previousPeriodDays, cylbDuration.equivalentPeriodDays) match {
      case (0, _) => lastYear(frequency, periodWithPaymentDate.period.period.start).plusDays(1)
      case _      => calculateDatesForPreviousYear(frequency, periodWithPaymentDate.period.period.start).head.plusDays(2)
    }
  }

  private def calculateDatesForPreviousYear(paymentFrequency: PaymentFrequency, payDateThisYear: LocalDate): Seq[LocalDate] = {
    val payDateTwo = lastYear(paymentFrequency, payDateThisYear)
    val payDateOne = payDateTwo.minusDays(paymentFrequencyDays(paymentFrequency))

    Seq(payDateOne, payDateTwo)
  }

  private def lastYear(paymentFrequency: PaymentFrequency, payDateThisYear: LocalDate): LocalDate = paymentFrequency match {
    case Monthly => payDateThisYear.minusYears(1)
    case _ =>
      val date = payDateThisYear.minusDays(364)
      if (date.isBefore(LocalDate.of(2019, 3, 1))) date.plusDays(1) else date
  }
}
