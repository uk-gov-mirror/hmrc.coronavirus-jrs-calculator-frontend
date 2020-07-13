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
import forms.ClaimPeriodQuestionFormProvider
import models.ClaimPeriodQuestion.ClaimOnSamePeriod
import navigation.FakeNavigator
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.JsObject
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ClaimPeriodQuestionView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ClaimPeriodQuestionControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  private val formProvider = new ClaimPeriodQuestionFormProvider()
  private val form = formProvider()
  private val claimStart = LocalDate.now
  private val claimEnd = claimStart.plusDays(30)

  lazy val claimPeriodQuestionRoute = routes.ClaimPeriodQuestionController.onPageLoad().url

  val view = app.injector.instanceOf[ClaimPeriodQuestionView]
  val controller = new ClaimPeriodQuestionController(
    messagesApi,
    mockSessionRepository,
    navigator,
    identifier,
    dataRetrieval,
    dataRequired,
    formProvider,
    component,
    view)

  "ClaimPeriodQuestion Controller" must {

    "return OK and the correct view for a GET" in {
      val userAnswers = dummyUserAnswers.withClaimPeriodStart(claimStart.toString).withClaimPeriodEnd(claimEnd.toString)
      val getRequest: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, claimPeriodQuestionRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))

      val result = controller.onPageLoad()(getRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, claimStart, claimEnd)(getRequest, messages).toString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = dummyUserAnswers
        .withClaimPeriodStart(claimStart.toString)
        .withClaimPeriodEnd(claimEnd.toString)
        .withClaimPeriodQuestion(ClaimOnSamePeriod)

      val getRequest: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, claimPeriodQuestionRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))

      val result = controller.onPageLoad()(getRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(ClaimOnSamePeriod), claimStart, claimEnd)(getRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {
      val controller = new ClaimPeriodQuestionController(
        messagesApi,
        mockSessionRepository,
        new FakeNavigator(onwardRoute),
        identifier,
        dataRetrieval,
        dataRequired,
        formProvider,
        component,
        view)

      val request =
        FakeRequest(POST, claimPeriodQuestionRoute)
          .withFormUrlEncodedBody(("value", ClaimOnSamePeriod.toString))

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val request =
        FakeRequest(POST, claimPeriodQuestionRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit()(request)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm, claimStart, claimEnd)(request, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val request = FakeRequest(GET, claimPeriodQuestionRoute)

      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)

      val result = controller.onPageLoad()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val request =
        FakeRequest(POST, claimPeriodQuestionRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to fast-journey-reset if going back from fast journey" in {
      val controller = new ClaimPeriodQuestionController(
        messagesApi,
        mockSessionRepository,
        navigator,
        identifier,
        dataRetrieval,
        dataRequired,
        formProvider,
        component,
        view) {
        override protected val didNotReuseDates: (Option[String], JsObject) => Boolean = (_, _) => true
      }

      val getRequest: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, claimPeriodQuestionRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(emptyUserAnswers))
      val result = controller.onPageLoad()(getRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ResetCalculationController.onPageLoad().url
    }
  }
}
