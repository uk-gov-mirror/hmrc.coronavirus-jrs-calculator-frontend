/*
 * Copyright 2021 HM Revenue & Customs
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

import play.api.i18n.Messages
import services.{FrequencyTaxYearThresholdMapping, NiRate, PensionRate, Threshold}
import viewmodels.DetailedFurloughBreakdown
import services.Calculators._

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
  def nicCategory: NicCategory
}

sealed trait PensionBreakdown extends PeriodBreakdown {
  def threshold: Threshold
  def allowance: Amount
  def pensionStatus: PensionStatus
}

final case class FullPeriodFurloughBreakdown(grant: Amount, paymentWithPeriod: PaymentWithFullPeriod, furloughCap: FurloughCap)
    extends FullPeriodBreakdown with FurloughBreakdown

final case class PartialPeriodFurloughBreakdown(grant: Amount, paymentWithPeriod: PaymentWithPartialPeriod, furloughCap: FurloughCap)
    extends PartialPeriodBreakdown with FurloughBreakdown

final case class FullPeriodNicBreakdown(grant: Amount,
                                        topUpPay: Amount,
                                        additionalPay: Amount,
                                        paymentWithPeriod: PaymentWithFullPeriod,
                                        threshold: Threshold,
                                        nicCap: NicCap,
                                        nicCategory: NicCategory)
    extends FullPeriodBreakdown with NicBreakdown

final case class PartialPeriodNicBreakdown(grant: Amount,
                                           topUpPay: Amount,
                                           additionalPay: Amount,
                                           paymentWithPeriod: PaymentWithPartialPeriod,
                                           threshold: Threshold,
                                           nicCap: NicCap,
                                           nicCategory: NicCategory)
    extends PartialPeriodBreakdown with NicBreakdown

final case class FullPeriodPensionBreakdown(grant: Amount,
                                            paymentWithPeriod: PaymentWithFullPeriod,
                                            threshold: Threshold,
                                            allowance: Amount,
                                            pensionStatus: PensionStatus)
    extends FullPeriodBreakdown with PensionBreakdown

final case class PartialPeriodPensionBreakdown(grant: Amount,
                                               paymentWithPeriod: PaymentWithPartialPeriod,
                                               threshold: Threshold,
                                               allowance: Amount,
                                               pensionStatus: PensionStatus)
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

sealed trait PhaseTwoPeriodBreakdown {
  val grant: Amount
  val paymentWithPeriod: PaymentWithPhaseTwoPeriod
}

final case class PhaseTwoFurloughBreakdown(grant: Amount, paymentWithPeriod: PaymentWithPhaseTwoPeriod, furloughCap: FurloughCap)
    extends PhaseTwoPeriodBreakdown {
  def isCapped: Boolean = (paymentWithPeriod.referencePay.value * 0.8) > furloughCap.value

  def grantAmount(furloughGrantRate: FurloughGrantRate): BigDecimal = furloughGrantRate match {
    case EightyPercent => grant.value
    case rate          => Amount((grant.value / 80) * rate.value).halfUp.value
  }

  def calculatedFurlough(furloughGrantRate: FurloughGrantRate): String =
    Amount(paymentWithPeriod.referencePay.value * furloughGrantRate.asPercentage).halfUp.value.formatted("%.2f")
}

final case class PhaseTwoNicBreakdown(grant: Amount,
                                      paymentWithPeriod: PaymentWithPhaseTwoPeriod,
                                      threshold: Threshold,
                                      nicCategory: NicCategory)
    extends PhaseTwoPeriodBreakdown {
  def isPartial  = paymentWithPeriod.phaseTwoPeriod.periodWithPaymentDate.period.isInstanceOf[PartialPeriod]
  def isPartTime = paymentWithPeriod.phaseTwoPeriod.isPartTime

  def thresholdMessage(implicit messages: Messages): String =
    (isPartial, isPartTime) match {
      case (false, false) => messages("phaseTwoNicBreakdown.l3", threshold.value.formatted("%.2f"))
      case (true, false) =>
        messages(
          "phaseTwoNicBreakdown.l3.partial",
          threshold.value.formatted("%.2f"),
          FrequencyTaxYearThresholdMapping.thresholdFor(threshold.frequency, threshold.taxYear, NiRate()).value.formatted("%.2f"),
          paymentWithPeriod.periodDays,
          paymentWithPeriod.furloughDays
        )
      case (false, true) =>
        messages(
          "phaseTwoNicBreakdown.l3.partTime",
          threshold.value.formatted("%.2f"),
          FrequencyTaxYearThresholdMapping.thresholdFor(threshold.frequency, threshold.taxYear, NiRate()).value.formatted("%.2f"),
          paymentWithPeriod.phaseTwoPeriod.usual.formatted("%.2f"),
          paymentWithPeriod.phaseTwoPeriod.furloughed.formatted("%.2f")
        )
      case (true, true) =>
        messages(
          "phaseTwoNicBreakdown.l3.partial.partTime",
          threshold.value.formatted("%.2f"),
          FrequencyTaxYearThresholdMapping.thresholdFor(threshold.frequency, threshold.taxYear, NiRate()).value.formatted("%.2f"),
          paymentWithPeriod.periodDays,
          paymentWithPeriod.furloughDays,
          paymentWithPeriod.phaseTwoPeriod.usual.formatted("%.2f"),
          paymentWithPeriod.phaseTwoPeriod.furloughed.formatted("%.2f")
        )
    }
}

final case class PhaseTwoPensionBreakdown(grant: Amount,
                                          paymentWithPeriod: PaymentWithPhaseTwoPeriod,
                                          threshold: Threshold,
                                          pensionStatus: PensionStatus)
    extends PhaseTwoPeriodBreakdown {
  def isPartial  = paymentWithPeriod.phaseTwoPeriod.periodWithPaymentDate.period.isInstanceOf[PartialPeriod]
  def isPartTime = paymentWithPeriod.phaseTwoPeriod.isPartTime

  def thresholdMessage(implicit messages: Messages): String =
    (isPartial, isPartTime) match {
      case (false, false) => messages("phaseTwoPensionBreakdown.l3", threshold.value.formatted("%.2f"))
      case (true, false) =>
        messages(
          "phaseTwoPensionBreakdown.l3.partial",
          threshold.value.formatted("%.2f"),
          FrequencyTaxYearThresholdMapping.thresholdFor(threshold.frequency, threshold.taxYear, PensionRate()).value.formatted("%.2f"),
          paymentWithPeriod.periodDays,
          paymentWithPeriod.furloughDays
        )
      case (false, true) =>
        messages(
          "phaseTwoPensionBreakdown.l3.partTime",
          threshold.value.formatted("%.2f"),
          FrequencyTaxYearThresholdMapping.thresholdFor(threshold.frequency, threshold.taxYear, PensionRate()).value.formatted("%.2f"),
          paymentWithPeriod.phaseTwoPeriod.usual.formatted("%.2f"),
          paymentWithPeriod.phaseTwoPeriod.furloughed.formatted("%.2f")
        )
      case (true, true) =>
        messages(
          "phaseTwoPensionBreakdown.l3.partial.partTime",
          threshold.value.formatted("%.2f"),
          FrequencyTaxYearThresholdMapping.thresholdFor(threshold.frequency, threshold.taxYear, PensionRate()).value.formatted("%.2f"),
          paymentWithPeriod.periodDays,
          paymentWithPeriod.furloughDays,
          paymentWithPeriod.phaseTwoPeriod.usual.formatted("%.2f"),
          paymentWithPeriod.phaseTwoPeriod.furloughed.formatted("%.2f")
        )
    }
}
