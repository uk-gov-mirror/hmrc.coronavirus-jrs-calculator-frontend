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

import java.time.{LocalDate, ZoneOffset}

import base.SpecBaseWithApplication
import forms.ClaimPeriodStartFormProvider
import models.{UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage}
import play.api.inject.bind
import play.api.libs.json.{JsString, Json}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.ClaimPeriodStartView

import scala.concurrent.Future

class ClaimPeriodStartControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  val formProvider = new ClaimPeriodStartFormProvider(frontendAppConfig)
  private def form = formProvider()

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = LocalDate.now(ZoneOffset.UTC)

  lazy val claimPeriodStartRoute = routes.ClaimPeriodStartController.onPageLoad().url

  override val emptyUserAnswers = UserAnswers(userAnswersId)

  val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, claimPeriodStartRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, claimPeriodStartRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(
        "startDate.day"   -> validAnswer.getDayOfMonth.toString,
        "startDate.month" -> validAnswer.getMonthValue.toString,
        "startDate.year"  -> validAnswer.getYear.toString
      )

  "ClaimPeriodStart Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[ClaimPeriodStartView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(getRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(ClaimPeriodStartPage, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[ClaimPeriodStartView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer))(getRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted and delete any existing mongo cache" in {

      val mockSessionRepository = mock[SessionRepository]

      val existingUserAnswers = emptyUserAnswers.copy(data = Json.obj(ClaimPeriodEndPage.toString -> JsString(validAnswer.toString)))

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(existingUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val result = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, claimPeriodStartRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("startDate", "invalid value"))

      val boundForm = form.bind(Map("startDate" -> "invalid value"))

      val view = application.injector.instanceOf[ClaimPeriodStartView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm)(request, messages).toString

      application.stop()
    }
  }
}
