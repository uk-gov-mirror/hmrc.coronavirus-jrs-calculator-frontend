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

import base.SpecBaseControllerSpecs
import forms.RegularPayAmountFormProvider
import models.{RegularLengthEmployed, UserAnswers}
import models.requests.DataRequest
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{OnPayrollBefore30thOct2020Page, PaymentFrequencyPage, RegularLengthEmployedPage, RegularPayAmountPage}
import play.api.libs.json.{JsNumber, JsString, Json}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.play.test.LogCapturing
import utils.LocalDateHelpers.{mar19th2020, mar2nd2021, oct30th2020}
import utils.PagerDutyHelper
import views.ViewUtils.dateToString
import views.html.RegularPayAmountView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RegularPayAmountControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

  val formProvider = new RegularPayAmountFormProvider()
  val form         = formProvider()

  lazy val regularPayAmountRoute = routes.RegularPayAmountController.onPageLoad().url
  val postAction                 = controllers.routes.RegularPayAmountController.onSubmit()

  val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, regularPayAmountRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val view = app.injector.instanceOf[RegularPayAmountView]

  val controller = new RegularPayAmountController(messagesApi,
                                                  mockSessionRepository,
                                                  navigator,
                                                  identifier,
                                                  dataRetrieval,
                                                  dataRequired,
                                                  formProvider,
                                                  component,
                                                  view)

  "RegularPayAmountController" must {

    "onPageLoad" when {

      "return OK and the correct view for a GET" in {
        val userAnswers = emptyUserAnswers
          .set(RegularLengthEmployedPage, RegularLengthEmployed.Yes)
          .success
          .value

        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
        val result      = controller.onPageLoad()(getRequest)
        val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(form, postAction, dateToString(mar19th2020))(dataRequest, messages).toString
      }
    }

    "onSubmit" when {

      "redirect to the next page when valid data is submitted" in {

        val userAnswers = emptyUserAnswers
          .set(RegularLengthEmployedPage, RegularLengthEmployed.Yes)
          .success
          .value

        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
        val request =
          FakeRequest(POST, regularPayAmountRoute)
            .withFormUrlEncodedBody(("value", "111"))

        val result = controller.onSubmit()(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/topup-question"
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val userAnswers = emptyUserAnswers
          .set(RegularLengthEmployedPage, RegularLengthEmployed.Yes)
          .success
          .value

        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
        val request =
          FakeRequest(POST, regularPayAmountRoute).withCSRFToken
            .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm   = form.bind(Map("value" -> "invalid value"))
        val result      = controller.onSubmit()(request)
        val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual
          view(boundForm, postAction, dateToString(mar19th2020))(dataRequest, messages).toString
      }

      "redirect to Session Expired for a GET if no existing data is found" in {
        when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
        val request = FakeRequest(GET, regularPayAmountRoute)
        val result  = controller.onPageLoad()(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
      }

      "redirect to Session Expired for a POST if no existing data is found" in {
        when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
        val request =
          FakeRequest(POST, regularPayAmountRoute)
            .withFormUrlEncodedBody(("value", "111"))

        val result = controller.onSubmit()(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
      }
    }
  }

  "cutoffDateResolver" when {

    "employee is type 1" must {

      "return mar19th2020" in {

        val userAnswers = emptyUserAnswers
          .set(RegularLengthEmployedPage, RegularLengthEmployed.Yes)
          .success
          .value

        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
        val request = DataRequest(getRequest, userAnswers.id, userAnswers)

        val result = controller.cutoffDateResolver()(request)

        result mustBe dateToString(mar19th2020)
      }
    }

    "employee is type 2a" must {

      "return mar19th2020" in {

        val userAnswers = emptyUserAnswers
          .set(RegularLengthEmployedPage, RegularLengthEmployed.No)
          .success
          .value
          .set(OnPayrollBefore30thOct2020Page, true)
          .success
          .value

        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
        val request = DataRequest(getRequest, userAnswers.id, userAnswers)

        val result = controller.cutoffDateResolver()(request)

        result mustBe dateToString(oct30th2020)
      }
    }

    "employee is type 2b" must {

      "return mar19th2020" in {

        val userAnswers = emptyUserAnswers
          .set(RegularLengthEmployedPage, RegularLengthEmployed.No)
          .success
          .value
          .set(OnPayrollBefore30thOct2020Page, false)
          .success
          .value

        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
        val request = DataRequest(getRequest, userAnswers.id, userAnswers)

        val result = controller.cutoffDateResolver()(request)

        result mustBe dateToString(mar2nd2021)
      }
    }

    "employee type can't be resolved" must {

      "throw ISE" in {

        val userAnswers = emptyUserAnswers

        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
        val request = DataRequest(getRequest, userAnswers.id, userAnswers)

        intercept[InternalServerException] {
          controller.cutoffDateResolver()(request)
        }
      }
    }
  }
}
