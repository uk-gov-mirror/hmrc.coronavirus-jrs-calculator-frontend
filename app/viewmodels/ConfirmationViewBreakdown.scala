/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package viewmodels

import models.CalculationResult

case class ConfirmationViewBreakdown(furlough: CalculationResult, nic: CalculationResult, pension: CalculationResult)
