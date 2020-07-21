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

import base.SpecBaseControllerSpecs
import forms.AdditionalPaymentStatusFormProvider
import models.requests.DataRequest
import models.{AdditionalPaymentStatus, UserAnswers}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.AdditionalPaymentStatusPage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.AdditionalPaymentStatusView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AdditionalPaymentStatusControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

  lazy val additionalPaymentStatusRoute = routes.AdditionalPaymentStatusController.onPageLoad().url
  lazy val additionalPaymentStatusRoutePost = routes.AdditionalPaymentStatusController.onSubmit().url

  val formProvider = new AdditionalPaymentStatusFormProvider()
  val form = formProvider()

  lazy val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, additionalPaymentStatusRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val validAnswer = AdditionalPaymentStatus.values.headOption.value

  val view = app.injector.instanceOf[AdditionalPaymentStatusView]

  val controller = new AdditionalPaymentStatusController(
    messagesApi,
    mockSessionRepository,
    navigator,
    identifier,
    dataRetrieval,
    dataRequired,
    formProvider,
    component,
    view)

  "AdditionalPaymentStatusController" must {

    "return OK and the correct view for a GET" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(emptyUserAnswers))
      val dataRequest = DataRequest(getRequest, emptyUserAnswers.id, emptyUserAnswers)
      val result = controller.onPageLoad()(getRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form)(dataRequest, messages).toString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val modifiedUserAnswers = UserAnswers("id").set(AdditionalPaymentStatusPage, validAnswer).success.value
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(modifiedUserAnswers))
      val dataRequest = DataRequest(getRequest, modifiedUserAnswers.id, modifiedUserAnswers)
      val result = controller.onPageLoad()(getRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(validAnswer))(dataRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(emptyUserAnswers))
      val request =
        FakeRequest(POST, additionalPaymentStatusRoutePost)
          .withFormUrlEncodedBody(("value", validAnswer))

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/additional-pay-periods"
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(emptyUserAnswers))
      val request =
        FakeRequest(POST, additionalPaymentStatusRoutePost).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))
      val dataRequest = DataRequest(request, emptyUserAnswers.id, emptyUserAnswers)
      val result = controller.onSubmit()(request)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm)(dataRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request = FakeRequest(GET, additionalPaymentStatusRoute)
      val result = controller.onPageLoad()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request =
        FakeRequest(POST, additionalPaymentStatusRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
