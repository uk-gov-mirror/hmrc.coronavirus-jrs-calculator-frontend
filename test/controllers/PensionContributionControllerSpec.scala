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
import forms.PensionContributionFormProvider
import models.PensionStatus.DoesContribute
import models.UserAnswers
import models.requests.DataRequest
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.PensionContributionView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PensionContributionControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

  val formProvider = new PensionContributionFormProvider()
  val form = formProvider()
  lazy val pensionContributionRoute = routes.PensionContributionController.onPageLoad().url

  val view = app.injector.instanceOf[PensionContributionView]

  val controller = new PensionContributionController(
    messagesApi,
    mockSessionRepository,
    navigator,
    identifier,
    dataRetrieval,
    dataRequired,
    formProvider,
    component,
    view)

  "PensionContributionController" must {

    "return OK and the correct view for a GET" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(emptyUserAnswers))
      val request = FakeRequest(GET, pensionContributionRoute).withCSRFToken
        .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      val result = controller.onPageLoad()(request)
      val dataRequest = DataRequest(request, emptyUserAnswers.id, emptyUserAnswers)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form)(dataRequest, messages).toString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = UserAnswers(userAnswersId).withPensionStatus(DoesContribute)
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val request = FakeRequest(GET, pensionContributionRoute).withCSRFToken
        .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      val result = controller.onPageLoad()(request)
      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(DoesContribute))(dataRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(emptyUserAnswers))
      val request =
        FakeRequest(POST, pensionContributionRoute)
          .withFormUrlEncodedBody(("value", "doesContribute"))
          .withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/confirmation"
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(emptyUserAnswers))
      val request =
        FakeRequest(POST, pensionContributionRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))
      val result = controller.onSubmit()(request)
      val dataRequest = DataRequest(request, emptyUserAnswers.id, emptyUserAnswers)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm)(dataRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request = FakeRequest(GET, pensionContributionRoute)
      val result = controller.onPageLoad()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request =
        FakeRequest(POST, pensionContributionRoute)
          .withFormUrlEncodedBody(("value", "optedIn"))

      val result = controller.onPageLoad()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
