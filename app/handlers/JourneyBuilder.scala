/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import models.EmployeeStarted.{After1Feb2019, OnOrBefore1Feb2019}
import models.PayMethod.{Regular, Variable}
import models.{BranchingQuestion, Journey, JourneyData, RegularPay, RegularPayData, UserAnswers, VariablePay, VariablePayData, VariablePayWithCylb}

trait JourneyBuilder extends DataExtractor {

  def define(data: BranchingQuestion): Journey = data match {
    case BranchingQuestion(Regular, _, _)                                                                  => RegularPay
    case BranchingQuestion(Variable, Some(After1Feb2019), Some(d)) if d.isAfter(LocalDate.of(2019, 4, 5))  => VariablePay
    case BranchingQuestion(Variable, Some(After1Feb2019), Some(d)) if d.isBefore(LocalDate.of(2019, 4, 6)) => VariablePayWithCylb
    case BranchingQuestion(Variable, Some(OnOrBefore1Feb2019), _)                                          => VariablePayWithCylb
  }

  def journeyData(journey: Journey, userAnswers: UserAnswers): Option[JourneyData] = journey match {
    case RegularPay  => regularPayData(userAnswers)
    case VariablePay => variablePayData(userAnswers)
  }

  private def variablePayData(userAnswers: UserAnswers): Option[VariablePayData] =
    for {
      coreData <- extractJourneyCoreData(userAnswers)
      grossPay <- extractVariableGrossPay(userAnswers)
      nonFurlough = extractNonFurlough(userAnswers)
      priorFurlough <- extractPriorFurloughPeriod(userAnswers)
    } yield VariablePayData(coreData, grossPay, nonFurlough, priorFurlough)

  private def regularPayData(userAnswers: UserAnswers): Option[RegularPayData] =
    for {
      coreData <- extractJourneyCoreData(userAnswers)
      salary   <- extractSalary(userAnswers)
    } yield RegularPayData(coreData, salary)
}
