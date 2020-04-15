/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import models.{CalculationResult, PayPeriodBreakdown, PaymentFrequency}
import services.{NicPensionCalculator, Rate}

class GrantCalculatorControllerRequestHandler extends NicPensionCalculator {

  def handleCalculation(paymentFrequency: PaymentFrequency, payPeriodBreakdowns: List[PayPeriodBreakdown], rate: Rate): CalculationResult =
    calculateGrant(paymentFrequency, payPeriodBreakdowns, rate)
}
