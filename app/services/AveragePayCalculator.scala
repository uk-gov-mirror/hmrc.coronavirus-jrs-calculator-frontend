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
import models.{Amount, FullPeriodWithPaymentDate, NonFurloughPay, PartialPeriodWithPaymentDate, PaymentWithFullPeriod, PaymentWithPartialPeriod, PaymentWithPeriod, Period, PeriodWithPaymentDate}
import services.Calculators._

trait AveragePayCalculator extends PeriodHelper {

  def calculateAveragePay(
    nonFurloughPay: NonFurloughPay,
    priorFurloughPeriod: Period,
    periods: Seq[PeriodWithPaymentDate],
    grossPay: Amount): Seq[PaymentWithPeriod] =
    periods map {
      case fp: FullPeriodWithPaymentDate =>
        val daily = periodDaysCount(fp.period.period) * averageDailyCalculator(priorFurloughPeriod, grossPay).value
        PaymentWithFullPeriod(Amount(daily), fp)
      case pp: PartialPeriodWithPaymentDate =>
        val nfp = determineNonFurloughPay(pp.period, nonFurloughPay)
        val daily = periodDaysCount(pp.period.partial) * averageDailyCalculator(priorFurloughPeriod, grossPay).value

        PaymentWithPartialPeriod(nfp, Amount(daily), pp)
    }

  protected def averageDailyCalculator(period: Period, amount: Amount): Amount =
    Amount(amount.value / periodDaysCount(period)).halfUp
}
