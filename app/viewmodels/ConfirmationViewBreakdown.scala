/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package viewmodels

import models.{CalculationResult, FurloughDates, NicCategory, PaymentFrequency, PensionStatus, Period}

case class ConfirmationDataResult(confirmationMetadata: ConfirmationMetadata, confirmationViewBreakdown: ConfirmationViewBreakdown)

case class ConfirmationViewBreakdown(furlough: CalculationResult, nic: CalculationResult, pension: CalculationResult)

case class ConfirmationMetadata(
  claimPeriod: Period,
  furloughDates: FurloughDates,
  frequency: PaymentFrequency,
  nic: NicCategory,
  pension: PensionStatus)
