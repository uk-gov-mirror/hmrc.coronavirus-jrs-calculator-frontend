/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import models.EmployeeStarted.{After1Feb2019, OnOrBefore1Feb2019}
import models.PayMethod.{Regular, Variable}
import models.{BranchingQuestions, Journey, ReferencePay, RegularPay, RegularPayData, UserAnswers, VariablePay, VariablePayData, VariablePayWithCylb, VariablePayWithCylbData}

trait JourneyBuilder extends DataExtractor {

  def define(data: BranchingQuestions): Journey = data match {
    case BranchingQuestions(Regular, _, _)                                                                  => RegularPay
    case BranchingQuestions(Variable, Some(After1Feb2019), Some(d)) if d.isAfter(LocalDate.of(2019, 4, 5))  => VariablePay
    case BranchingQuestions(Variable, Some(After1Feb2019), Some(d)) if d.isBefore(LocalDate.of(2019, 4, 6)) => VariablePayWithCylb
    case BranchingQuestions(Variable, Some(OnOrBefore1Feb2019), _)                                          => VariablePayWithCylb
  }

  def journeyData(journey: Journey, userAnswers: UserAnswers): Option[ReferencePay] = journey match {
    case RegularPay          => regularPayData(userAnswers)
    case VariablePay         => variablePayData(userAnswers)
    case VariablePayWithCylb => variablePayWithCylbData(userAnswers)
  }

  private def regularPayData(userAnswers: UserAnswers): Option[RegularPayData] =
    for {
      referencePayData <- extractReferencePayData(userAnswers)
      salary           <- extractSalary(userAnswers)
    } yield RegularPayData(referencePayData, salary)

  private def variablePayData(userAnswers: UserAnswers): Option[VariablePayData] =
    for {
      referencePayData <- extractReferencePayData(userAnswers)
      grossPay         <- extractVariableGrossPay(userAnswers)
      nonFurlough = extractNonFurlough(userAnswers)
      priorFurlough <- extractPriorFurloughPeriod(userAnswers)
    } yield VariablePayData(referencePayData, grossPay, nonFurlough, priorFurlough)

  private def variablePayWithCylbData(userAnswers: UserAnswers): Option[VariablePayWithCylbData] =
    for {
      referencePayData <- extractReferencePayData(userAnswers)
      grossPay         <- extractVariableGrossPay(userAnswers)
      nonFurlough = extractNonFurlough(userAnswers)
      priorFurlough <- extractPriorFurloughPeriod(userAnswers)
      cylbPayments = extractCylbPayments(userAnswers)
    } yield VariablePayWithCylbData(referencePayData, grossPay, nonFurlough, priorFurlough, cylbPayments)
}
