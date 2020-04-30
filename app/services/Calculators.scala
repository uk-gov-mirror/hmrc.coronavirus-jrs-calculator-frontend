/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.{Amount, PartialPeriod}
import utils.AmountRounding._

import scala.math.BigDecimal.RoundingMode
import Calculators._

trait Calculators extends PeriodHelper {

  def claimableAmount(amount: Amount, cap: BigDecimal): Amount =
    capCalulation(cap, eightyPercent(amount).value)

  def partialPeriodDailyCalculation(payment: Amount, partialPeriod: PartialPeriod): Amount =
    dailyCalculation(payment, periodDaysCount(partialPeriod.original), periodDaysCount(partialPeriod.partial))

  def dailyCalculation(payment: Amount, wholePeriodCount: Int, partialPeriodCount: Int): Amount =
    Amount((payment.value / wholePeriodCount) * partialPeriodCount).halfUp

  protected def eightyPercent(amount: Amount): Amount = Amount(amount.value * 0.8)

  private def capCalulation(cap: BigDecimal, eighty: BigDecimal): Amount =
    Amount(if (eighty > cap) cap else eighty)
}

object Calculators {
  implicit class AmountRounding(amount: Amount) {
    def halfUp: Amount = roundAmountWithMode(amount, RoundingMode.HALF_UP)
  }
}
