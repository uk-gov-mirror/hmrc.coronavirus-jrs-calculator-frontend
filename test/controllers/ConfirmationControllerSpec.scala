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

import base.{CoreTestDataBuilder, SpecBaseWithApplication}
import models.NicCategory.Payable
import models.PaymentFrequency.Monthly
import models.PensionStatus.DoesContribute
import models.{FullPeriodCap, FurloughCalculationResult, FurloughOngoing, NicCalculationResult, PensionCalculationResult, Period}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.{ConfirmationMetadata, ConfirmationViewBreakdown}
import views.html.ConfirmationView
import views.html.ConfirmationViewWithDetailedBreakdowns

class ConfirmationControllerSpec extends SpecBaseWithApplication with CoreTestDataBuilder {

  "Confirmation Controller" must {

    "return OK and the confirmation view without detailed breakdowns for a GET" in {
      val application =
        applicationBuilder(config = Map("confirmationWithDetailedBreakdowns.enabled" -> "false"), userAnswers = Some(dummyUserAnswers))
          .build()

      val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ConfirmationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(meta, breakdown, frontendAppConfig.calculatorVersion)(request, messages).toString

      application.stop()
    }

    "return OK and the confirmation view with detailed breakdowns for a GET" in {
      val application =
        applicationBuilder(config = Map("confirmationWithDetailedBreakdowns.enabled" -> "true"), userAnswers = Some(dummyUserAnswers))
          .build()

      val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ConfirmationViewWithDetailedBreakdowns]

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(breakdown, meta.claimPeriod, frontendAppConfig.calculatorVersion)(request, messages).toString

      application.stop()
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
        regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-20"))),
      fullPeriodNicBreakdown(
        119.78,
        0.0,
        0.0,
        regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-20")))
    )
  )

  lazy val pension = PensionCalculationResult(
    65.04,
    Seq(
      fullPeriodPensionBreakdown(
        32.64,
        regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-20"))),
      fullPeriodPensionBreakdown(
        32.40,
        regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-20")))
    )
  )

  lazy val breakdown = ConfirmationViewBreakdown(furlough, nic, pension)

  val furloughPeriod = FurloughOngoing(LocalDate.of(2020, 3, 1))

  val meta =
    ConfirmationMetadata(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 4, 30)), furloughPeriod, Monthly, Payable, DoesContribute)

}
