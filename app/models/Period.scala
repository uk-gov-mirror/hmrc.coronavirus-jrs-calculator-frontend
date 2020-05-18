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

package models

import java.time.LocalDate
import java.time.temporal.ChronoUnit

final case class Period(start: LocalDate, end: LocalDate)

object Period {
  implicit class DaysCounter(period: Period) {
    def countDays: Int =
      (ChronoUnit.DAYS.between(period.start, period.end) + 1).toInt
  }
}

sealed trait Periods {
  val period: Period
}
final case class FullPeriod(period: Period) extends Periods
final case class PartialPeriod(original: Period, partial: Period) extends Periods {
  override val period = original
  def isFurloughStart: Boolean = original.start.isBefore(partial.start)
  def isFurloughEnd: Boolean = original.end.isAfter(partial.end)
}

sealed trait PeriodWithPaymentDate {
  val period: Periods
  val paymentDate: PaymentDate
}
case class FullPeriodWithPaymentDate(period: FullPeriod, paymentDate: PaymentDate) extends PeriodWithPaymentDate
case class PartialPeriodWithPaymentDate(period: PartialPeriod, paymentDate: PaymentDate) extends PeriodWithPaymentDate
