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
import forms.PartTimeQuestionFormProvider
import models.PartTimeQuestion
import models.requests.DataRequest
import navigation.{FakeNavigator, Navigator}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.PartTimeQuestionView

class PartTimeQuestionControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val partTimeQuestionRoute = routes.PartTimeQuestionController.onPageLoad().url

  val formProvider = new PartTimeQuestionFormProvider()
  val form = formProvider()
  val claimStart = LocalDate.of(2020, 3, 1)

  val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, partTimeQuestionRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  "PartTimeQuestion Controller" must {

    "return OK and the correct view for a GET" in {
      val userAnswers = emptyUserAnswers.withClaimPeriodStart(claimStart.toString)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[PartTimeQuestionView]

      status(result) mustEqual OK

      val dataRequest = DataRequest(getRequest, emptyUserAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(form, claimStart)(dataRequest, messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET for phase two" in {
      val userAnswers = emptyUserAnswers.withClaimPeriodStart("2020, 7, 2")

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) must include(messagesApi.messages("en")("partTimeQuestion.1stJuly.title"))
      contentAsString(result) must include(messagesApi.messages("en")("partTimeQuestion.1stJuly.heading"))
      contentAsString(result) must include(messagesApi.messages("en")("partTimeQuestion.1stJuly.p1"))

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .withPartTimeQuestion(PartTimeQuestion.values.head)
        .withClaimPeriodStart(claimStart.toString)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[PartTimeQuestionView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(form.fill(PartTimeQuestion.values.head), claimStart)(dataRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {
      val userAnswers = emptyUserAnswers.withClaimPeriodStart(claimStart.toString)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, partTimeQuestionRoute)
          .withFormUrlEncodedBody(("value", PartTimeQuestion.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.withClaimPeriodStart(claimStart.toString)
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, partTimeQuestionRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[PartTimeQuestionView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(boundForm, claimStart)(dataRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, partTimeQuestionRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, partTimeQuestionRoute)
          .withFormUrlEncodedBody(("value", PartTimeQuestion.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
