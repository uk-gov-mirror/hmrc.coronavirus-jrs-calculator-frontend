/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import models.{FurloughPayment, NicCalculationResult, PaymentFrequency}
import services.NicCalculatorService

class NICGrantCalculatorControllerRequestHandler extends NicCalculatorService {

  def handleCalculation(
    paymentFrequency: PaymentFrequency,
    furloughPayments: List[FurloughPayment]): NicCalculationResult =
    calculateNics(paymentFrequency, furloughPayments)
}
