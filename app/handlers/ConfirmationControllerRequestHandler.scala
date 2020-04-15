/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import models.Calculation.{NicCalculationResult, PensionCalculationResult}
import models.NicCategory.{Nonpayable, Payable}
import models.PensionStatus.{OptedIn, OptedOut}
import models.{CalculationResult, ClaimPeriodModel, FurloughPeriod, RegularPayment, UserAnswers}
import pages._
import services._
import viewmodels.{ConfirmationMetadata, ConfirmationViewBreakdown}

trait ConfirmationControllerRequestHandler extends FurloughCalculator with PayPeriodGenerator with NicPensionCalculator {

  def breakdown(userAnswers: UserAnswers): Option[ConfirmationViewBreakdown] =
    for {
      furlough <- handleCalculation(userAnswers)
      ni       <- handleCalculationNi(userAnswers, furlough)
      pension  <- handleCalculationPension(userAnswers, furlough)
    } yield ConfirmationViewBreakdown(furlough, ni, pension)

  def meta(userAnswers: UserAnswers): Option[ConfirmationMetadata] =
    for {
      claimStart <- userAnswers.get(ClaimPeriodStartPage)
      claimEnd   <- userAnswers.get(ClaimPeriodEndPage)
      furlough   <- userAnswers.get(FurloughQuestionPage)
      frequency  <- userAnswers.get(PaymentFrequencyPage)
      nic        <- userAnswers.get(NicCategoryPage)
      pension    <- userAnswers.get(PensionAutoEnrolmentPage)
      claimPeriod = ClaimPeriodModel(claimStart, claimEnd)
    } yield ConfirmationMetadata(claimPeriod, furlough, frequency, nic, pension) //TODO gather actual FurloughPeriod

  def handleCalculation(userAnswers: UserAnswers): Option[CalculationResult] =
    for {
      frequency  <- userAnswers.get(PaymentFrequencyPage)
      taxPayYear <- userAnswers.get(TaxYearPayDatePage)
      payDate = userAnswers.getList(PayDatePage)
      periods = generatePayPeriods(payDate.toList)
      salary = userAnswers.get(SalaryQuestionPage)
      regulars = periods.map(p => RegularPayment(salary.get, p))
    } yield calculateFurlough(frequency, regulars, taxPayYear)

  protected def handleCalculationNi(userAnswers: UserAnswers, furloughResult: CalculationResult): Option[CalculationResult] =
    userAnswers.get(NicCategoryPage) match {
      case Some(Payable) => calculateNi(userAnswers, furloughResult)
      case Some(Nonpayable) =>
        Option(CalculationResult(NicCalculationResult, 0.0, furloughResult.payPeriodBreakdowns.map(_.copy(amount = 0.0))))
    }

  protected def handleCalculationPension(userAnswers: UserAnswers, furloughResult: CalculationResult): Option[CalculationResult] =
    userAnswers.get(PensionAutoEnrolmentPage) match {
      case Some(OptedIn) => calculatePension(userAnswers, furloughResult)
      case Some(OptedOut) =>
        Option(CalculationResult(PensionCalculationResult, 0.0, furloughResult.payPeriodBreakdowns.map(_.copy(amount = 0.0))))
    }

  private def calculateNi(userAnswers: UserAnswers, furloughResult: CalculationResult): Option[CalculationResult] =
    for {
      frequency <- userAnswers.get(PaymentFrequencyPage)
    } yield calculateGrant(frequency, furloughResult.payPeriodBreakdowns.toList, NiRate())

  private def calculatePension(userAnswers: UserAnswers, furloughResult: CalculationResult): Option[CalculationResult] =
    for {
      frequency <- userAnswers.get(PaymentFrequencyPage)
    } yield calculateGrant(frequency, furloughResult.payPeriodBreakdowns.toList, PensionRate())
}
