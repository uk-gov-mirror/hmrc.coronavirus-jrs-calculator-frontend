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

package controllers

import java.time.LocalDate

import base.{CoreTestDataBuilder, SpecBaseControllerSpecs}
import config.CalculatorVersionConfiguration
import models.NicCategory.Payable
import models.PaymentFrequency.Monthly
import models.PensionStatus.DoesContribute
import models._
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{AuditService, Threshold}
import viewmodels.{ConfirmationMetadata, ConfirmationViewBreakdown, PhaseTwoConfirmationViewBreakdown}
import views.html.{ConfirmationViewWithDetailedBreakdowns, NoNicAndPensionConfirmationView, OctoberConfirmationView, PhaseTwoConfirmationView, SeptemberConfirmationView}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConfirmationControllerSpec extends SpecBaseControllerSpecs with CoreTestDataBuilder {

  val view = app.injector.instanceOf[ConfirmationViewWithDetailedBreakdowns]
  val noNicView = app.injector.instanceOf[NoNicAndPensionConfirmationView]
  val phaseTwoView = app.injector.instanceOf[PhaseTwoConfirmationView]
  val septView = app.injector.instanceOf[SeptemberConfirmationView]
  val octView = app.injector.instanceOf[OctoberConfirmationView]
  val audit = app.injector.instanceOf[AuditService]

  val controller = new ConfirmationController(
    messagesApi,
    identifier,
    dataRetrieval,
    dataRequired,
    component,
    view,
    phaseTwoView,
    noNicView,
    septView,
    octView,
    audit,
    navigator)

  "Confirmation Controller" must {

    "return OK and the confirmation view with detailed breakdowns for a GET" in new CalculatorVersionConfiguration {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(dummyUserAnswers))
      val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)

      val result = controller.onPageLoad()(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(breakdown, meta.claimPeriod, calculatorVersionConf)(request, messages).toString
    }

    "return OK and the phase two confirmation view with detailed breakdowns for a GET" in new CalculatorVersionConfiguration {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(phaseTwoJourney()))
      val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)

      val result = controller.onPageLoad()(request)

      val payment = RegularPaymentWithPhaseTwoPeriod(
        Amount(2000.00),
        Amount(2000.0),
        PhaseTwoPeriod(fullPeriodWithPaymentDate("2020, 7, 1", "2020, 7, 31", "2020, 7, 31"), None, None))

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
  }

  lazy val furlough =
    FurloughCalculationResult(
      3200.00,
      Seq(
        fullPeriodFurloughBreakdown(
          1600.00,
          regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-20")),
          FullPeriodCap(2500.00)),
        fullPeriodFurloughBreakdown(
          1600.00,
          regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-20")),
          FullPeriodCap(2500.00))
      )
    )

  lazy val nic = NicCalculationResult(
    241.36,
    Seq(
      fullPeriodNicBreakdown(
        121.58,
        0.0,
        0.0,
        regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-20")),
        Threshold(719.0, TaxYearEnding2020, Monthly),
        NicCap(Amount(1600.0), Amount(121.58), Amount(200.80)),
        Payable
      ),
      fullPeriodNicBreakdown(
        119.78,
        0.0,
        0.0,
        regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-20")),
        Threshold(732.0, TaxYearEnding2021, Monthly),
        NicCap(Amount(1600.00), Amount(119.78), Amount(220.80)),
        Payable
      )
    )
  )

  lazy val pension = PensionCalculationResult(
    65.04,
    Seq(
      fullPeriodPensionBreakdown(
        32.64,
        regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-20")),
        Threshold(512.0, TaxYearEnding2020, Monthly),
        512.0,
        DoesContribute
      ),
      fullPeriodPensionBreakdown(
        32.40,
        regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-20")),
        Threshold(520.0, TaxYearEnding2021, Monthly),
        520.0,
        DoesContribute
      )
    )
  )

  lazy val breakdown = ConfirmationViewBreakdown(furlough, nic, pension)

  val furloughPeriod = FurloughOngoing(LocalDate.of(2020, 3, 1))

  val meta =
    ConfirmationMetadata(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 4, 30)), furloughPeriod, Monthly, Payable, DoesContribute)

}
