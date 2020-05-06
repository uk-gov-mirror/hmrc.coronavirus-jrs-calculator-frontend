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
import models.{Amount, CylbDuration, CylbOperators, CylbPayment, FullPeriodWithPaymentDate, NonFurloughPay, PartialPeriodWithPaymentDate, PaymentFrequency, PaymentWithFullPeriod, PaymentWithPartialPeriod, PaymentWithPeriod, PeriodWithPaymentDate}

trait CylbCalculator extends PreviousYearPeriod {

  def calculateCylb(
    nonFurloughPay: NonFurloughPay,
    frequency: PaymentFrequency,
    cylbs: Seq[CylbPayment],
    periods: Seq[PeriodWithPaymentDate]): Seq[PaymentWithPeriod] =
    for {
      period: PeriodWithPaymentDate <- periods
      datesRequired = previousYearPayDate(frequency, period)
      nfp = determineNonFurloughPay(period.period, nonFurloughPay)
    } yield cylbsAmount(frequency, period, datesRequired, nfp, cylbs)

  private def cylbsAmount(
    frequency: PaymentFrequency,
    period: PeriodWithPaymentDate,
    datesRequired: Seq[LocalDate],
    nfp: Amount,
    cylbs: Seq[CylbPayment]): PaymentWithPeriod = {
    val cylbOps: CylbDuration = CylbDuration(frequency, period.period)
    val furlough: Amount = previousYearFurlough(datesRequired, cylbs, cylbOps)

    period match {
      case fp: FullPeriodWithPaymentDate    => PaymentWithFullPeriod(furlough, fp)
      case pp: PartialPeriodWithPaymentDate => PaymentWithPartialPeriod(nfp, furlough, pp)
    }
  }

  private def previousYearFurlough(datesRequired: Seq[LocalDate], cylbs: Seq[CylbPayment], ops: CylbDuration): Amount = {
    val amounts: Seq[Amount] = datesRequired.flatMap(date => cylbs.find(_.date == date)).map(_.amount)

    amounts match {
      case amount :: Nil                          => previousOrCurrent(amount, ops)
      case previousAmount :: currentAmount :: Nil => previousAndCurrent(ops, previousAmount, currentAmount)
    }
  }

  private def previousOrCurrent(amount: Amount, ops: CylbDuration) =
    if (ops.equivalentPeriodDays == 0)
      Amount((amount.value / ops.fullPeriodLength) * ops.previousPeriodDays)
    else Amount((amount.value / ops.fullPeriodLength) * ops.equivalentPeriodDays)

  private def previousAndCurrent(ops: CylbDuration, previousAmount: Amount, currentAmount: Amount): Amount =
    Amount(
      ((previousAmount.value / ops.fullPeriodLength) * ops.previousPeriodDays) + ((currentAmount.value / ops.fullPeriodLength) * ops.equivalentPeriodDays))

}
