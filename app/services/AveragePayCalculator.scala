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
import models.{Amount, AveragePayment, AveragePaymentWithFullPeriod, AveragePaymentWithPartialPeriod, FullPeriodWithPaymentDate, NonFurloughPay, PartialPeriodWithPaymentDate, Period, PeriodWithPaymentDate}
import services.Calculators._

trait AveragePayCalculator {

  def calculateAveragePay(
    nonFurloughPay: NonFurloughPay,
    priorFurloughPeriod: Period,
    periods: Seq[PeriodWithPaymentDate],
    grossPay: Amount): Seq[AveragePayment] =
    periods map {
      case fp: FullPeriodWithPaymentDate =>
        AveragePaymentWithFullPeriod(Amount(daily(fp.period.period, priorFurloughPeriod, grossPay)), fp, grossPay, priorFurloughPeriod)
      case pp: PartialPeriodWithPaymentDate =>
        val nfp = determineNonFurloughPay(pp.period, nonFurloughPay)
        AveragePaymentWithPartialPeriod(
          nfp,
          Amount(daily(pp.period.partial, priorFurloughPeriod, grossPay)),
          pp,
          grossPay,
          priorFurloughPeriod)
    }

  protected def averageDailyCalculator(period: Period, amount: Amount): Amount =
    Amount(amount.value / period.countDays).halfUp

  private def daily(period: Period, priorFurloughPeriod: Period, grossPay: Amount): BigDecimal =
    period.countDays * averageDailyCalculator(priorFurloughPeriod, grossPay).value
}
