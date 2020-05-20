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

import base.SpecBaseWithApplication
import forms.ClaimPeriodQuestionFormProvider
import models.ClaimPeriodQuestion.ClaimOnSamePeriod
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.ClaimPeriodQuestionView

import scala.concurrent.Future

class ClaimPeriodQuestionControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  private val formProvider = new ClaimPeriodQuestionFormProvider()
  private val form = formProvider()
  private val claimStart = LocalDate.now
  private val claimEnd = claimStart.plusDays(30)

  lazy val claimPeriodQuestionRoute = routes.ClaimPeriodQuestionController.onPageLoad().url

  "ClaimPeriodQuestion Controller" must {

    "redirect to claim-period-start view if feature flag is disabled for a GET" in {

      val application =
        applicationBuilder(config = Map("fastTrackJourney.enabled" -> "false"), userAnswers = Some(dummyUserAnswers)).build()

      val request = FakeRequest(GET, claimPeriodQuestionRoute).withCSRFToken

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).get must include("job-retention-scheme-calculator/claim-period-start")

      application.stop()
    }

    "return OK and the correct view for a GET" in {
      val userAnswers = dummyUserAnswers.withClaimPeriodStart(claimStart.toString).withClaimPeriodEnd(claimEnd.toString)
      val application = applicationBuilder(config = Map("fastTrackJourney.enabled" -> "true"), userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, claimPeriodQuestionRoute).withCSRFToken

      val result = route(application, request).value

      val view = application.injector.instanceOf[ClaimPeriodQuestionView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(form, claimStart, claimEnd)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = dummyUserAnswers
        .withClaimPeriodStart(claimStart.toString)
        .withClaimPeriodEnd(claimEnd.toString)
        .withClaimPeriodQuestion(ClaimOnSamePeriod)

      val application = applicationBuilder(config = Map("fastTrackJourney.enabled" -> "true"), userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, claimPeriodQuestionRoute).withCSRFToken

      val view = application.injector.instanceOf[ClaimPeriodQuestionView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(ClaimOnSamePeriod), claimStart, claimEnd)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {
      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(dummyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request =
        FakeRequest(POST, claimPeriodQuestionRoute)
          .withFormUrlEncodedBody(("value", ClaimOnSamePeriod.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val userAnswers = dummyUserAnswers.withClaimPeriodStart(claimStart.toString).withClaimPeriodEnd(claimEnd.toString)
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, claimPeriodQuestionRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[ClaimPeriodQuestionView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, claimStart, claimEnd)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, claimPeriodQuestionRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, claimPeriodQuestionRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
