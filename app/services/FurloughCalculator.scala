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

import models.{FullPeriodFurloughBreakdown, FurloughCalculationResult, PartialPeriodFurloughBreakdown, PaymentFrequency, PaymentWithFullPeriod, PaymentWithPartialPeriod, PaymentWithPeriod}
import services.Calculators._
import utils.TaxYearFinder

trait FurloughCalculator extends FurloughCapCalculator with TaxYearFinder with Calculators {

  def calculateFurloughGrant(paymentFrequency: PaymentFrequency, payments: Seq[PaymentWithPeriod]): FurloughCalculationResult = {
    val breakdowns = payments.map {
      case fp: PaymentWithFullPeriod    => calculateFullPeriod(paymentFrequency, fp)
      case pp: PaymentWithPartialPeriod => calculatePartialPeriod(pp)
    }
    FurloughCalculationResult(breakdowns.map(_.grant.value).sum, breakdowns)
  }

  protected def calculateFullPeriod(
    paymentFrequency: PaymentFrequency,
    payment: PaymentWithFullPeriod,
  ): FullPeriodFurloughBreakdown = {
    val cap = furloughCap(paymentFrequency, payment.periodWithPaymentDate.period.period)

    val grant = claimableAmount(payment.furloughPayment, cap.value).halfUp

    FullPeriodFurloughBreakdown(grant, payment, cap)
  }

  protected def calculatePartialPeriod(payment: PaymentWithPartialPeriod): PartialPeriodFurloughBreakdown = {
    import payment.periodWithPaymentDate._
    val cap = partialFurloughCap(period.partial)

    val grant = claimableAmount(payment.furloughPayment, cap.value).halfUp

    PartialPeriodFurloughBreakdown(
      grant,
      payment,
      cap
    )
  }

}
