/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import models.Calculation.{NicCalculationResult, PensionCalculationResult}
import models.NicCategory.{Nonpayable, Payable}
import models.PensionStatus.{DoesContribute, DoesNotContribute}
import models.{Amount, CalculationResult, FullPeriodBreakdown, MandatoryData, NicCategory, PartialPeriodBreakdown, PaymentFrequency, PensionStatus, Period, UserAnswers}
import services._
import viewmodels.{ConfirmationDataResult, ConfirmationMetadata, ConfirmationViewBreakdown}

trait ConfirmationControllerRequestHandler
    extends FurloughCalculator with PeriodHelper with NicCalculator with PensionCalculator with DataExtractor {

  def loadResultData(userAnswers: UserAnswers): Option[ConfirmationDataResult] =
    for {
      data      <- extract(userAnswers)
      breakdown <- breakdown(userAnswers, data)
      metadata  <- meta(userAnswers, data)
    } yield ConfirmationDataResult(metadata, breakdown)

  private def breakdown(userAnswers: UserAnswers, data: MandatoryData): Option[ConfirmationViewBreakdown] =
    for {
      regulars <- extractPayments(userAnswers, extractRelevantFurloughPeriod(data, userAnswers))
      furlough = calculateFurloughGrant(data.paymentFrequency, regulars)
      ni = calculateNi(furlough, data.nicCategory, data.paymentFrequency)
      pension = calculatePension(furlough, data.payStatus, data.paymentFrequency)
    } yield ConfirmationViewBreakdown(furlough, ni, pension)

  private def meta(userAnswers: UserAnswers, data: MandatoryData): Option[ConfirmationMetadata] =
    for {
      furloughPeriod <- extractFurloughPeriod(userAnswers)
    } yield
      ConfirmationMetadata(
        Period(data.claimPeriod.start, data.claimPeriod.end),
        furloughPeriod,
        data.paymentFrequency,
        data.nicCategory,
        data.payStatus)

  private def calculateNi(furloughResult: CalculationResult, nic: NicCategory, frequency: PaymentFrequency): CalculationResult =
    nic match {
      case Payable => calculateNicGrant(frequency, furloughResult.payPeriodBreakdowns)
      case Nonpayable =>
        CalculationResult(
          NicCalculationResult,
          0.0,
          furloughResult.payPeriodBreakdowns.map {
            case FullPeriodBreakdown(_, withPaymentDate)       => FullPeriodBreakdown(Amount(0.0), withPaymentDate)
            case PartialPeriodBreakdown(_, _, withPaymentDate) => PartialPeriodBreakdown(Amount(0.0), Amount(0.0), withPaymentDate)
          }
        )
    }

  private def calculatePension(
    furloughResult: CalculationResult,
    payStatus: PensionStatus,
    frequency: PaymentFrequency): CalculationResult =
    payStatus match {
      case DoesContribute => calculatePensionGrant(frequency, furloughResult.payPeriodBreakdowns)
      case DoesNotContribute =>
        CalculationResult(
          PensionCalculationResult,
          0.0,
          furloughResult.payPeriodBreakdowns.map {
            case FullPeriodBreakdown(_, withPaymentDate)       => FullPeriodBreakdown(Amount(0.0), withPaymentDate)
            case PartialPeriodBreakdown(_, _, withPaymentDate) => PartialPeriodBreakdown(Amount(0.0), Amount(0.0), withPaymentDate)
          }
        )
    }
}
