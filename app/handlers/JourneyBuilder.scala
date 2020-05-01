/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import models.EmployeeStarted.{After1Feb2019, OnOrBefore1Feb2019}
import models.PayMethod.{Regular, Variable}
import models.{BranchingQuestions, Journey, JourneyData, RegularPay, RegularPayData, UserAnswers, VariablePay, VariablePayData, VariablePayWithCylb, VariablePayWithCylbData}

trait JourneyBuilder extends DataExtractor {

  def define(data: BranchingQuestions): Journey = data match {
    case BranchingQuestions(Regular, _, _)                                                                  => RegularPay
    case BranchingQuestions(Variable, Some(After1Feb2019), Some(d)) if d.isAfter(LocalDate.of(2019, 4, 5))  => VariablePay
    case BranchingQuestions(Variable, Some(After1Feb2019), Some(d)) if d.isBefore(LocalDate.of(2019, 4, 6)) => VariablePayWithCylb
    case BranchingQuestions(Variable, Some(OnOrBefore1Feb2019), _)                                          => VariablePayWithCylb
  }

  def journeyData(journey: Journey, userAnswers: UserAnswers): Option[JourneyData] = journey match {
    case RegularPay          => regularPayData(userAnswers)
    case VariablePay         => variablePayData(userAnswers)
    case VariablePayWithCylb => variablePayWithCylbData(userAnswers)
  }

  private def regularPayData(userAnswers: UserAnswers): Option[RegularPayData] =
    for {
      coreData <- extractJourneyCoreData(userAnswers)
      salary   <- extractSalary(userAnswers)
    } yield RegularPayData(coreData, salary)

  private def variablePayData(userAnswers: UserAnswers): Option[VariablePayData] =
    for {
      coreData <- extractJourneyCoreData(userAnswers)
      grossPay <- extractVariableGrossPay(userAnswers)
      nonFurlough = extractNonFurlough(userAnswers)
      priorFurlough <- extractPriorFurloughPeriod(userAnswers)
    } yield VariablePayData(coreData, grossPay, nonFurlough, priorFurlough)

  private def variablePayWithCylbData(userAnswers: UserAnswers): Option[VariablePayWithCylbData] =
    for {
      coreData <- extractJourneyCoreData(userAnswers)
      grossPay <- extractVariableGrossPay(userAnswers)
      nonFurlough = extractNonFurlough(userAnswers)
      priorFurlough <- extractPriorFurloughPeriod(userAnswers)
      cylbPayments = extractCylbPayments(userAnswers)
    } yield VariablePayWithCylbData(coreData, grossPay, nonFurlough, priorFurlough, cylbPayments)
}
