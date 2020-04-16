/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.{Amount, PayPeriod, RegularPayment, Salary}
import utils.AmountRounding._

import scala.math.BigDecimal.RoundingMode._

trait ReferencePayCalculator extends PayPeriodGenerator {

  def calculateVariablePay(priorFurloughPeriod: PayPeriod, afterFurloughPayPeriod: Seq[PayPeriod], amount: Amount): Seq[RegularPayment] =
    afterFurloughPayPeriod.map(period => calculateReferencePay(priorFurloughPeriod, period, amount))

  private def calculateReferencePay(priorFurloughPeriod: PayPeriod, afterFurloughPayPeriod: PayPeriod, amount: Amount): RegularPayment = {
    val daily = periodDaysCount(afterFurloughPayPeriod) * averageDailyCalculator(priorFurloughPeriod, amount).value

    RegularPayment(Salary(daily), afterFurloughPayPeriod)
  }

  protected def averageDailyCalculator(payPeriod: PayPeriod, amount: Amount): Amount =
    roundAmountWithMode(Amount(amount.value / periodDaysCount(payPeriod)), HALF_UP)
}
