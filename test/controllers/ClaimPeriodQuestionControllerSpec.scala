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
import controllers.actions.DataRetrievalActionImpl
import forms.ClaimPeriodQuestionFormProvider
import models.ClaimPeriodQuestion.ClaimOnSamePeriod
import models.UserAnswers
import play.api.libs.json.JsObject
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswerPersistence
import views.html.ClaimPeriodQuestionView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ClaimPeriodQuestionControllerSpec extends SpecBaseControllerSpecs {

  private val formProvider = new ClaimPeriodQuestionFormProvider()
  private val form = formProvider()
  private val claimStart = LocalDate.now
  private val claimEnd = claimStart.plusDays(30)

  lazy val claimPeriodQuestionRoute = routes.ClaimPeriodQuestionController.onPageLoad().url

  val view = app.injector.instanceOf[ClaimPeriodQuestionView]

  def controller(stubbedAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) =
    new ClaimPeriodQuestionController(
      messagesApi,
      mockSessionRepository,
      navigator,
      identifier,
      new DataRetrievalActionImpl(mockSessionRepository) {
        override protected val identifierRetrieval: String => Future[Option[UserAnswers]] =
          _ => Future.successful(stubbedAnswers)
      },
      dataRequired,
      formProvider,
      component,
      view
    ) {
      override val userAnswerPersistence = new UserAnswerPersistence(_ => Future.successful(true))
    }

  "ClaimPeriodQuestion Controller" must {

    "return OK and the correct view for a GET" in {
      val userAnswers = dummyUserAnswers.withClaimPeriodStart(claimStart.toString).withClaimPeriodEnd(claimEnd.toString)
      val getRequest: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, claimPeriodQuestionRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

      val result = controller(Some(userAnswers)).onPageLoad()(getRequest)

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

      val result = controller(Some(userAnswers)).onPageLoad()(getRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(ClaimOnSamePeriod), claimStart, claimEnd)(getRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {
      val userAnswers = dummyUserAnswers
        .withClaimPeriodStart(claimStart.toString)
        .withClaimPeriodEnd(claimEnd.toString)
        .withClaimPeriodQuestion(ClaimOnSamePeriod)

      val controller = new ClaimPeriodQuestionController(
        messagesApi,
        mockSessionRepository,
        navigator,
        identifier,
        new DataRetrievalActionImpl(mockSessionRepository) {
          override protected val identifierRetrieval: String => Future[Option[UserAnswers]] =
            _ => Future.successful(Some(userAnswers))
        },
        dataRequired,
        formProvider,
        component,
        view
      ) {
        override val userAnswerPersistence = new UserAnswerPersistence(_ => Future.successful(true))
      }

      val request =
        FakeRequest(POST, claimPeriodQuestionRoute)
          .withFormUrlEncodedBody(("value", ClaimOnSamePeriod.toString))

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/furlough-period-question"
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val userAnswers = dummyUserAnswers
        .withClaimPeriodStart(claimStart.toString)
        .withClaimPeriodEnd(claimEnd.toString)
        .withClaimPeriodQuestion(ClaimOnSamePeriod)

      val request =
        FakeRequest(POST, claimPeriodQuestionRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val result = controller(Some(userAnswers)).onSubmit()(request)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm, claimStart, claimEnd)(request, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val request = FakeRequest(GET, claimPeriodQuestionRoute)
      val result = controller(None).onPageLoad()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val request =
        FakeRequest(POST, claimPeriodQuestionRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = controller(None).onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to fast-journey-reset if going back from fast journey" in {
      val controller = new ClaimPeriodQuestionController(
        messagesApi,
        mockSessionRepository,
        navigator,
        identifier,
        new DataRetrievalActionImpl(mockSessionRepository) {
          override protected val identifierRetrieval: String => Future[Option[UserAnswers]] =
            _ => Future.successful(Some(emptyUserAnswers))
        },
        dataRequired,
        formProvider,
        component,
        view
      ) {
        override protected val didNotReuseDates: (Option[String], JsObject) => Boolean = (_, _) => true
      }

      val getRequest: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, claimPeriodQuestionRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

      val result = controller.onPageLoad()(getRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ResetCalculationController.onPageLoad().url
    }
  }
}
