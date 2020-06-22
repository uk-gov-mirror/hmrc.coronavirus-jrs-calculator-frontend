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

import models._
import services.{AuditBreakdown, AuditCalculationResult, AuditPeriodBreakdown}

sealed trait ConfirmationDataResult

case class PhaseOneConfirmationDataResult(metaData: ConfirmationMetadata, confirmationViewBreakdown: ConfirmationViewBreakdown)
    extends ConfirmationDataResult

case class PhaseTwoConfirmationDataResult(metaData: ConfirmationMetadata, confirmationViewBreakdown: PhaseTwoConfirmationViewBreakdown)
    extends ConfirmationDataResult

case class ConfirmationDataResultWithoutNicAndPension(
  metaData: ConfirmationMetadataWithoutNicAndPension,
  confirmationViewBreakdown: ConfirmationViewBreakdownWithoutNicAndPension)
    extends ConfirmationDataResult

sealed trait ViewBreakdown {
  def toAuditBreakdown: AuditBreakdown
}

case class ConfirmationViewBreakdown(furlough: FurloughCalculationResult, nic: NicCalculationResult, pension: PensionCalculationResult)
    extends ViewBreakdown {
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

  def detailedBreakdownMessageKeys: Seq[String] =
    furlough.periodBreakdowns.headOption
      .map {
        _.paymentWithPeriod match {
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
      .getOrElse(Seq())

  override def toAuditBreakdown: AuditBreakdown = {
    val auditFurlough = AuditCalculationResult(
      furlough.total,
      furlough.periodBreakdowns
        .map(ppb => AuditPeriodBreakdown(ppb.grant.value, ppb.paymentWithPeriod.periodWithPaymentDate.period.period.end))
    )

    val auditNic = AuditCalculationResult(
      nic.total,
      nic.periodBreakdowns
        .map(ppb => AuditPeriodBreakdown(ppb.grant.value, ppb.paymentWithPeriod.periodWithPaymentDate.period.period.end))
    )

    val auditPension = AuditCalculationResult(
      pension.total,
      pension.periodBreakdowns
        .map(ppb => AuditPeriodBreakdown(ppb.grant.value, ppb.paymentWithPeriod.periodWithPaymentDate.period.period.end))
    )

    AuditBreakdown(auditFurlough, Some(auditNic), Some(auditPension))
  }
}

case class PhaseTwoConfirmationViewBreakdown(
  furlough: PhaseTwoFurloughCalculationResult,
  nic: PhaseTwoNicCalculationResult,
  pension: PhaseTwoPensionCalculationResult)
    extends ViewBreakdown {
  def zippedBreakdowns: Seq[(PhaseTwoFurloughBreakdown, PhaseTwoNicBreakdown, PhaseTwoPensionBreakdown)] =
    (furlough.periodBreakdowns, nic.periodBreakdowns, pension.periodBreakdowns).zipped.toList

  def detailedBreakdowns: Seq[PhaseTwoDetailedBreakdown] = zippedBreakdowns map { breakdowns =>
    PhaseTwoDetailedBreakdown(
      breakdowns._1.paymentWithPeriod.phaseTwoPeriod.periodWithPaymentDate.period,
      breakdowns._1,
      breakdowns._2,
      breakdowns._3
    )
  }

  def detailedBreakdownMessageKeys: Seq[String] =
    furlough.periodBreakdowns.headOption
      .map {
        _.paymentWithPeriod match {
          case _: RegularPaymentWithPhaseTwoPeriod =>
            Seq(
              "phaseTwoDetailedBreakdown.p1.regular"
            )
          case _: AveragePaymentWithPhaseTwoPeriod =>
            Seq(
              "phaseTwoDetailedBreakdown.p1.average"
            )
          case _: CylbPaymentWithPhaseTwoPeriod =>
            Seq(
              "phaseTwoDetailedBreakdown.p1.cylb.1",
              "phaseTwoDetailedBreakdown.p1.cylb.2",
              "phaseTwoDetailedBreakdown.p1.cylb.3"
            )
        }
      }
      .getOrElse(Seq())

  override def toAuditBreakdown: AuditBreakdown = {
    val auditFurlough = AuditCalculationResult(
      furlough.total,
      furlough.periodBreakdowns
        .map(ppb => AuditPeriodBreakdown(ppb.grant.value, ppb.paymentWithPeriod.phaseTwoPeriod.periodWithPaymentDate.period.period.end))
    )

    val auditNic = AuditCalculationResult(
      nic.total,
      nic.periodBreakdowns
        .map(ppb => AuditPeriodBreakdown(ppb.grant.value, ppb.paymentWithPeriod.phaseTwoPeriod.periodWithPaymentDate.period.period.end))
    )

    val auditPension = AuditCalculationResult(
      pension.total,
      pension.periodBreakdowns
        .map(ppb => AuditPeriodBreakdown(ppb.grant.value, ppb.paymentWithPeriod.phaseTwoPeriod.periodWithPaymentDate.period.period.end))
    )

    AuditBreakdown(auditFurlough, Some(auditNic), Some(auditPension))
  }
}

case class ConfirmationViewBreakdownWithoutNicAndPension(furlough: PhaseTwoFurloughCalculationResult) extends ViewBreakdown {

  val auditFurlough = AuditCalculationResult(
    furlough.total,
    furlough.periodBreakdowns
      .map(ppb => AuditPeriodBreakdown(ppb.grant.value, ppb.paymentWithPeriod.phaseTwoPeriod.periodWithPaymentDate.period.period.end))
  )

  override def toAuditBreakdown: AuditBreakdown = AuditBreakdown(auditFurlough, None, None)

  def detailedBreakdowns: Seq[NoNicAndPensionDetailedBreakdown] = furlough.periodBreakdowns map { breakdowns =>
    import breakdowns._
    NoNicAndPensionDetailedBreakdown(
      paymentWithPeriod.phaseTwoPeriod.periodWithPaymentDate.period,
      PhaseTwoFurloughBreakdown(grant, paymentWithPeriod, furloughCap)
    )
  }

  def detailedBreakdownMessageKeys: Seq[String] =
    furlough.periodBreakdowns.headOption
      .map {
        _.paymentWithPeriod match {
          case _: RegularPaymentWithPhaseTwoPeriod =>
            Seq(
              "phaseTwoDetailedBreakdown.p1.regular"
            )
          case _: AveragePaymentWithPhaseTwoPeriod =>
            Seq(
              "phaseTwoDetailedBreakdown.p1.average"
            )
          case _: CylbPaymentWithPhaseTwoPeriod =>
            Seq(
              "phaseTwoDetailedBreakdown.p1.cylb.1",
              "phaseTwoDetailedBreakdown.p1.cylb.2",
              "phaseTwoDetailedBreakdown.p1.cylb.3"
            )
        }
      }
      .getOrElse(Seq())
}

sealed trait Metadata

final case class ConfirmationMetadataWithoutNicAndPension(claimPeriod: Period, furloughDates: FurloughDates, frequency: PaymentFrequency)
    extends Metadata

final case class ConfirmationMetadata(
  claimPeriod: Period,
  furloughDates: FurloughDates,
  frequency: PaymentFrequency,
  nic: NicCategory,
  pension: PensionStatus)
    extends Metadata
