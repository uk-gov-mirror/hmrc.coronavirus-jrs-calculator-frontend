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

import base.SpecBaseWithApplication
import models.Calculation.{FurloughCalculationResult, NicCalculationResult, PensionCalculationResult}
import models.NicCategory.Payable
import models.PaymentFrequency.Monthly
import models.PensionStatus.DoesContribute
import models.{Amount, CalculationResult, FullPeriod, FullPeriodBreakdown, FullPeriodWithPaymentDate, FurloughOngoing, PaymentDate, Period}
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

      contentAsString(result) mustEqual view(meta, breakdown, frontendAppConfig.calculatorVersion)(request, messages).toString

      application.stop()
    }
  }

  def periodBreakdownOne(grant: BigDecimal) =
    FullPeriodBreakdown(
      Amount(grant.setScale(2)),
      FullPeriodWithPaymentDate(
        FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
        PaymentDate(LocalDate.of(2020, 3, 20)))
    )
  def periodBreakdownTwo(grant: BigDecimal) =
    FullPeriodBreakdown(
      Amount(grant.setScale(2)),
      FullPeriodWithPaymentDate(
        FullPeriod(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))),
        PaymentDate(LocalDate.of(2020, 4, 20)))
    )
  val furlough =
    CalculationResult(FurloughCalculationResult, 3200.00, List(periodBreakdownOne(1600.00), periodBreakdownTwo(1600.00)))
  val nic = CalculationResult(NicCalculationResult, 241.36, List(periodBreakdownOne(121.58), periodBreakdownTwo(119.78)))
  val pension =
    CalculationResult(PensionCalculationResult, 65.04, List(periodBreakdownOne(32.64), periodBreakdownTwo(32.40)))
  val furloughPeriod = FurloughOngoing(LocalDate.of(2020, 3, 1))

  val breakdown = ConfirmationViewBreakdown(furlough, nic, pension)

  val meta =
    ConfirmationMetadata(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 4, 30)), furloughPeriod, Monthly, Payable, DoesContribute)
}
