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

import models.PaymentFrequency.Monthly
import models.{PaymentFrequency, Period}
import play.api.Logger
import utils.AmountRounding._

import scala.math.BigDecimal.RoundingMode._

trait FurloughCapCalculator extends PeriodHelper {

  def furloughCap(paymentFrequency: PaymentFrequency, payPeriod: Period): BigDecimal = {
    val furloughCap = FurloughCapMapping.mappings
      .get(paymentFrequency)
      .fold {
        Logger.warn(s"Unable to find a Furlough Cap for $paymentFrequency")
        BigDecimal(0).setScale(2)
      } { cap =>
        cap.value
      }

    paymentFrequency match {
      case Monthly if (periodSpansMonth(payPeriod)) =>
        calculateFurloughCapNonSimplified(payPeriod)
      case _ => furloughCap
    }
  }

  def partialFurloughCap(period: Period): BigDecimal =
    if (periodSpansMonth(period)) {
      calculateFurloughCapNonSimplified(period)
    } else {
      val max = dailyMax(period.start.getMonth)
      val periodDays = periodDaysCount(period)
      roundWithMode(periodDays * max, HALF_UP)
    }

  protected def dailyMax(month: Month): BigDecimal =
    roundWithMode(2500.00 / month.maxLength, UP)

  protected def calculateFurloughCapNonSimplified(payPeriod: Period): BigDecimal = {
    val startMonthPeriod = Period(payPeriod.start, payPeriod.start.withDayOfMonth(payPeriod.start.getMonth.maxLength()))
    val startMonthDays: Long = periodDaysCount(startMonthPeriod)
    val endMonthPeriod = Period(payPeriod.end.withDayOfMonth(1), payPeriod.end)
    val endMonthDays: Long = periodDaysCount(endMonthPeriod)
    val startMonthDailyMax: BigDecimal = dailyMax(payPeriod.start.getMonth)
    val endMonthDailyMax: BigDecimal = dailyMax(payPeriod.end.getMonth)

    roundWithMode((startMonthDays * startMonthDailyMax) + (endMonthDays * endMonthDailyMax), HALF_UP)
  }
}
