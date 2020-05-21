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

package models

import services.Threshold
import viewmodels.DetailedFurloughBreakdown

sealed trait PeriodBreakdown {
  val grant: Amount
  val paymentWithPeriod: PaymentWithPeriod
}

sealed trait FullPeriodBreakdown extends PeriodBreakdown {
  val paymentWithPeriod: PaymentWithFullPeriod
}

sealed trait PartialPeriodBreakdown extends PeriodBreakdown {
  val paymentWithPeriod: PaymentWithPartialPeriod
}

sealed trait FurloughBreakdown extends PeriodBreakdown {
  val furloughCap: FurloughCap
}

sealed trait NicBreakdown extends PeriodBreakdown {
  def topUpPay: Amount
  def additionalPay: Amount
  def threshold: Threshold
  def nicCap: NicCap
}

sealed trait PensionBreakdown extends PeriodBreakdown {
  def threshold: Threshold
  def allowance: Amount
}

final case class FullPeriodFurloughBreakdown(grant: Amount, paymentWithPeriod: PaymentWithFullPeriod, furloughCap: FurloughCap)
    extends FullPeriodBreakdown with FurloughBreakdown

final case class PartialPeriodFurloughBreakdown(grant: Amount, paymentWithPeriod: PaymentWithPartialPeriod, furloughCap: FurloughCap)
    extends PartialPeriodBreakdown with FurloughBreakdown

final case class FullPeriodNicBreakdown(
  grant: Amount,
  topUpPay: Amount,
  additionalPay: Amount,
  paymentWithPeriod: PaymentWithFullPeriod,
  threshold: Threshold,
  nicCap: NicCap)
    extends FullPeriodBreakdown with NicBreakdown

final case class PartialPeriodNicBreakdown(
  grant: Amount,
  topUpPay: Amount,
  additionalPay: Amount,
  paymentWithPeriod: PaymentWithPartialPeriod,
  threshold: Threshold,
  nicCap: NicCap)
    extends PartialPeriodBreakdown with NicBreakdown

final case class FullPeriodPensionBreakdown(
  grant: Amount,
  paymentWithPeriod: PaymentWithFullPeriod,
  threshold: Threshold,
  allowance: Amount)
    extends FullPeriodBreakdown with PensionBreakdown

final case class PartialPeriodPensionBreakdown(
  grant: Amount,
  paymentWithPeriod: PaymentWithPartialPeriod,
  threshold: Threshold,
  allowance: Amount)
    extends PartialPeriodBreakdown with PensionBreakdown

object FurloughBreakdown {
  implicit class DetailedBreakdownTransformer(breakdown: FurloughBreakdown) {
    def toDetailedFurloughBreakdown =
      DetailedFurloughBreakdown(
        breakdown.paymentWithPeriod.referencePay,
        breakdown.furloughCap,
        breakdown.grant,
        breakdown.paymentWithPeriod
      )
  }
}
