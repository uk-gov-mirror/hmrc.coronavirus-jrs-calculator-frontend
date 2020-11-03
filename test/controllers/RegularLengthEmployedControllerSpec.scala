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

import base.SpecBaseControllerSpecs
import forms.RegularLengthEmployedFormProvider
import models.requests.DataRequest
import models.{RegularLengthEmployed, UserAnswers}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{ClaimPeriodStartPage, RegularLengthEmployedPage}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.RegularLengthEmployedView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RegularLengthEmployedControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

  val view = app.injector.instanceOf[RegularLengthEmployedView]

  val formProvider = new RegularLengthEmployedFormProvider()
  val form = formProvider()

  val controller = new RegularLengthEmployedController(
    messagesApi,
    mockSessionRepository,
    navigator,
    identifier,
    dataRetrieval,
    dataRequired,
    formProvider,
    component,
    view
  )

  lazy val regularLengthEmployedRouteGet = routes.RegularLengthEmployedController.onPageLoad().url
  lazy val regularLengthEmployedRoutePost = routes.RegularLengthEmployedController.onSubmit().url

  "RegularLengthEmployed Controller" must {

    "return OK and the correct view for a GET" in {

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(emptyUserAnswers))
      val request = FakeRequest(GET, regularLengthEmployedRouteGet).withCSRFToken
        .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      val result = controller.onPageLoad()(request)
      val dataRequest = DataRequest(request, emptyUserAnswers.id, emptyUserAnswers)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form)(dataRequest, messages).toString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers =
        UserAnswers(userAnswersId).set(RegularLengthEmployedPage, RegularLengthEmployed.Yes).success.value
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val request = FakeRequest(GET, regularLengthEmployedRouteGet).withCSRFToken
        .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      val result = controller.onPageLoad()(request)
      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(RegularLengthEmployed.Yes))(dataRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(ClaimPeriodStartPage, LocalDate.of(2020, 11, 1)).success.value

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val request =
        FakeRequest(POST, regularLengthEmployedRoutePost)
          .withFormUrlEncodedBody(("value", "yes"))
          .withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/pay-date/1"
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(ClaimPeriodStartPage, LocalDate.of(2020, 11, 1)).success.value

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val request =
        FakeRequest(POST, regularLengthEmployedRoutePost)
          .withFormUrlEncodedBody(("value", "invalid value"))
          .withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

      val boundForm = form.bind(Map("value" -> "invalid value"))
      val result = controller.onSubmit()(request)
      val dataRequest = DataRequest(request, emptyUserAnswers.id, emptyUserAnswers)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm)(dataRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request = FakeRequest(GET, regularLengthEmployedRouteGet).withCSRFToken
        .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      val result = controller.onPageLoad()(request)
      val dataRequest = DataRequest(request, emptyUserAnswers.id, emptyUserAnswers)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request =
        FakeRequest(POST, regularLengthEmployedRoutePost)
          .withFormUrlEncodedBody(("value", "yes"))
          .withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
