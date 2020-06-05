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
import models.{Amount, FullPeriodFurloughBreakdown, FullPeriodPensionBreakdown, FullPeriodWithPaymentDate, FurloughBreakdown, PartialPeriodFurloughBreakdown, PartialPeriodPensionBreakdown, PartialPeriodWithPaymentDate, PaymentFrequency, PaymentWithFullPeriod, PaymentWithPartialPeriod, PensionCalculationResult, PensionStatus, PhaseTwoFurloughBreakdown, PhaseTwoPensionBreakdown, PhaseTwoPensionCalculationResult}
import services.Calculators._

trait PensionCalculator extends FurloughCapCalculator with CommonCalculationService with Calculators {

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

  def phaseTwoPension(
    furloughBreakdowns: Seq[PhaseTwoFurloughBreakdown],
    frequency: PaymentFrequency,
    pensionStatus: PensionStatus): PhaseTwoPensionCalculationResult = {
    val breakdowns = furloughBreakdowns.map { furloughBreakdown =>
      val phaseTwoPeriod = furloughBreakdown.paymentWithPeriod.phaseTwoPeriod

      val threshold =
        thresholdFinder(frequency, phaseTwoPeriod.periodWithPaymentDate.paymentDate, PensionRate())

      val thresholdBasedOnDays = phaseTwoPeriod.periodWithPaymentDate match {
        case _: FullPeriodWithPaymentDate => threshold
        case pp: PartialPeriodWithPaymentDate =>
          threshold.copy(value = partialPeriodDailyCalculation(Amount(threshold.value), pp.period).value)
      }

      val thresholdBasedOnHours = if (phaseTwoPeriod.isPartTime) {
        thresholdBasedOnDays.copy(
          value = partTimeHoursCalculation(Amount(thresholdBasedOnDays.value), phaseTwoPeriod.furloughed, phaseTwoPeriod.usual).value)
      } else {
        thresholdBasedOnDays
      }
      val roundedFurloughGrant = furloughBreakdown.grant.down

      val grant = pensionStatus match {
        case DoesContribute => greaterThanAllowance(roundedFurloughGrant, thresholdBasedOnHours.value, PensionRate())
        case DoesNotContribute => Amount(0.0)
      }

      PhaseTwoPensionBreakdown(grant, furloughBreakdown.paymentWithPeriod, thresholdBasedOnHours, pensionStatus)
    }

    PhaseTwoPensionCalculationResult(breakdowns.map(_.grant.value).sum, breakdowns)
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

    val allowance = Amount((threshold.value / fullPeriodDays) * furloughDays).halfUp
    val roundedFurloughPayment = furloughPayment.down
    val grant = pensionStatus match {
      case DoesContribute    => greaterThanAllowance(roundedFurloughPayment, allowance.value, PensionRate())
      case DoesNotContribute => Amount(0.00)
    }

    PartialPeriodPensionBreakdown(grant, payment, threshold, allowance, pensionStatus)
  }

  protected def calculateFullPeriodPension(
    pensionStatus: PensionStatus,
    frequency: PaymentFrequency,
    furloughPayment: Amount,
    payment: PaymentWithFullPeriod): FullPeriodPensionBreakdown = {

    val threshold = thresholdFinder(frequency, payment.periodWithPaymentDate.paymentDate, PensionRate())
    val roundedFurloughPayment = furloughPayment.down
    val grant = pensionStatus match {
      case DoesContribute    => greaterThanAllowance(roundedFurloughPayment, threshold.value, PensionRate())
      case DoesNotContribute => Amount(0.00)
    }

    FullPeriodPensionBreakdown(grant, payment, threshold, Amount(threshold.value), pensionStatus)
  }
}
