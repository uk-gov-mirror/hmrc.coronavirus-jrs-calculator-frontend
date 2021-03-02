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

package controllers

import assets.constants.ConfirmationConstants._
import base.{CoreTestDataBuilder, SpecBaseControllerSpecs}
import config.CalculatorVersionConfiguration
import models.NicCategory.Payable
import models.PaymentFrequency.Monthly
import models.PensionStatus.DoesContribute
import models._
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{AuditService, Threshold}
import viewmodels.{ConfirmationViewBreakdownWithoutNicAndPension, PhaseTwoConfirmationViewBreakdown}
import views.html._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConfirmationControllerSpec extends SpecBaseControllerSpecs with CoreTestDataBuilder {

  val view = app.injector.instanceOf[ConfirmationViewWithDetailedBreakdowns]
  val noNicView = app.injector.instanceOf[NoNicAndPensionConfirmationView]
  val phaseTwoView = app.injector.instanceOf[PhaseTwoConfirmationView]
  val septView = app.injector.instanceOf[SeptemberConfirmationView]
  val octView = app.injector.instanceOf[OctoberConfirmationView]
  val extensionView = app.injector.instanceOf[JrsExtensionConfirmationView]
  val audit = app.injector.instanceOf[AuditService]

  val controller = new ConfirmationController(
    messagesApi = messagesApi,
    identify = identifier,
    getData = dataRetrieval,
    requireData = dataRequired,
    controllerComponents = component,
    viewWithDetailedBreakdowns = view,
    phaseTwoView = phaseTwoView,
    noNicAndPensionView = noNicView,
    septemberConfirmationView = septView,
    octoberConfirmationView = octView,
    extensionView = extensionView,
    auditService = audit,
    navigator = navigator
  )

  "Confirmation Controller" must {

    "return OK and the confirmation view with detailed breakdowns for a GET" in new CalculatorVersionConfiguration {

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(dummyUserAnswers))

      val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)
      val result = controller.onPageLoad()(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(breakdown, meta.claimPeriod, calculatorVersionConf)(request, messages).toString
    }

    "return OK and the phase two confirmation view with detailed breakdowns for a GET for dates 1st to 31st July 2020" in new CalculatorVersionConfiguration {

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(phaseTwoJourney()))

      val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)
      val result: Future[Result] = controller.onPageLoad()(request)
      val payment: RegularPaymentWithPhaseTwoPeriod = {
        RegularPaymentWithPhaseTwoPeriod(
          regularPay = Amount(2000.00),
          referencePay = Amount(2000.0),
          phaseTwoPeriod = PhaseTwoPeriod(fullPeriodWithPaymentDate("2020, 7, 1", "2020, 7, 31", "2020, 7, 31"), None, None)
        )
      }

      val breakdown = PhaseTwoConfirmationViewBreakdown(
        PhaseTwoFurloughCalculationResult(
          1600.00,
          Seq(PhaseTwoFurloughBreakdown(Amount(1600.0), payment, FullPeriodCap(2500.00)))
        ),
        PhaseTwoNicCalculationResult(
          119.78,
          Seq(PhaseTwoNicBreakdown(Amount(119.78), payment, Threshold(732.0, TaxYearEnding2021, Monthly), Payable))
        ),
        PhaseTwoPensionCalculationResult(
          32.40,
          Seq(PhaseTwoPensionBreakdown(Amount(32.40), payment, Threshold(520.0, TaxYearEnding2021, Monthly), DoesContribute))
        )
      )

      status(result) mustEqual OK
      contentAsString(result) mustEqual phaseTwoView(breakdown, period("2020, 7, 1", "2020, 7, 31"), calculatorVersionConf)(
        request,
        messages).toString
    }

    "return OK and the JRSExtension view with calculations, for a GET for dates 1st to 31st March 2021" in new CalculatorVersionConfiguration {

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(march2021Journey()))

      val employeeIncomeForPeriod: Amount = Amount(10000.00)
      val maxMonthFurloughGrant: BigDecimal = 2500.00
      val claimStartDate = "2021, 3, 1"
      val claimEndDate = "2021, 3, 31"

      val payment: RegularPaymentWithPhaseTwoPeriod = {
        RegularPaymentWithPhaseTwoPeriod(
          regularPay = employeeIncomeForPeriod,
          referencePay = employeeIncomeForPeriod,
          phaseTwoPeriod = PhaseTwoPeriod(
            periodWithPaymentDate = fullPeriodWithPaymentDate(start = claimStartDate, end = claimEndDate, paymentDate = claimEndDate),
            actualHours = None,
            usualHours = None
          )
        )
      }

      val breakdown: ConfirmationViewBreakdownWithoutNicAndPension = {
        ConfirmationViewBreakdownWithoutNicAndPension(
          furlough = PhaseTwoFurloughCalculationResult(
            total = maxMonthFurloughGrant,
            periodBreakdowns = Seq(
              PhaseTwoFurloughBreakdown(
                grant = Amount(maxMonthFurloughGrant),
                paymentWithPeriod = payment,
                furloughCap = FullPeriodCap(maxMonthFurloughGrant))
            )
          )
        )
      }

      val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)
      val result: Future[Result] = controller.onPageLoad()(request)

      val expected: String = contentAsString(result)
      val actual: String = extensionView(
        cvb = breakdown,
        claimPeriod = period(start = claimStartDate, end = claimEndDate),
        version = calculatorVersionConf,
        extensionHasMultipleFurloughs = false
      )(request, messages).toString

      status(result) mustEqual OK
      expected mustEqual actual

    }
  }
}
