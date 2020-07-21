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
import forms.VariableLengthEmployedFormProvider
import models.EmployeeStarted
import models.requests.DataRequest
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.VariableLengthEmployedView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class VariableLengthEmployedControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {
  lazy val variableLengthEmployedRoute = routes.VariableLengthEmployedController.onPageLoad().url
  val formProvider = new VariableLengthEmployedFormProvider()
  val form = formProvider()

  lazy val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, variableLengthEmployedRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val view = app.injector.instanceOf[VariableLengthEmployedView]

  val controller = new VariableLengthEmployedController(
    messagesApi,
    mockSessionRepository,
    navigator,
    identifier,
    dataRetrieval,
    dataRequired,
    formProvider,
    component,
    view)

  "VariableLengthEmployed Controller" must {
    "return OK and the correct view for a GET" in {
      val userAnswers = emptyUserAnswers.withClaimPeriodStart("2020,3,1")
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val result = controller.onPageLoad()(getRequest)
      val dataRequest = DataRequest(getRequest, emptyUserAnswers.id, emptyUserAnswers)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form)(dataRequest, messages).toString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = emptyUserAnswers
        .withClaimPeriodStart("2020,3,1")
        .withEmployeeStartedOnOrBefore1Feb2019()
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val result = controller.onPageLoad()(getRequest)
      val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(EmployeeStarted.values.head))(dataRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {
      val userAnswers = emptyUserAnswers
        .withClaimPeriodStart("2020,3,1")

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val request =
        FakeRequest(POST, variableLengthEmployedRoute)
          .withFormUrlEncodedBody(("value", EmployeeStarted.values.head.toString))

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/pay-date/1"
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val userAnswers = emptyUserAnswers
        .withClaimPeriodStart("2020,3,1")

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val request =
        FakeRequest(POST, variableLengthEmployedRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))
      val result = controller.onSubmit()(request)
      val dataRequest = DataRequest(request, emptyUserAnswers.id, emptyUserAnswers)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm)(dataRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request = FakeRequest(GET, variableLengthEmployedRoute)
      val result = controller.onPageLoad()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request =
        FakeRequest(POST, variableLengthEmployedRoute)
          .withFormUrlEncodedBody(("value", EmployeeStarted.values.head.toString))

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
