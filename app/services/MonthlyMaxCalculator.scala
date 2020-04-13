/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.temporal.ChronoUnit

import models.{PayPeriod, RegularPayment}

import scala.math.BigDecimal.RoundingMode.RoundingMode
import scala.math.BigDecimal.RoundingMode._

class MonthlyMaxCalculator {

  def calculate(payPeriod: PayPeriod): Double =
    if (payPeriod.start.getMonth == payPeriod.end.getMonth) 2500.0
    else calculateMonthMax(payPeriod)

  private def calculateMonthMax(payPeriod: PayPeriod): Double = {
    val startMonthDays: Long = ChronoUnit.DAYS.between(payPeriod.start, payPeriod.start.withDayOfMonth(payPeriod.start.getMonth.maxLength))
    val endMonthDays: Long = ChronoUnit.DAYS.between(payPeriod.start.withDayOfMonth(payPeriod.start.getMonth.maxLength), payPeriod.end)
    val startMonthDailyMax: Double = helper(2500.0 / payPeriod.start.getMonth.maxLength, UP)
    val endMonthDailyMax: Double = helper(2500.0 / payPeriod.end.getMonth.maxLength, UP)

    helper((startMonthDays * startMonthDailyMax) + (endMonthDays * endMonthDailyMax), HALF_UP)
  }

  val helper: (Double, RoundingMode) => Double = (value, mode) => BigDecimal(value).setScale(2, mode).toDouble

}
