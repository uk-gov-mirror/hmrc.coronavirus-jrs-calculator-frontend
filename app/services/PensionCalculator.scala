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

import models.PensionStatus.{DoesContribute, DoesNotContribute}
import models.{Amount, FullPeriodFurloughBreakdown, FullPeriodPensionBreakdown, FurloughBreakdown, PartialPeriodFurloughBreakdown, PartialPeriodPensionBreakdown, PaymentFrequency, PaymentWithFullPeriod, PaymentWithPartialPeriod, PensionCalculationResult, PensionStatus}
import services.Calculators._

trait PensionCalculator extends FurloughCapCalculator with CommonCalculationService {

  def calculatePensionGrant(
    pensionStatus: PensionStatus,
    frequency: PaymentFrequency,
    furloughBreakdown: Seq[FurloughBreakdown]): PensionCalculationResult = {
    val pensionBreakdowns = furloughBreakdown.map {
      case fp: FullPeriodFurloughBreakdown =>
        calculateFullPeriodPension(pensionStatus, frequency, fp.grant, fp.paymentWithPeriod)
      case pp: PartialPeriodFurloughBreakdown =>
        calculatePartialPeriodPension(pensionStatus, frequency, pp.grant, pp.paymentWithPeriod)
    }

    PensionCalculationResult(pensionBreakdowns.map(_.grant.value).sum, pensionBreakdowns)
  }

  protected def calculatePartialPeriodPension(
    pensionStatus: PensionStatus,
    frequency: PaymentFrequency,
    furloughPayment: Amount,
    payment: PaymentWithPartialPeriod): PartialPeriodPensionBreakdown = {

    import payment.periodWithPaymentDate._

    val fullPeriodDays = period.original.countDays
    val furloughDays = period.partial.countDays
    val threshold = thresholdFinder(frequency, paymentDate, PensionRate())

    val allowance = Amount((threshold / fullPeriodDays) * furloughDays).halfUp
    val roundedFurloughPayment = furloughPayment.down
    val grant = pensionStatus match {
      case DoesContribute    => greaterThanAllowance(roundedFurloughPayment, allowance.value, PensionRate())
      case DoesNotContribute => Amount(0.00)
    }

    PartialPeriodPensionBreakdown(grant, payment)
  }

  protected def calculateFullPeriodPension(
    pensionStatus: PensionStatus,
    frequency: PaymentFrequency,
    furloughPayment: Amount,
    payment: PaymentWithFullPeriod): FullPeriodPensionBreakdown = {

    val threshold = thresholdFinder(frequency, payment.periodWithPaymentDate.paymentDate, PensionRate())
    val roundedFurloughPayment = furloughPayment.down
    val grant = pensionStatus match {
      case DoesContribute    => greaterThanAllowance(roundedFurloughPayment, threshold, PensionRate())
      case DoesNotContribute => Amount(0.00)
    }

    FullPeriodPensionBreakdown(grant, payment)
  }
}
