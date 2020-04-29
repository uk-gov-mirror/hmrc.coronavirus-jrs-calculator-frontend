/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import models.Calculation.{NicCalculationResult, PensionCalculationResult}
import models.NicCategory.{Nonpayable, Payable}
import models.PensionContribution.{No, Yes}
import models.{Amount, CalculationResult, FullPeriodBreakdown, MandatoryData, NicCategory, PartialPeriodBreakdown, PaymentFrequency, PensionContribution, Period, UserAnswers}
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
      furloughPeriod <- extractRelevantFurloughPeriod(data, userAnswers)
      regulars       <- extractPayments(userAnswers, furloughPeriod)
      furlough = calculateFurloughGrant(data.paymentFrequency, regulars)
      ni = calculateNi(furlough, data.nicCategory, data.paymentFrequency)
      pension = calculatePension(furlough, data.pensionContribution, data.paymentFrequency)
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
        data.pensionContribution)

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
    pensionContribution: PensionContribution,
    frequency: PaymentFrequency): CalculationResult =
    pensionContribution match {
      case Yes => calculatePensionGrant(frequency, furloughResult.payPeriodBreakdowns)
      case No =>
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
