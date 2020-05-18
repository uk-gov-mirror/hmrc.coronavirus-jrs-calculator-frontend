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

import java.time.Month

import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.{FullPeriodCap, FurloughCap, PartialPeriodCap, PaymentFrequency, Period, PeriodSpansMonthCap}
import utils.AmountRounding._

import scala.math.BigDecimal.RoundingMode._

trait FurloughCapCalculator extends PeriodHelper {

  def furloughCap(paymentFrequency: PaymentFrequency, payPeriod: Period): FurloughCap = {
    val furloughCap = capForFrequency(paymentFrequency)

    paymentFrequency match {
      case Monthly if (periodSpansMonth(payPeriod)) =>
        calculateFurloughCapNonSimplified(payPeriod)
      case _ => FullPeriodCap(furloughCap)
    }
  }

  def partialFurloughCap(period: Period): FurloughCap =
    if (periodSpansMonth(period)) {
      calculateFurloughCapNonSimplified(period)
    } else {
      val max = dailyMax(period.start.getMonth)
      val periodDays = period.countDays
      val cap = roundWithMode(periodDays * max, HALF_UP)

      PartialPeriodCap(cap, periodDays, period.start.getMonthValue, max)
    }

  protected def dailyMax(month: Month): BigDecimal =
    roundWithMode(2500.00 / month.maxLength, UP)

  protected def calculateFurloughCapNonSimplified(payPeriod: Period): PeriodSpansMonthCap = {
    val startMonthPeriod = Period(payPeriod.start, payPeriod.start.withDayOfMonth(payPeriod.start.getMonth.maxLength()))
    val startMonthDays = startMonthPeriod.countDays
    val endMonthPeriod = Period(payPeriod.end.withDayOfMonth(1), payPeriod.end)
    val endMonthDays = endMonthPeriod.countDays
    val startMonthDailyMax: BigDecimal = dailyMax(payPeriod.start.getMonth)
    val endMonthDailyMax: BigDecimal = dailyMax(payPeriod.end.getMonth)

    val cap = roundWithMode((startMonthDays * startMonthDailyMax) + (endMonthDays * endMonthDailyMax), HALF_UP)

    PeriodSpansMonthCap(
      cap,
      startMonthDays,
      payPeriod.start.getMonthValue,
      startMonthDailyMax,
      endMonthDays,
      payPeriod.end.getMonthValue,
      endMonthDailyMax)
  }

  private def capForFrequency(frequency: PaymentFrequency): BigDecimal = frequency match {
    case Monthly     => BigDecimal(2500.00)
    case Weekly      => BigDecimal(576.92)
    case FortNightly => BigDecimal(1153.84)
    case FourWeekly  => BigDecimal(2307.68)
  }
}
