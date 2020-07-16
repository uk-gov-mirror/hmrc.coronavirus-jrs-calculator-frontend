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
import forms.NicCategoryFormProvider
import models.requests.DataRequest
import models.{NicCategory, UserAnswers}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.NicCategoryPage
import play.api.mvc.AnyContentAsEmpty
import play.api.test
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.NicCategoryView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NicCategoryControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

  lazy val nicCategoryRoute = routes.NicCategoryController.onPageLoad().url

  val formProvider = new NicCategoryFormProvider()
  val form = formProvider()

  val view = app.injector.instanceOf[NicCategoryView]

  val controller = new NicCategoryController(
    messagesApi,
    mockSessionRepository,
    navigator,
    identifier,
    dataRetrieval,
    dataRequired,
    formProvider,
    component,
    view)

  "NicCategory Controller" must {

    "return OK and the correct view for a GET" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(emptyUserAnswers))

      val request = FakeRequest(GET, nicCategoryRoute).withCSRFToken
        .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

      val result = controller.onPageLoad()(request)

      status(result) mustEqual OK

      val dataRequest = DataRequest(request, emptyUserAnswers.id, emptyUserAnswers)

      contentAsString(result) mustEqual
        view(form)(dataRequest, messages).toString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(NicCategoryPage, NicCategory.values.head).success.value

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))

      val request = FakeRequest(GET, nicCategoryRoute).withCSRFToken
        .asInstanceOf[test.FakeRequest[AnyContentAsEmpty.type]]

      val result = controller.onPageLoad()(request)

      status(result) mustEqual OK

      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(form.fill(NicCategory.values.head))(dataRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, nicCategoryRoute)
          .withFormUrlEncodedBody(("value", NicCategory.values.head.toString))

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/pension"
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, nicCategoryRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller.onSubmit()(request)

      status(result) mustEqual BAD_REQUEST

      val dataRequest = DataRequest(request, emptyUserAnswers.id, emptyUserAnswers)

      contentAsString(result) mustEqual
        view(boundForm)(dataRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request = FakeRequest(GET, nicCategoryRoute)

      val result = controller.onPageLoad()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)

      val request =
        FakeRequest(POST, nicCategoryRoute)
          .withFormUrlEncodedBody(("value", NicCategory.values.head.toString))

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
