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
import models.{Amount, AveragePayment, AveragePaymentWithPhaseTwoPeriod, CylbBreakdown, CylbDuration, CylbPayment, CylbPaymentWithFullPeriod, CylbPaymentWithPartialPeriod, CylbPaymentWithPhaseTwoPeriod, FullPeriodWithPaymentDate, LastYearPayment, NonFurloughPay, OnePeriodCylb, PartialPeriodWithPaymentDate, PaymentFrequency, PeriodWithPaymentDate, PhaseTwoPeriod, TwoPeriodCylb}
import services.Calculators.AmountRounding

trait CylbCalculator extends PreviousYearPeriod with Calculators {

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

  def phaseTwoCylb(
    averagePayment: AveragePaymentWithPhaseTwoPeriod,
    frequency: PaymentFrequency,
    cylbs: Seq[LastYearPayment],
    phaseTwoPeriod: PhaseTwoPeriod): CylbPaymentWithPhaseTwoPeriod = {
    val datesRequired = previousYearPayDate(frequency, phaseTwoPeriod.periodWithPaymentDate)
    val cylbOps = CylbDuration(frequency, phaseTwoPeriod.periodWithPaymentDate.period)
    val cylbBreakdown = previousYearFurlough(datesRequired, cylbs, cylbOps)

    val referencePay = Amount(averagePayment.referencePay.value.max(cylbBasedOnHours(cylbBreakdown.referencePay, phaseTwoPeriod).value))

    CylbPaymentWithPhaseTwoPeriod(referencePay, phaseTwoPeriod, averagePayment, cylbBreakdown)
  }

  private def cylbBasedOnHours(cylbReferencePay: Amount, phaseTwoPeriod: PhaseTwoPeriod): Amount =
    if (phaseTwoPeriod.isPartTime) {
      partTimeHoursCalculation(cylbReferencePay, phaseTwoPeriod.furloughed, phaseTwoPeriod.usual)
    } else {
      cylbReferencePay
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
    val lastYearPayments: Seq[LastYearPayment] = datesRequired.flatMap(date => cylbs.find(_.date == date))

    lastYearPayments match {
      case amount :: Nil                          => previousOrCurrent(amount, ops)
      case previousAmount :: currentAmount :: Nil => previousAndCurrent(ops, previousAmount, currentAmount)
    }
  }

  private def previousOrCurrent(lastYearPayment: LastYearPayment, ops: CylbDuration): OnePeriodCylb =
    if (ops.equivalentPeriodDays == 0) {
      val referencePay = Amount((lastYearPayment.amount.value / ops.fullPeriodLength) * ops.previousPeriodDays).halfUp
      OnePeriodCylb(referencePay, lastYearPayment.amount, ops.fullPeriodLength, ops.previousPeriodDays, lastYearPayment.date)
    } else {
      val referencePay = Amount((lastYearPayment.amount.value / ops.fullPeriodLength) * ops.equivalentPeriodDays).halfUp
      OnePeriodCylb(referencePay, lastYearPayment.amount, ops.fullPeriodLength, ops.equivalentPeriodDays, lastYearPayment.date)
    }

  private def previousAndCurrent(
    ops: CylbDuration,
    lastYearPaymentOne: LastYearPayment,
    lastYearPaymentTwo: LastYearPayment): TwoPeriodCylb = {
    val periodOneAmount = Amount((lastYearPaymentOne.amount.value / ops.fullPeriodLength) * ops.previousPeriodDays).halfUp
    val periodTwoAmount = Amount((lastYearPaymentTwo.amount.value / ops.fullPeriodLength) * ops.equivalentPeriodDays).halfUp

    val referencePay = Amount(periodOneAmount.value + periodTwoAmount.value)

    TwoPeriodCylb(
      referencePay,
      OnePeriodCylb(
        periodOneAmount,
        lastYearPaymentOne.amount,
        ops.fullPeriodLength,
        ops.previousPeriodDays,
        lastYearPaymentOne.date
      ),
      OnePeriodCylb(
        periodTwoAmount,
        lastYearPaymentTwo.amount,
        ops.fullPeriodLength,
        ops.equivalentPeriodDays,
        lastYearPaymentTwo.date
      )
    )
  }

}
