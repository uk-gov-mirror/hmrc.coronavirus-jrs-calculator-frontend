/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.{Amount, Period, RegularPayment, Salary}
import utils.AmountRounding._

import scala.math.BigDecimal.RoundingMode._

trait ReferencePayCalculator extends PayPeriodGenerator {

  def calculateVariablePay(priorFurloughPeriod: Period, afterFurloughPayPeriod: Seq[Period], amount: Amount): Seq[RegularPayment] =
    afterFurloughPayPeriod.map(period => calculateReferencePay(priorFurloughPeriod, period, amount))

  private def calculateReferencePay(priorFurloughPeriod: Period, afterFurloughPayPeriod: Period, amount: Amount): RegularPayment = {
    val daily = periodDaysCount(afterFurloughPayPeriod) * averageDailyCalculator(priorFurloughPeriod, amount).value

    RegularPayment(Salary(daily), afterFurloughPayPeriod)
  }

  protected def averageDailyCalculator(payPeriod: Period, amount: Amount): Amount =
    roundAmountWithMode(Amount(amount.value / periodDaysCount(payPeriod)), HALF_UP)
}
