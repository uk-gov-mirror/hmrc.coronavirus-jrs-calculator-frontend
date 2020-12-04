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
import java.util.UUID

import assets.BaseITConstants
import models.NicCategory.Payable
import models.PaymentFrequency.Monthly
import models.PensionStatus.DoesContribute
import models._
import play.api.libs.json.Json
import play.api.test.Helpers._
import services.{AuditService, Threshold}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.logging.SessionId
import utils.{CreateRequestHelper, CustomMatchers, ITCoreTestData, IntegrationSpecBase}
import viewmodels.{ConfirmationMetadata, ConfirmationViewBreakdown}
import views.html._

class ConfirmationControllerISpec extends IntegrationSpecBase with CreateRequestHelper with CustomMatchers
  with BaseITConstants with ITCoreTestData {

  "GET /confirmation" should {

    "show the page" when {

      "the user has answered the questions" in {

        val userAnswers: UserAnswers = phaseTwoJourney()

        setAnswers(userAnswers)

        val res = getRequestHeaders("/confirmation")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

        whenReady(res) { result =>
          result should have(
            httpStatus(OK),
            titleOf("What you can claim for this employee - Job Retention Scheme calculator - GOV.UK")
          )
        }
      }
    }
    "redirect to another page" when {

      "the user has not answered the questions" in {

        val res = getRequest("/confirmation")()

        whenReady(res) { result =>
          result should have(
            httpStatus(SEE_OTHER),
            redirectLocation("/job-retention-scheme-calculator/this-service-has-been-reset")
          )
        }
      }
    }
  }

//  "Confirmation Controller" must {
//
//    "return OK and the confirmation view with detailed breakdowns for a GET" in new CalculatorVersionConfiguration {
//      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(dummyUserAnswers))
//      val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)
//
//      val result = controller.onPageLoad()(request)
//
//      status(result) mustEqual OK
//      contentAsString(result) mustEqual view(breakdown, meta.claimPeriod, calculatorVersionConf)(request, messages).toString
//    }
//
//    "return OK and the phase two confirmation view with detailed breakdowns for a GET" in new CalculatorVersionConfiguration {
//      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(phaseTwoJourney()))
//      val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)
//
//      val result = controller.onPageLoad()(request)
//
//      val payment = RegularPaymentWithPhaseTwoPeriod(
//        Amount(2000.00),
//        Amount(2000.0),
//        PhaseTwoPeriod(fullPeriodWithPaymentDate("2020, 7, 1", "2020, 7, 31", "2020, 7, 31"), None, None))
//
//      val breakdown = PhaseTwoConfirmationViewBreakdown(
//        PhaseTwoFurloughCalculationResult(
//          1600.00,
//          Seq(PhaseTwoFurloughBreakdown(Amount(1600.0), payment, FullPeriodCap(2500.00)))
//        ),
//        PhaseTwoNicCalculationResult(
//          119.78,
//          Seq(PhaseTwoNicBreakdown(Amount(119.78), payment, Threshold(732.0, TaxYearEnding2021, Monthly), Payable))
//        ),
//        PhaseTwoPensionCalculationResult(
//          32.40,
//          Seq(PhaseTwoPensionBreakdown(Amount(32.40), payment, Threshold(520.0, TaxYearEnding2021, Monthly), DoesContribute))
//        )
//      )
//
//      status(result) mustEqual OK
//      contentAsString(result) mustEqual phaseTwoView(breakdown, period("2020, 7, 1", "2020, 7, 31"), calculatorVersionConf)(
//        request,
//        messages).toString
//    }
//  }
}
