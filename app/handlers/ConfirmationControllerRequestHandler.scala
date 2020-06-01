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

import cats.data.Validated.{Invalid, Valid}
import models.UserAnswers.AnswerV
import models._
import services._
import viewmodels.{ConfirmationDataResult, ConfirmationMetadata, ConfirmationViewBreakdown}
import cats.syntax.apply._

trait ConfirmationControllerRequestHandler
    extends FurloughCalculator with NicCalculator with PensionCalculator with JourneyBuilder with ReferencePayCalculator {

  def loadResultData(userAnswers: UserAnswers): AnswerV[ConfirmationDataResult] =
    (meta(userAnswers), breakdown(userAnswers)).mapN(ConfirmationDataResult.apply)

  private def breakdown(userAnswers: UserAnswers): AnswerV[ConfirmationViewBreakdown] =
    extractBranchingQuestionsV(userAnswers) match {
      case Valid(questions) =>
        journeyDataV(define(questions), userAnswers) match {

          case Valid(data) =>
            val payments = calculateReferencePay(data)
            val furlough = calculateFurloughGrant(data.frequency, payments)

            (
              extractNicCategoryV(userAnswers),
              extractPensionStatusV(userAnswers)
            ).mapN { (niAnswer, pensionAnswer) =>
              val ni =
                calculateNi(furlough, niAnswer, data.frequency, extractAdditionalPayment(userAnswers), extractTopUpPayment(userAnswers))
              val pension = calculatePension(furlough, pensionAnswer, data.frequency)
              ConfirmationViewBreakdown(furlough, ni, pension)
            }

          case invalid @ Invalid(_) => invalid

        }
      case inv @ Invalid(e) => inv
    }

  private def meta(userAnswers: UserAnswers): AnswerV[ConfirmationMetadata] =
    (
      extractFurloughPeriodV(userAnswers),
      extractClaimPeriodStartV(userAnswers),
      extractClaimPeriodEndV(userAnswers),
      extractPaymentFrequencyV(userAnswers),
      extractNicCategoryV(userAnswers),
      extractPensionStatusV(userAnswers)
    ).mapN {
      case (furloughPeriod, claimStart, claimEnd, frequency, nicCategory, pensionStatus) =>
        ConfirmationMetadata(Period(claimStart, claimEnd), furloughPeriod, frequency, nicCategory, pensionStatus)
    }

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
