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
    def down: Amount = Amount(amount.value.setScale(0, RoundingMode.DOWN))
  }
}
