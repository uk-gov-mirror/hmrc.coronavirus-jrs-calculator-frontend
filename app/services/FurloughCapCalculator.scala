/*
 * Copyright 2020 HM Revenue & Customs
 *
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

  def partialFurloughCap(payPeriod: Period): BigDecimal =
    if (periodSpansMonth(payPeriod)) {
      calculateFurloughCapNonSimplified(payPeriod)
    } else {
      val max = dailyMax(payPeriod.start.getMonth)
      val periodDays = periodDaysCount(payPeriod)
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
