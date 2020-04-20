/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import models.{CalculationResult, UserAnswers}
import services.FurloughCalculator

trait FurloughCalculationControllerRequestHandler extends FurloughCalculator with DataExtractor {

  def handleCalculationFurlough(userAnswers: UserAnswers): Option[CalculationResult] =
    for {
      data           <- extract(userAnswers)
      furloughPeriod <- extractFurloughPeriod(data, userAnswers)
      regulars       <- extractPayments(userAnswers, furloughPeriod)
    } yield calculateFurloughGrant(data.paymentFrequency, regulars)

}
