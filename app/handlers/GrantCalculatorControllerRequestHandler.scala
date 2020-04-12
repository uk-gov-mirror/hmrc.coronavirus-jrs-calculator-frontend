/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import models.{CalculationResult, FurloughPayment, PaymentFrequency}
import services.{CalculatorService, Rate}

class GrantCalculatorControllerRequestHandler extends CalculatorService {

  def handleCalculation(paymentFrequency: PaymentFrequency, furloughPayments: List[FurloughPayment], rate: Rate): CalculationResult =
    calculateResult(paymentFrequency, furloughPayments, rate)
}
