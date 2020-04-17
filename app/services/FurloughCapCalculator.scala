/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.Month
import java.time.temporal.ChronoUnit

import models.PaymentFrequency.Monthly
import models.{PaymentFrequency, Period}
import play.api.Logger
import utils.AmountRounding._

import scala.math.BigDecimal.RoundingMode._

trait FurloughCapCalculator {

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
      case Monthly if (payPeriod.start.getMonth != payPeriod.end.getMonth) =>
        calculateFurloughCapNonSimplified(payPeriod)
      case _ => furloughCap
    }
  }

  def partialFurloughCap(payPeriod: Period): BigDecimal = calculateFurloughCapNonSimplified(payPeriod)

  protected def dailyMax(month: Month): BigDecimal =
    roundWithMode(2500.00 / month.maxLength, UP)

  private def calculateFurloughCapNonSimplified(payPeriod: Period): BigDecimal = {
    val startMonthDays: Long = ChronoUnit.DAYS.between(payPeriod.start, payPeriod.start.withDayOfMonth(payPeriod.start.getMonth.maxLength))
    val endMonthDays: Long = ChronoUnit.DAYS.between(payPeriod.start.withDayOfMonth(payPeriod.start.getMonth.maxLength), payPeriod.end)
    val startMonthDailyMax: BigDecimal = dailyMax(payPeriod.start.getMonth)
    val endMonthDailyMax: BigDecimal = dailyMax(payPeriod.end.getMonth)

    roundWithMode((startMonthDays * startMonthDailyMax) + (endMonthDays * endMonthDailyMax), HALF_UP)
  }
}
