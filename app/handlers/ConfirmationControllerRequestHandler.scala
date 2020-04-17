/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import models.Calculation.{NicCalculationResult, PensionCalculationResult}
import models.NicCategory.{Nonpayable, Payable}
import models.PensionStatus.{OptedIn, OptedOut}
import models.{CalculationResult, NicCategory, PaymentFrequency, PensionStatus, Period, UserAnswers}
import pages._
import services._
import viewmodels.{ConfirmationDataResult, ConfirmationMetadata, ConfirmationViewBreakdown}

trait ConfirmationControllerRequestHandler extends FurloughCalculator with PayPeriodGenerator with NicPensionCalculator with DataExtractor {

  def loadResultData(userAnswers: UserAnswers): Option[ConfirmationDataResult] =
    for {
      breakdown <- breakdown(userAnswers)
      metadata  <- meta(userAnswers)
    } yield ConfirmationDataResult(metadata, breakdown)

  private def breakdown(userAnswers: UserAnswers): Option[ConfirmationViewBreakdown] =
    for {
      furlough <- handleCalculationFurlough(userAnswers)
      ni       <- handleCalculationNi(extract(userAnswers), furlough)
      pension  <- handleCalculationPension(extract(userAnswers), furlough)
    } yield ConfirmationViewBreakdown(furlough, ni, pension)

  private def meta(userAnswers: UserAnswers): Option[ConfirmationMetadata] =
    for {
      data           <- extract(userAnswers)
      furloughPeriod <- extractFurloughPeriod(userAnswers)
    } yield
      ConfirmationMetadata(
        Period(data.claimPeriod.start, data.claimPeriod.end),
        furloughPeriod,
        data.paymentFrequency,
        data.nicCategory,
        data.pensionStatus)

  private def handleCalculationFurlough(userAnswers: UserAnswers): Option[CalculationResult] =
    for {
      data           <- extract(userAnswers)
      taxPayYear     <- userAnswers.get(TaxYearPayDatePage)
      furloughPeriod <- extractFurloughPeriod(userAnswers)
      regulars       <- extractRegularPayments(userAnswers)
    } yield calculateFurlough(data.paymentFrequency, regulars, furloughPeriod, taxPayYear)

  private def handleCalculationNi(data: Option[MandatoryData], furloughResult: CalculationResult): Option[CalculationResult] =
    for {
      nic       <- data.map(_.nicCategory)
      frequency <- data.map(_.paymentFrequency)
    } yield calculateNi(furloughResult, nic, frequency)

  private def calculateNi(furloughResult: CalculationResult, nic: NicCategory, frequency: PaymentFrequency): CalculationResult =
    nic match {
      case Payable    => calculateGrant(frequency, furloughResult.payPeriodBreakdowns.toList, NiRate())
      case Nonpayable => CalculationResult(NicCalculationResult, 0.0, furloughResult.payPeriodBreakdowns.map(_.copy(amount = 0.0)))
    }

  private def handleCalculationPension(data: Option[MandatoryData], furloughResult: CalculationResult): Option[CalculationResult] =
    for {
      pension   <- data.map(_.pensionStatus)
      frequency <- data.map(_.paymentFrequency)
    } yield calculatePension(furloughResult, pension, frequency)

  private def calculatePension(
    furloughResult: CalculationResult,
    pensionStatus: PensionStatus,
    frequency: PaymentFrequency): CalculationResult =
    pensionStatus match {
      case OptedIn  => calculateGrant(frequency, furloughResult.payPeriodBreakdowns.toList, PensionRate())
      case OptedOut => CalculationResult(PensionCalculationResult, 0.0, furloughResult.payPeriodBreakdowns.map(_.copy(amount = 0.0)))
    }
}
