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
import models.NicCategory.{Nonpayable, Payable}
import models.Period._
import models.{AdditionalPayment, Amount, FullPeriodFurloughBreakdown, FullPeriodNicBreakdown, FurloughBreakdown, NicCalculationResult, NicCategory, PartialPeriodFurloughBreakdown, PartialPeriodNicBreakdown, PaymentDate, PaymentFrequency, PaymentWithFullPeriod, PaymentWithPartialPeriod, PeriodWithPaymentDate, TopUpPayment}
import services.Calculators._

trait NicCalculator extends FurloughCapCalculator with CommonCalculationService {

  def calculateNicGrant(
    nicCategory: NicCategory,
    frequency: PaymentFrequency,
    furloughBreakdowns: Seq[FurloughBreakdown],
    additionals: Seq[AdditionalPayment],
    topUps: Seq[TopUpPayment]): NicCalculationResult = {
    val breakdowns = furloughBreakdowns.map {
      case fp: FullPeriodFurloughBreakdown =>
        calculateFullPeriodNic(
          nicCategory,
          frequency,
          fp.grant,
          fp.paymentWithPeriod,
          additionalPayments(additionals, fp.paymentWithPeriod.periodWithPaymentDate),
          topUpPayments(topUps, fp.paymentWithPeriod.periodWithPaymentDate)
        )
      case pp: PartialPeriodFurloughBreakdown =>
        calculatePartialPeriodNic(
          nicCategory,
          frequency,
          pp.grant,
          pp.paymentWithPeriod,
          additionalPayments(additionals, pp.paymentWithPeriod.periodWithPaymentDate),
          topUpPayments(topUps, pp.paymentWithPeriod.periodWithPaymentDate)
        )
    }
    NicCalculationResult(breakdowns.map(_.grant.value).sum, breakdowns)
  }

  protected def calculatePartialPeriodNic(
    nicCategory: NicCategory,
    frequency: PaymentFrequency,
    furloughPayment: Amount,
    payment: PaymentWithPartialPeriod,
    additionalPayment: Option[Amount],
    topUp: Option[Amount]): PartialPeriodNicBreakdown = {

    import payment.periodWithPaymentDate._

    val total = Amount(payment.nonFurloughPay.value + sumValues(furloughPayment, additionalPayment, topUp))
    val calculationParameters = periodCalculation(total, frequency, paymentDate, furloughPayment, topUp)
    import calculationParameters._

    val dailyNi = grossNi.value / period.original.countDays
    val grant = nicCategory match {
      case Payable    => niGrant(Amount(dailyNi * period.partial.countDays), apportion)
      case Nonpayable => Amount(0.00)
    }

    PartialPeriodNicBreakdown(grant, topUp.defaulted, additionalPayment.defaulted, payment)
  }

  protected def calculateFullPeriodNic(
    nicCategory: NicCategory,
    frequency: PaymentFrequency,
    furloughPayment: Amount,
    payment: PaymentWithFullPeriod,
    additionalPayment: Option[Amount],
    topUp: Option[Amount]): FullPeriodNicBreakdown = {

    import payment.periodWithPaymentDate._

    val total = Amount(sumValues(furloughPayment, additionalPayment, topUp))
    val calculationParameters = periodCalculation(total, frequency, paymentDate, furloughPayment, topUp)
    import calculationParameters._

    val grant = nicCategory match {
      case Payable    => niGrant(grossNi, apportion)
      case Nonpayable => Amount(0.00)
    }

    FullPeriodNicBreakdown(grant, topUp.defaulted, additionalPayment.defaulted, payment)
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
