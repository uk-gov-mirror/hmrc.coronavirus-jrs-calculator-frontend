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

import models.{AdditionalPayment, FurloughCalculationResult, NicCalculationResult, NicCategory, PaymentFrequency, PensionCalculationResult, PensionStatus, Period, TopUpPayment, UserAnswers}
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
      ni = calculateNi(furlough, niAnswer, data.frequency, extractAdditionalPayment(userAnswers), extractTopUpPayment(userAnswers))
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

  private def calculateNi(
    furloughResult: FurloughCalculationResult,
    nic: NicCategory,
    frequency: PaymentFrequency,
    additionals: Seq[AdditionalPayment],
    topUps: Seq[TopUpPayment]): NicCalculationResult =
    calculateNicGrant(nic, frequency, furloughResult.periodBreakdowns, additionals, topUps)

  private def calculatePension(
    furloughResult: FurloughCalculationResult,
    pensionStatus: PensionStatus,
    frequency: PaymentFrequency): PensionCalculationResult =
    calculatePensionGrant(pensionStatus, frequency, furloughResult.periodBreakdowns)

}
