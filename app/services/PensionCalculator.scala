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

import models.Calculation.PensionCalculationResult
import models.{Amount, CalculationResult, FullPeriod, FullPeriodBreakdown, FullPeriodWithPaymentDate, PartialPeriodBreakdown, PartialPeriodWithPaymentDate, PaymentDate, PaymentFrequency, PeriodBreakdown}
import services.Calculators._

trait PensionCalculator extends FurloughCapCalculator with CommonCalculationService {

  def calculatePensionGrant(frequency: PaymentFrequency, furloughBreakdown: Seq[PeriodBreakdown]): CalculationResult = {
    val pensionBreakdowns = furloughBreakdown.map {
      case FullPeriodBreakdown(grant, period) =>
        calculateFullPeriodPension(frequency, grant, period.period, period.paymentDate)
      case PartialPeriodBreakdown(nonFurlough, grant, periodWithPaymentDate) =>
        calculatePartialPeriodPension(frequency, nonFurlough, grant, periodWithPaymentDate)
    }

    CalculationResult(PensionCalculationResult, pensionBreakdowns.map(_.grant.value).sum, pensionBreakdowns)
  }

  private def calculatePartialPeriodPension(
    frequency: PaymentFrequency,
    grossPay: Amount,
    furloughPayment: Amount,
    period: PartialPeriodWithPaymentDate): PartialPeriodBreakdown = {
    val fullPeriodDays = period.period.original.countDays
    val furloughDays = period.period.partial.countDays
    val threshold = thresholdFinder(frequency, period.paymentDate, PensionRate())

    val allowance = Amount((threshold / fullPeriodDays) * furloughDays).halfUp
    val roundedFurloughPayment = furloughPayment.down
    val grant = greaterThanAllowance(roundedFurloughPayment, allowance.value, PensionRate())

    PartialPeriodBreakdown(grossPay, grant, PartialPeriodWithPaymentDate(period.period, period.paymentDate))
  }

  protected def calculateFullPeriodPension(
    frequency: PaymentFrequency,
    furloughPayment: Amount,
    period: FullPeriod,
    paymentDate: PaymentDate): FullPeriodBreakdown = {

    val threshold = thresholdFinder(frequency, paymentDate, PensionRate())
    val roundedFurloughPayment = furloughPayment.down
    val grant = greaterThanAllowance(roundedFurloughPayment, threshold, PensionRate())

    FullPeriodBreakdown(grant, FullPeriodWithPaymentDate(period, paymentDate))
  }
}
