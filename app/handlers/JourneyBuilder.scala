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

import models.EmployeeStarted.{After1Feb2019, OnOrBefore1Feb2019}
import models.PayMethod.{Regular, Variable}
import models.{BranchingQuestions, Journey, PhaseTwoReferencePay, PhaseTwoRegularPayData, PhaseTwoVariablePayData, PhaseTwoVariablePayWithCylbData, ReferencePay, RegularPay, RegularPayData, UserAnswers, VariablePay, VariablePayData, VariablePayWithCylb, VariablePayWithCylbData}
import cats.syntax.apply._
import models.UserAnswers.AnswerV

trait JourneyBuilder extends DataExtractor {

  def define(data: BranchingQuestions, cylbCutoff: LocalDate): Journey = data match {
    case BranchingQuestions(Regular, _, _)                                                     => RegularPay
    case BranchingQuestions(Variable, Some(After1Feb2019), Some(d)) if !d.isBefore(cylbCutoff) => VariablePay
    case BranchingQuestions(Variable, Some(After1Feb2019), Some(d)) if d.isBefore(cylbCutoff)  => VariablePayWithCylb
    case BranchingQuestions(Variable, Some(OnOrBefore1Feb2019), _)                             => VariablePayWithCylb
  }

  def journeyDataV(journey: Journey, userAnswers: UserAnswers): AnswerV[ReferencePay] = journey match {
    case RegularPay          => regularPayDataV(userAnswers)
    case VariablePay         => variablePayDataV(userAnswers)
    case VariablePayWithCylb => variablePayWithCylbDataV(userAnswers)
  }

  def phaseTwoJourneyDataV(journey: Journey, userAnswers: UserAnswers): AnswerV[PhaseTwoReferencePay] = journey match {
    case RegularPay          => phaseTwoRegularPayDataV(userAnswers)
    case VariablePay         => phaseTwoVariablePayDataV(userAnswers)
    case VariablePayWithCylb => phaseTwoVariablePayWithCylbDataV(userAnswers)
  }

  private def regularPayDataV(userAnswers: UserAnswers): AnswerV[RegularPayData] =
    (
      extractReferencePayDataV(userAnswers),
      extractSalaryV(userAnswers)
    ).mapN(RegularPayData.apply)

  private def phaseTwoRegularPayDataV(userAnswers: UserAnswers): AnswerV[PhaseTwoRegularPayData] =
    (
      extractPhaseTwoReferencePayDataV(userAnswers),
      extractSalaryV(userAnswers)
    ).mapN(PhaseTwoRegularPayData.apply)

  private[this] def variablePayDataV(userAnswers: UserAnswers): AnswerV[VariablePayData] =
    (
      extractReferencePayDataV(userAnswers),
      extractAnnualPayAmountV(userAnswers),
      extractNonFurloughV(userAnswers),
      extractPriorFurloughPeriodV(userAnswers)
    ).mapN(VariablePayData.apply)

  private def phaseTwoVariablePayDataV(userAnswers: UserAnswers): AnswerV[PhaseTwoVariablePayData] =
    (
      extractPhaseTwoReferencePayDataV(userAnswers),
      extractAnnualPayAmountV(userAnswers),
      extractPriorFurloughPeriodV(userAnswers)
    ).mapN(PhaseTwoVariablePayData.apply)

  private def variablePayWithCylbDataV(userAnswers: UserAnswers): AnswerV[VariablePayWithCylbData] =
    (
      extractReferencePayDataV(userAnswers),
      extractAnnualPayAmountV(userAnswers),
      extractNonFurloughV(userAnswers),
      extractPriorFurloughPeriodV(userAnswers),
    ).mapN { (referencePayData, grossPay, nonFurlough, priorFurlough) =>
      val cylbPayments = extractCylbPayments(userAnswers)
      VariablePayWithCylbData(
        referencePayData = referencePayData,
        grossPay = grossPay,
        nonFurloughPay = nonFurlough,
        priorFurlough = priorFurlough,
        cylbPayments = cylbPayments
      )
    }

  private def phaseTwoVariablePayWithCylbDataV(userAnswers: UserAnswers): AnswerV[PhaseTwoVariablePayWithCylbData] =
    (
      extractPhaseTwoReferencePayDataV(userAnswers),
      extractAnnualPayAmountV(userAnswers),
      extractPriorFurloughPeriodV(userAnswers)
    ).mapN { (referencePayData, annualPay, priorFurlough) =>
      val cylbPayments = extractCylbPayments(userAnswers)
      PhaseTwoVariablePayWithCylbData(
        referencePayData,
        annualPay,
        priorFurlough,
        cylbPayments
      )
    }
}
