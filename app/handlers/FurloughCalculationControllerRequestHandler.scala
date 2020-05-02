/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import models.{CalculationResult, UserAnswers}
import services.{FurloughCalculator, ReferencePayCalculator}

trait FurloughCalculationControllerRequestHandler extends FurloughCalculator with ReferencePayCalculator with JourneyBuilder {

  def handleCalculationFurlough(userAnswers: UserAnswers): Option[CalculationResult] =
    for {
      questions <- extractBranchingQuestions(userAnswers)
      data      <- journeyData(define(questions), userAnswers)
      payments = calculateReferencePay(data)
    } yield calculateFurloughGrant(data.core.frequency, payments)

}
