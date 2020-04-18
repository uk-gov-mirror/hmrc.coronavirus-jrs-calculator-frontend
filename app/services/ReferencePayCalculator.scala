/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.{Amount, FullPeriod, PartialPeriod, PaymentWithPeriod, Period, Periods}
import utils.AmountRounding._

import scala.math.BigDecimal.RoundingMode._

trait ReferencePayCalculator extends PeriodHelper {

  def calculateVariablePay(priorFurloughPeriod: Period, afterFurloughPayPeriod: Seq[Periods], amount: Amount): Seq[PaymentWithPeriod] =
    afterFurloughPayPeriod.map(period => calculateReferencePay(priorFurloughPeriod, period, amount))

  private def calculateReferencePay(priorFurloughPeriod: Period, afterFurloughPayPeriod: Periods, amount: Amount): PaymentWithPeriod = {

    val x = afterFurloughPayPeriod match {
      case FullPeriod(p)       => p
      case PartialPeriod(_, p) => p
    }
    val daily = periodDaysCount(x) * averageDailyCalculator(priorFurloughPeriod, amount)

    PaymentWithPeriod(Amount(daily), afterFurloughPayPeriod)
  }

  protected def averageDailyCalculator(period: Period, amount: Amount): BigDecimal =
    roundWithMode(amount.value / periodDaysCount(period), HALF_UP)
}
