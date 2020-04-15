/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package viewmodels

import models.{CalculationResult, ClaimPeriodModel, FurloughPeriod, FurloughQuestion, NicCategory, PaymentFrequency, PensionStatus}

case class ConfirmationViewBreakdown(furlough: CalculationResult, nic: CalculationResult, pension: CalculationResult)

case class ConfirmationMetadata(
  claimPeriod: ClaimPeriodModel,
  furloughQuestion: FurloughQuestion,
  frequency: PaymentFrequency,
  nic: NicCategory,
  pension: PensionStatus)
