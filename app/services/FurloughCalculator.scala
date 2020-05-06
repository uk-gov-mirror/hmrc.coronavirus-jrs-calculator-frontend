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

import models.Calculation.FurloughCalculationResult
import models.{Amount, CalculationResult, FullPeriodBreakdown, PartialPeriodBreakdown, PartialPeriodWithPaymentDate, PaymentFrequency, PaymentWithFullPeriod, PaymentWithPartialPeriod, PaymentWithPeriod, PeriodBreakdown}
import services.Calculators._
import utils.TaxYearFinder

trait FurloughCalculator extends FurloughCapCalculator with TaxYearFinder with Calculators {

  def calculateFurloughGrant(paymentFrequency: PaymentFrequency, payments: Seq[PaymentWithPeriod]): CalculationResult = {
    val paymentDateBreakdowns = payPeriodBreakdownFromRegularPayment(paymentFrequency, payments)
    CalculationResult(FurloughCalculationResult, paymentDateBreakdowns.map(_.grant.value).sum, paymentDateBreakdowns)
  }

  protected def payPeriodBreakdownFromRegularPayment(
    paymentFrequency: PaymentFrequency,
    paymentsWithPeriod: Seq[PaymentWithPeriod]): Seq[PeriodBreakdown] =
    paymentsWithPeriod.map {
      case fp: PaymentWithFullPeriod =>
        FullPeriodBreakdown(calculateFullPeriod(paymentFrequency, fp), fp.periodWithPaymentDate)
      case pp: PaymentWithPartialPeriod =>
        calculatePartialPeriod(pp)
    }

  protected def calculateFullPeriod(
    paymentFrequency: PaymentFrequency,
    payment: PaymentWithFullPeriod,
  ): Amount = {
    val cap = furloughCap(paymentFrequency, payment.periodWithPaymentDate.period.period)

    claimableAmount(payment.furloughPayment, cap).halfUp
  }

  protected def calculatePartialPeriod(payment: PaymentWithPartialPeriod): PartialPeriodBreakdown = {
    import payment.periodWithPaymentDate._
    val cap = partialFurloughCap(period.partial)

    PartialPeriodBreakdown(
      payment.nonFurloughPay,
      claimableAmount(payment.furloughPayment, cap).halfUp,
      PartialPeriodWithPaymentDate(period, paymentDate))
  }

}
