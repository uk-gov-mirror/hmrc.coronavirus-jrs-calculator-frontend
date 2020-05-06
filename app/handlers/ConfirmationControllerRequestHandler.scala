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

package handlers

import models.Calculation.{NicCalculationResult, PensionCalculationResult}
import models.NicCategory.{Nonpayable, Payable}
import models.PensionStatus.{DoesContribute, DoesNotContribute}
import models.{Amount, CalculationResult, FullPeriodBreakdown, NicCategory, PartialPeriodBreakdown, PaymentFrequency, PensionStatus, Period, UserAnswers}
import services._
import viewmodels.{ConfirmationDataResult, ConfirmationMetadata, ConfirmationViewBreakdown}

trait ConfirmationControllerRequestHandler
    extends FurloughCalculator with NicCalculator with PensionCalculator with JourneyBuilder with ReferencePayCalculator {

  def loadResultData(userAnswers: UserAnswers): Option[ConfirmationDataResult] =
    for {
      breakdown <- breakdown(userAnswers)
      metadata  <- meta(userAnswers)
    } yield ConfirmationDataResult(metadata, breakdown)

  private def breakdown(userAnswers: UserAnswers): Option[ConfirmationViewBreakdown] =
    for {
      questions <- extractBranchingQuestions(userAnswers)
      data      <- journeyData(define(questions), userAnswers)
      payments = calculateReferencePay(data)
      furlough = calculateFurloughGrant(data.frequency, payments)
      niAnswer <- extractNicCategory(userAnswers)
      ni = calculateNi(furlough, niAnswer, data.frequency)
      pensionAnswer <- extractPensionStatus(userAnswers)
      pension = calculatePension(furlough, pensionAnswer, data.frequency)
    } yield ConfirmationViewBreakdown(furlough, ni, pension)

  private def meta(userAnswers: UserAnswers): Option[ConfirmationMetadata] =
    for {
      furloughPeriod <- extractFurloughPeriod(userAnswers)
      claimStart     <- extractClaimPeriodStart(userAnswers)
      claimEnd       <- extractClaimPeriodEnd(userAnswers)
      frequency      <- extractPaymentFrequency(userAnswers)
      nicCategory    <- extractNicCategory(userAnswers)
      pensionStatus  <- extractPensionStatus(userAnswers)
    } yield ConfirmationMetadata(Period(claimStart, claimEnd), furloughPeriod, frequency, nicCategory, pensionStatus)

  private def calculateNi(furloughResult: CalculationResult, nic: NicCategory, frequency: PaymentFrequency): CalculationResult =
    nic match {
      case Payable => calculateNicGrant(frequency, furloughResult.payPeriodBreakdowns, Seq.empty, Seq.empty) //TODO wire with UI data
      case Nonpayable =>
        CalculationResult(
          NicCalculationResult,
          0.0,
          furloughResult.payPeriodBreakdowns.map {
            case FullPeriodBreakdown(_, withPaymentDate)       => FullPeriodBreakdown(Amount(0.0), withPaymentDate)
            case PartialPeriodBreakdown(_, _, withPaymentDate) => PartialPeriodBreakdown(Amount(0.0), Amount(0.0), withPaymentDate)
          }
        )
    }

  private def calculatePension(
    furloughResult: CalculationResult,
    payStatus: PensionStatus,
    frequency: PaymentFrequency): CalculationResult =
    payStatus match {
      case DoesContribute => calculatePensionGrant(frequency, furloughResult.payPeriodBreakdowns)
      case DoesNotContribute =>
        CalculationResult(
          PensionCalculationResult,
          0.0,
          furloughResult.payPeriodBreakdowns.map {
            case FullPeriodBreakdown(_, withPaymentDate)       => FullPeriodBreakdown(Amount(0.0), withPaymentDate)
            case PartialPeriodBreakdown(_, _, withPaymentDate) => PartialPeriodBreakdown(Amount(0.0), Amount(0.0), withPaymentDate)
          }
        )
    }
}
