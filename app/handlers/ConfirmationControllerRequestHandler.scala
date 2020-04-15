/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import models.Calculation.{NicCalculationResult, PensionCalculationResult}
import models.NicCategory.{Nonpayable, Payable}
import models.{CalculationResult, RegularPayment, UserAnswers}
import pages._
import services._
import viewmodels.ConfirmationViewBreakdown

trait ConfirmationControllerRequestHandler extends FurloughCalculator with PayPeriodGenerator with NicPensionCalculator {

  def breakdown(userAnswers: UserAnswers): Option[ConfirmationViewBreakdown] =
    for {
      furlough <- handleCalculation(userAnswers)
      ni       <- handleCalculationNi(userAnswers, furlough)
      pension  <- handleCalculationPension(userAnswers, furlough)
    } yield ConfirmationViewBreakdown(furlough, ni, pension)

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
        Option(CalculationResult(NicCalculationResult, 0.0, furloughResult.payPeriodBreakdowns.map(_.copy(amount = 0.0)))) // TODO cleanup
    }

  protected def handleCalculationPension(userAnswers: UserAnswers, furloughResult: CalculationResult): Option[CalculationResult] =
    userAnswers.get(PensionAutoEnrolmentPage) match {
      case Some(false) => calculatePension(userAnswers, furloughResult)
      case Some(true) =>
        Option(CalculationResult(PensionCalculationResult, 0.0, furloughResult.payPeriodBreakdowns.map(_.copy(amount = 0.0)))) // TODO cleanup
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
