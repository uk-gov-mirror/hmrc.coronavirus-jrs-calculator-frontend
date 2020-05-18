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

import java.time.LocalDate

import models.NonFurloughPay.determineNonFurloughPay
import models.{Amount, AveragePayment, CylbBreakdown, CylbDuration, CylbPayment, CylbPaymentWithFullPeriod, CylbPaymentWithPartialPeriod, FullPeriodWithPaymentDate, LastYearPayment, NonFurloughPay, OnePeriodCylb, PartialPeriodWithPaymentDate, PaymentFrequency, PeriodWithPaymentDate, TwoPeriodCylb}
import services.Calculators.AmountRounding

trait CylbCalculator extends PreviousYearPeriod {

  def calculateCylb(
    averagePayment: AveragePayment,
    nonFurloughPay: NonFurloughPay,
    frequency: PaymentFrequency,
    cylbs: Seq[LastYearPayment],
    period: PeriodWithPaymentDate): CylbPayment = {
    val datesRequired = previousYearPayDate(frequency, period)
    val nfp = determineNonFurloughPay(period.period, nonFurloughPay)

    cylbsAmount(averagePayment, frequency, period, datesRequired, nfp, cylbs)
  }

  private def cylbsAmount(
    averagePayment: AveragePayment,
    frequency: PaymentFrequency,
    period: PeriodWithPaymentDate,
    datesRequired: Seq[LocalDate],
    nfp: Amount,
    cylbs: Seq[LastYearPayment]): CylbPayment = {
    val cylbOps: CylbDuration = CylbDuration(frequency, period.period)
    val cylbBreakdown: CylbBreakdown = previousYearFurlough(datesRequired, cylbs, cylbOps)

    val referencePay = Amount(averagePayment.referencePay.value.max(cylbBreakdown.referencePay.value))

    period match {
      case fp: FullPeriodWithPaymentDate    => CylbPaymentWithFullPeriod(referencePay, fp, averagePayment, cylbBreakdown)
      case pp: PartialPeriodWithPaymentDate => CylbPaymentWithPartialPeriod(nfp, referencePay, pp, averagePayment, cylbBreakdown)
    }
  }

  private def previousYearFurlough(datesRequired: Seq[LocalDate], cylbs: Seq[LastYearPayment], ops: CylbDuration): CylbBreakdown = {
    val amounts: Seq[Amount] = datesRequired.flatMap(date => cylbs.find(_.date == date)).map(_.amount)

    amounts match {
      case amount :: Nil                          => previousOrCurrent(amount, ops)
      case previousAmount :: currentAmount :: Nil => previousAndCurrent(ops, previousAmount, currentAmount)
    }
  }

  private def previousOrCurrent(amount: Amount, ops: CylbDuration): OnePeriodCylb =
    if (ops.equivalentPeriodDays == 0) {
      val referencePay = Amount((amount.value / ops.fullPeriodLength) * ops.previousPeriodDays).halfUp
      OnePeriodCylb(referencePay, amount, ops.fullPeriodLength, ops.previousPeriodDays)
    } else {
      val referencePay = Amount((amount.value / ops.fullPeriodLength) * ops.equivalentPeriodDays).halfUp
      OnePeriodCylb(referencePay, amount, ops.fullPeriodLength, ops.equivalentPeriodDays)
    }

  private def previousAndCurrent(ops: CylbDuration, previousAmount: Amount, currentAmount: Amount): TwoPeriodCylb = {
    val periodOneAmount = Amount((previousAmount.value / ops.fullPeriodLength) * ops.previousPeriodDays).halfUp
    val periodTwoAmount = Amount((currentAmount.value / ops.fullPeriodLength) * ops.equivalentPeriodDays).halfUp

    val referencePay = Amount(periodOneAmount.value + periodTwoAmount.value)

    TwoPeriodCylb(
      referencePay,
      previousAmount,
      ops.fullPeriodLength,
      ops.previousPeriodDays,
      periodOneAmount,
      currentAmount,
      ops.fullPeriodLength,
      ops.equivalentPeriodDays,
      periodTwoAmount
    )
  }

}
