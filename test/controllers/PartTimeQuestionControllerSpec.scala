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
import forms.PartTimeQuestionFormProvider
import models.{FurloughStatus, FurloughWithinClaim, PartTimeQuestion}
import models.requests.DataRequest
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.PartTimeQuestionView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PartTimeQuestionControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

  lazy val partTimeQuestionRoute = routes.PartTimeQuestionController.onPageLoad().url

  val formProvider = new PartTimeQuestionFormProvider()
  val form = formProvider()

  val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, partTimeQuestionRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val view = app.injector.instanceOf[PartTimeQuestionView]

  val controller = new PartTimeQuestionController(
    messagesApi,
    mockSessionRepository,
    navigator,
    identifier,
    dataRetrieval,
    dataRequired,
    formProvider,
    component,
    view)

  "PartTimeQuestion Controller" must {

    "return OK and the correct view for a GET" in {
      val userAnswers = emptyUserAnswers
        .withClaimPeriodStart("2020,7,1")
        .withClaimPeriodEnd("2020,7,31")
        .withFurloughStartDate("2020,3,20")
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2020,7,15")
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val result = controller.onPageLoad()(getRequest)
      val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form, FurloughWithinClaim(LocalDate.of(2020, 7, 1), LocalDate.of(2020, 7, 15)))(dataRequest, messages).toString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = emptyUserAnswers
        .withClaimPeriodStart("2020,7,1")
        .withClaimPeriodEnd("2020,7,31")
        .withFurloughStartDate("2020,3,20")
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2020,7,15")
        .withPartTimeQuestion(PartTimeQuestion.values.head)
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

      val result = controller.onPageLoad()(getRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(PartTimeQuestion.values.head), FurloughWithinClaim(LocalDate.of(2020, 7, 1), LocalDate.of(2020, 7, 15)))(
          dataRequest,
          messages).toString
    }

    "redirect to the next page when valid data is submitted" in {
      val userAnswers = emptyUserAnswers
        .withClaimPeriodStart("2020,7,1")
        .withClaimPeriodEnd("2020,7,31")
        .withFurloughStartDate("2020,3,20")
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2020,7,15")
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val request =
        FakeRequest(POST, partTimeQuestionRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", PartTimeQuestion.values.head.toString))

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/part-time-periods"
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val userAnswers = emptyUserAnswers
        .withClaimPeriodStart("2020,7,1")
        .withClaimPeriodEnd("2020,7,31")
        .withFurloughStartDate("2020,3,20")
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2020,7,15")
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val request =
        FakeRequest(POST, partTimeQuestionRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))
      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      val result = controller.onSubmit()(request)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm, FurloughWithinClaim(LocalDate.of(2020, 7, 1), LocalDate.of(2020, 7, 15)))(dataRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request = FakeRequest(GET, partTimeQuestionRoute)
      val result = controller.onPageLoad()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request =
        FakeRequest(POST, partTimeQuestionRoute)
          .withFormUrlEncodedBody(("value", PartTimeQuestion.values.head.toString))

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
