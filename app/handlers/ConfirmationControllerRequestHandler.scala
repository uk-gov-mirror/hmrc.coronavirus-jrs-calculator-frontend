/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import models.NicCategory.Payable
import models.{CalculationResult, RegularPayment, UserAnswers}
import pages.{NicCategoryPage, PayDatePage, PaymentFrequencyPage, PensionAutoEnrolmentPage, SalaryQuestionPage, TaxYearPayDatePage}
import services.{FurloughCalculator, NiRate, NicPensionCalculator, PayPeriodGenerator, PensionRate}
import viewmodels.ConfirmationViewBreakdown

trait ConfirmationControllerRequestHandler extends FurloughCalculator with PayPeriodGenerator with NicPensionCalculator {

  def breakdown(userAnswers: UserAnswers) =
    for {
      frequency     <- userAnswers.get(PaymentFrequencyPage)
      salary        <- userAnswers.get(SalaryQuestionPage)
      nicCategory   <- userAnswers.get(NicCategoryPage)
      pensionOptOut <- userAnswers.get(PensionAutoEnrolmentPage)
      taxYearPayDay <- userAnswers.get(TaxYearPayDatePage)
      endDates = userAnswers.getList(PayDatePage)
      payPeriods = generatePayPeriods(endDates)
      regularPayments = payPeriods.map(p => RegularPayment(salary, p))
      furlough = calculateFurlough(frequency, regularPayments, taxYearPayDay)
    } yield {
      val nic = nicCategory match {
        case Payable => calculateGrant(frequency, furlough.payPeriodBreakdowns, NiRate())
        case _       => CalculationResult(0.00, furlough.payPeriodBreakdowns.map(p => p.copy(amount = 0.00)))
      }

      val pension = pensionOptOut match {
        case false => calculateGrant(frequency, furlough.payPeriodBreakdowns, PensionRate())
        case _     => CalculationResult(0.00, furlough.payPeriodBreakdowns.map(p => p.copy(amount = 0)))
      }

      ConfirmationViewBreakdown(furlough, nic, pension)
    }

}
