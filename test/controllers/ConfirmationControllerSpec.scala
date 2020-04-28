/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.LocalDate

import base.SpecBaseWithApplication
import models.Calculation.{FurloughCalculationResult, NicCalculationResult, PensionCalculationResult}
import models.NicCategory.Payable
import models.PaymentFrequency.Monthly
import models.PensionContribution.Yes
import models.{Amount, CalculationResult, FullPeriod, FurloughQuestion, PaymentDate, Period, PeriodBreakdown, PeriodWithPaymentDate}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.{ConfirmationMetadata, ConfirmationViewBreakdown}
import views.html.ConfirmationView

class ConfirmationControllerSpec extends SpecBaseWithApplication {

  "Confirmation Controller" must {

    "return OK and the correct view for a GET" in {
      val application = applicationBuilder(userAnswers = Some(dummyUserAnswers)).build()

      val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ConfirmationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(meta, breakdown, frontendAppConfig.calculatorVersion, FurloughQuestion.No)(request, messages).toString

      application.stop()
    }
  }

  def periodBreakdownOne(grossPay: BigDecimal, grant: BigDecimal) =
    PeriodBreakdown(
      Amount(grossPay.setScale(2)),
      Amount(grant.setScale(2)),
      PeriodWithPaymentDate(FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))), PaymentDate(LocalDate.of(2020, 3, 20)))
    )
  def periodBreakdownTwo(grossPay: BigDecimal, grant: BigDecimal) =
    PeriodBreakdown(
      Amount(grossPay.setScale(2)),
      Amount(grant.setScale(2)),
      PeriodWithPaymentDate(FullPeriod(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))), PaymentDate(LocalDate.of(2020, 4, 20)))
    )
  val furlough =
    CalculationResult(FurloughCalculationResult, 3200.00, List(periodBreakdownOne(2000.00, 1600.00), periodBreakdownTwo(2000.00, 1600.00)))
  val nic = CalculationResult(NicCalculationResult, 241.36, List(periodBreakdownOne(2000.00, 121.58), periodBreakdownTwo(2000.00, 119.78)))
  val pension =
    CalculationResult(PensionCalculationResult, 65.04, List(periodBreakdownOne(2000.00, 32.64), periodBreakdownTwo(2000.00, 32.40)))
  val furloughPeriod = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 4, 30))

  val breakdown = ConfirmationViewBreakdown(furlough, nic, pension)

  val meta =
    ConfirmationMetadata(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 4, 30)), furloughPeriod, Monthly, Payable, Yes)
}
