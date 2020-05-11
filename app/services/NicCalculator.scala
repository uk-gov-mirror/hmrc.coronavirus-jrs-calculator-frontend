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

import models.Amount._
import models.Calculation.NicCalculationResult
import models.{AdditionalPayment, Amount, CalculationResult, FullPeriod, FullPeriodBreakdown, FullPeriodWithPaymentDate, PartialPeriod, PartialPeriodBreakdown, PartialPeriodWithPaymentDate, PaymentDate, PaymentFrequency, PeriodBreakdown, PeriodWithPaymentDate, TopUpPayment}
import services.Calculators._
import models.Period._

trait NicCalculator extends FurloughCapCalculator with CommonCalculationService {

  def calculateNicGrant(
    frequency: PaymentFrequency,
    furloughBreakdown: Seq[PeriodBreakdown],
    additionals: Seq[AdditionalPayment],
    topUps: Seq[TopUpPayment]): CalculationResult = {
    val nicBreakdowns = furloughBreakdown.map {
      case FullPeriodBreakdown(grant, periodWithPaymentDate) =>
        calculateFullPeriodNic(
          frequency,
          grant,
          periodWithPaymentDate.period,
          periodWithPaymentDate.paymentDate,
          additionalPayments(additionals, periodWithPaymentDate),
          topUpPayments(topUps, periodWithPaymentDate)
        )
      case PartialPeriodBreakdown(nonFurloughPay, grant, periodWithPaymentDate) =>
        calculatePartialPeriodNic(
          frequency,
          nonFurloughPay,
          grant,
          periodWithPaymentDate.period,
          periodWithPaymentDate.paymentDate,
          additionalPayments(additionals, periodWithPaymentDate),
          topUpPayments(topUps, periodWithPaymentDate)
        )
    }
    CalculationResult(NicCalculationResult, nicBreakdowns.map(_.grant.value).sum, nicBreakdowns)
  }

  protected def calculatePartialPeriodNic(
    frequency: PaymentFrequency,
    nonFurloughPay: Amount,
    furloughPayment: Amount,
    period: PartialPeriod,
    paymentDate: PaymentDate,
    additionalPayment: Option[Amount],
    topUp: Option[Amount]): PartialPeriodBreakdown = {

    val total = Amount(nonFurloughPay.value + sumValues(furloughPayment, additionalPayment, topUp))
    val calculationParameters = periodCalculation(total, frequency, paymentDate, furloughPayment, topUp)
    import calculationParameters._

    val dailyNi = grossNi.value / period.original.countDays
    val grant = niGrant(Amount(dailyNi * period.partial.countDays), apportion)

    PartialPeriodBreakdown(nonFurloughPay, grant, PartialPeriodWithPaymentDate(period, paymentDate))
  }

  protected def calculateFullPeriodNic(
    frequency: PaymentFrequency,
    furloughPayment: Amount,
    period: FullPeriod,
    paymentDate: PaymentDate,
    additionalPayment: Option[Amount],
    topUp: Option[Amount]): FullPeriodBreakdown = {

    val total = Amount(sumValues(furloughPayment, additionalPayment, topUp))
    val calculationParameters = periodCalculation(total, frequency, paymentDate, furloughPayment, topUp)
    import calculationParameters._

    val grant = niGrant(grossNi, apportion)

    FullPeriodBreakdown(grant, FullPeriodWithPaymentDate(period, paymentDate))
  }

  private def periodCalculation(
    total: Amount,
    frequency: PaymentFrequency,
    paymentDate: PaymentDate,
    furloughPayment: Amount,
    topUpPayment: Option[Amount]): CalculationParameters = {
    val roundedTotalPay = total.down
    val threshold: BigDecimal = thresholdFinder(frequency, paymentDate, NiRate())
    val grossNi: Amount = greaterThanAllowance(roundedTotalPay, threshold, NiRate())
    val apportion: BigDecimal = furloughPayment.value / (furloughPayment.value + topUpPayment.defaulted.value)

    CalculationParameters(roundedTotalPay, threshold, grossNi, apportion)
  }

  private def topUpPayments(topUps: Seq[TopUpPayment], periodWithPaymentDate: PeriodWithPaymentDate): Option[Amount] =
    topUps.find(_.date == periodWithPaymentDate.period.period.end).map(_.amount)

  private def additionalPayments(additionals: Seq[AdditionalPayment], periodWithPaymentDate: PeriodWithPaymentDate): Option[Amount] =
    additionals.find(_.date == periodWithPaymentDate.period.period.end).map(_.amount)

  private def sumValues(furloughPayment: Amount, additional: Option[Amount], topUp: Option[Amount]): BigDecimal =
    furloughPayment.value + additional.defaulted.value + topUp.defaulted.value

  private def niGrant(grossNi: Amount, apportion: BigDecimal) = Amount(grossNi.value * apportion).halfUp

  case class CalculationParameters(total: Amount, threshold: BigDecimal, grossNi: Amount, apportion: BigDecimal)
}
