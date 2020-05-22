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

import models.{AveragePayment, CylbPayment, FurloughBreakdown, FurloughCalculationResult, FurloughDates, NicBreakdown, NicCalculationResult, NicCategory, PaymentFrequency, PensionBreakdown, PensionCalculationResult, PensionStatus, Period, RegularPayment}

case class ConfirmationDataResult(metaData: ConfirmationMetadata, confirmationViewBreakdown: ConfirmationViewBreakdown)

case class ConfirmationViewBreakdown(furlough: FurloughCalculationResult, nic: NicCalculationResult, pension: PensionCalculationResult) {
  def zippedBreakdowns: Seq[(FurloughBreakdown, NicBreakdown, PensionBreakdown)] =
    (furlough.periodBreakdowns, nic.periodBreakdowns, pension.periodBreakdowns).zipped.toList

  def detailedBreakdowns: Seq[DetailedBreakdown] = zippedBreakdowns map { breakdowns =>
    DetailedBreakdown(
      breakdowns._1.paymentWithPeriod.periodWithPaymentDate.period,
      breakdowns._1.toDetailedFurloughBreakdown,
      breakdowns._2,
      breakdowns._3
    )
  }

  def detailedBreakdownMessageKeys: Seq[String] = furlough.periodBreakdowns.head.paymentWithPeriod match {
    case _: RegularPayment =>
      Seq(
        "detailedBreakdown.p1.regular"
      )
    case _: AveragePayment =>
      Seq(
        "detailedBreakdown.p1.average"
      )
    case _: CylbPayment =>
      Seq(
        "detailedBreakdown.p1.cylb.1",
        "detailedBreakdown.p1.cylb.2",
        "detailedBreakdown.p1.cylb.3"
      )
  }
}

case class ConfirmationMetadata(
  claimPeriod: Period,
  furloughDates: FurloughDates,
  frequency: PaymentFrequency,
  nic: NicCategory,
  pension: PensionStatus)
