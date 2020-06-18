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

import java.time.LocalDate

import cats.data.Validated.{Invalid, Valid}
import cats.syntax.apply._
import models.UserAnswers.AnswerV
import models._
import services._
import viewmodels._

trait ConfirmationControllerRequestHandler
    extends FurloughCalculator with NicCalculator with PensionCalculator with JourneyBuilder with ReferencePayCalculator
    with LastYearPayControllerRequestHandler {

  def loadResultData(userAnswers: UserAnswers): AnswerV[ConfirmationDataResult] =
    metaData(userAnswers) match {
      case Valid(m)       => validateBreakdown(userAnswers, m)
      case i @ Invalid(_) => i
    }

  private def breakDown(meta: Metadata, userAnswers: UserAnswers): AnswerV[ViewBreakdown] = meta match {
    case _: ConfirmationMetadataWithoutNicAndPension => breakdownWithoutNicAndPension(userAnswers)
    case ConfirmationMetadata(claim, _, _, _, _) =>
      if (claim.start.isBefore(LocalDate.of(2020, 7, 1))) breakdown(userAnswers) else phaseTwoBreakdown(userAnswers)
  }

  private def validateBreakdown(userAnswers: UserAnswers, m: Metadata): AnswerV[ConfirmationDataResult] =
    breakDown(m, userAnswers) match {
      case Valid(bd)      => Valid(confirmationResult(m, bd))
      case i @ Invalid(_) => i
    }

  private def confirmationResult(metadata: Metadata, breakdown: ViewBreakdown): ConfirmationDataResult =
    (metadata, breakdown) match {
      case (data: ConfirmationMetadata, b: ConfirmationViewBreakdown)         => PhaseOneConfirmationDataResult(data, b)
      case (data: ConfirmationMetadata, b: PhaseTwoConfirmationViewBreakdown) => PhaseTwoConfirmationDataResult(data, b)
      case (m: ConfirmationMetadataWithoutNicAndPension, b: ConfirmationViewBreakdownWithoutNicAndPension) =>
        ConfirmationDataResultWithoutNicAndPension(m, b)
      case _ =>
        throw new Exception("ConfirmationControllerRequestHandler.confirmationResult: Unexpected combination of metadata and breakdown")
    }

  private def breakdown(userAnswers: UserAnswers): AnswerV[ConfirmationViewBreakdown] =
    extractBranchingQuestionsV(userAnswers) match {
      case Valid(questions) =>
        journeyDataV(define(questions, cylbCutoff(userAnswers)), userAnswers) match {

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

  private def breakdownWithoutNicAndPension(userAnswers: UserAnswers): AnswerV[ConfirmationViewBreakdownWithoutNicAndPension] =
    extractBranchingQuestionsV(userAnswers) match {
      case Valid(questions) =>
        phaseTwoJourneyDataV(define(questions, cylbCutoff(userAnswers)), userAnswers) match {
          case Valid(data) =>
            val payments: Seq[PaymentWithPhaseTwoPeriod] = phaseTwoReferencePay(data)
            val furlough: PhaseTwoFurloughCalculationResult = phaseTwoFurlough(data.frequency, payments)
            Valid(ConfirmationViewBreakdownWithoutNicAndPension(furlough))
          case i @ Invalid(_) => i
        }
      case i @ Invalid(_) => i
    }

  private def phaseTwoBreakdown(userAnswers: UserAnswers): AnswerV[PhaseTwoConfirmationViewBreakdown] =
    extractBranchingQuestionsV(userAnswers) match {
      case Valid(questions) =>
        phaseTwoJourneyDataV(define(questions, cylbCutoff(userAnswers)), userAnswers) match {
          case Valid(data) =>
            val payments = phaseTwoReferencePay(data)
            val furlough = phaseTwoFurlough(data.frequency, payments)

            (
              extractNicCategoryV(userAnswers),
              extractPensionStatusV(userAnswers)
            ).mapN { (niAnswer, pensionAnswer) =>
              val ni = phaseTwoNic(furlough.periodBreakdowns, data.frequency, niAnswer)
              val pension = phaseTwoPension(furlough.periodBreakdowns, data.frequency, pensionAnswer)

              PhaseTwoConfirmationViewBreakdown(furlough, ni, pension)
            }

          case i @ Invalid(_) => i
        }
      case i @ Invalid(_) => i
    }

  private def metaData(userAnswers: UserAnswers): AnswerV[Metadata] =
    extractClaimPeriodStartV(userAnswers) match {
      case Valid(start) =>
        if (start.getMonthValue > 7) metaWithoutNicAndPension(userAnswers) else metaWithNicAndPension(userAnswers)
      case i @ Invalid(_) => i
    }

  private def metaWithNicAndPension(userAnswers: UserAnswers): AnswerV[ConfirmationMetadata] =
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

  private def metaWithoutNicAndPension(userAnswers: UserAnswers): AnswerV[ConfirmationMetadataWithoutNicAndPension] =
    (
      extractFurloughPeriodV(userAnswers),
      extractClaimPeriodStartV(userAnswers),
      extractClaimPeriodEndV(userAnswers),
      extractPaymentFrequencyV(userAnswers)
    ).mapN {
      case (furloughPeriod, claimStart, claimEnd, frequency) =>
        ConfirmationMetadataWithoutNicAndPension(Period(claimStart, claimEnd), furloughPeriod, frequency)
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
