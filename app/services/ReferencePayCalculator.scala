/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.{Amount, PaymentWithPeriod, Period, Salary}
import utils.AmountRounding._

import scala.math.BigDecimal.RoundingMode._

trait ReferencePayCalculator extends PeriodHelper {

  def calculateVariablePay(priorFurloughPeriod: Period, afterFurloughPayPeriod: Seq[Period], amount: Amount): Seq[PaymentWithPeriod] =
    afterFurloughPayPeriod.map(period => calculateReferencePay(priorFurloughPeriod, period, amount))

  private def calculateReferencePay(priorFurloughPeriod: Period, afterFurloughPayPeriod: Period, amount: Amount): PaymentWithPeriod = {
    val daily = periodDaysCount(afterFurloughPayPeriod) * averageDailyCalculator(priorFurloughPeriod, amount)

    PaymentWithPeriod(Amount(daily), afterFurloughPayPeriod)
  }

  protected def averageDailyCalculator(payPeriod: Period, amount: Amount): BigDecimal =
    roundWithMode(amount.value / periodDaysCount(payPeriod), HALF_UP)
}
