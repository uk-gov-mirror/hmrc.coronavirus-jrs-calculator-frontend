/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package viewmodels

import models.{CalculationResult, ClaimPeriodModel, FurloughQuestion, NicCategory, PaymentFrequency, PensionStatus}

case class ConfirmationDataResult(confirmationMetadata: ConfirmationMetadata, confirmationViewBreakdown: ConfirmationViewBreakdown)

case class ConfirmationViewBreakdown(furlough: CalculationResult, nic: CalculationResult, pension: CalculationResult)

case class ConfirmationMetadata(
  claimPeriod: ClaimPeriodModel,
  furloughQuestion: FurloughQuestion,
  frequency: PaymentFrequency,
  nic: NicCategory,
  pension: PensionStatus)
