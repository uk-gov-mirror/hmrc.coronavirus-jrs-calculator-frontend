/*
 * Copyright 2021 HM Revenue & Customs
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

import models.NonFurloughPay.determineNonFurloughPay
import models.{Amount, AveragePayment, AveragePaymentWithFullPeriod, AveragePaymentWithPartialPeriod, AveragePaymentWithPhaseTwoPeriod, FullPeriodWithPaymentDate, NonFurloughPay, PartialPeriodWithPaymentDate, Period, PeriodWithPaymentDate, PhaseTwoPeriod, StatutoryLeaveData}
import services.Calculators._

trait AveragePayCalculator extends Calculators {

  def calculateAveragePay(nonFurloughPay: NonFurloughPay,
                          priorFurloughPeriod: Period,
                          periods: Seq[PeriodWithPaymentDate],
                          annualPay: Amount): Seq[AveragePayment] =
    periods map {
      case fp: FullPeriodWithPaymentDate =>
        AveragePaymentWithFullPeriod(
          referencePay = daily(fp.period.period, priorFurloughPeriod, annualPay, None),
          periodWithPaymentDate = fp,
          annualPay = annualPay,
          priorFurloughPeriod = priorFurloughPeriod
        )
      case pp: PartialPeriodWithPaymentDate =>
        val nfp = determineNonFurloughPay(pp.period, nonFurloughPay)
        AveragePaymentWithPartialPeriod(
          nonFurloughPay = nfp,
          referencePay = daily(pp.period.partial, priorFurloughPeriod, annualPay, None),
          periodWithPaymentDate = pp,
          annualPay = annualPay,
          priorFurloughPeriod = priorFurloughPeriod
        )
    }

  def phaseTwoAveragePay(annualPay: Amount,
                         priorFurloughPeriod: Period,
                         periods: Seq[PhaseTwoPeriod],
                         statutoryLeaveData: Option[StatutoryLeaveData]): Seq[AveragePaymentWithPhaseTwoPeriod] =
    periods.map { phaseTwoPeriod =>
      val basedOnDays = phaseTwoPeriod.periodWithPaymentDate match {
        case fp: FullPeriodWithPaymentDate    => daily(fp.period.period, priorFurloughPeriod, annualPay, statutoryLeaveData)
        case pp: PartialPeriodWithPaymentDate => daily(pp.period.partial, priorFurloughPeriod, annualPay, statutoryLeaveData)
      }

      val referencePay = if (phaseTwoPeriod.isPartTime) {
        partTimeHoursCalculation(basedOnDays, phaseTwoPeriod.furloughed, phaseTwoPeriod.usual)
      } else {
        basedOnDays
      }

      AveragePaymentWithPhaseTwoPeriod(referencePay, annualPay, priorFurloughPeriod, phaseTwoPeriod, statutoryLeaveData)
    }

  protected def averageDailyCalculator(period: Period, amount: Amount, statLeaveAmount: BigDecimal, statLeaveDays: Int): Amount =
    Amount((amount.value - statLeaveAmount) / (period.countDays - statLeaveDays)).halfUp

  protected def daily(period: Period,
                      priorFurloughPeriod: Period,
                      annualPay: Amount,
                      statutoryLeaveData: Option[StatutoryLeaveData]): Amount = {

    val statLeaveAmount: BigDecimal = statutoryLeaveData.fold[BigDecimal](0)(_.pay)
    val statLeaveDays: Int          = statutoryLeaveData.fold[Int](0)(_.days)

    Amount(period.countDays * averageDailyCalculator(priorFurloughPeriod, annualPay, statLeaveAmount, statLeaveDays).value)
  }
}
