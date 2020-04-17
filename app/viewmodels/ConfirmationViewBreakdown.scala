/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package viewmodels

import models.{CalculationResult, NicCategory, PaymentFrequency, PensionStatus, Period}

case class ConfirmationDataResult(confirmationMetadata: ConfirmationMetadata, confirmationViewBreakdown: ConfirmationViewBreakdown)

case class ConfirmationViewBreakdown(furlough: CalculationResult, nic: CalculationResult, pension: CalculationResult)

case class ConfirmationMetadata(
  claimPeriod: Period,
  furloughPeriod: Period,
  frequency: PaymentFrequency,
  nic: NicCategory,
  pension: PensionStatus)
