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

import models.NonFurloughPay.determineNonFurloughPay
import models.{Amount, AveragePayment, AveragePaymentWithFullPeriod, AveragePaymentWithPartialPeriod, AveragePaymentWithPhaseTwoPeriod, FullPeriodWithPaymentDate, NonFurloughPay, PartialPeriodWithPaymentDate, Period, PeriodWithPaymentDate, PhaseTwoPeriod}
import services.Calculators._

trait AveragePayCalculator extends Calculators {

  def calculateAveragePay(
    nonFurloughPay: NonFurloughPay,
    priorFurloughPeriod: Period,
    periods: Seq[PeriodWithPaymentDate],
    annualPay: Amount): Seq[AveragePayment] =
    periods map {
      case fp: FullPeriodWithPaymentDate =>
        AveragePaymentWithFullPeriod(daily(fp.period.period, priorFurloughPeriod, annualPay), fp, annualPay, priorFurloughPeriod)
      case pp: PartialPeriodWithPaymentDate =>
        val nfp = determineNonFurloughPay(pp.period, nonFurloughPay)
        AveragePaymentWithPartialPeriod(nfp, daily(pp.period.partial, priorFurloughPeriod, annualPay), pp, annualPay, priorFurloughPeriod)
    }

  def phaseTwoAveragePay(
    annualPay: Amount,
    priorFurloughPeriod: Period,
    periods: Seq[PhaseTwoPeriod]): Seq[AveragePaymentWithPhaseTwoPeriod] =
    periods.map { phaseTwoPeriod =>
      val basedOnDays = phaseTwoPeriod.periodWithPaymentDate match {
        case fp: FullPeriodWithPaymentDate    => daily(fp.period.period, priorFurloughPeriod, annualPay)
        case pp: PartialPeriodWithPaymentDate => daily(pp.period.partial, priorFurloughPeriod, annualPay)
      }

      val referencePay = if (phaseTwoPeriod.isPartTime) {
        partTimeHoursCalculation(basedOnDays, phaseTwoPeriod.furloughed, phaseTwoPeriod.usual)
      } else {
        basedOnDays
      }

      AveragePaymentWithPhaseTwoPeriod(referencePay, annualPay, priorFurloughPeriod, phaseTwoPeriod)
    }

  protected def averageDailyCalculator(period: Period, amount: Amount): Amount =
    Amount(amount.value / period.countDays).halfUp

  private def daily(period: Period, priorFurloughPeriod: Period, annualPay: Amount): Amount =
    Amount(period.countDays * averageDailyCalculator(priorFurloughPeriod, annualPay).value)
}
