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
import forms.ClaimPeriodEndFormProvider
import models.UserAnswers
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage}
import play.api.inject.bind
import play.api.libs.json.{JsString, Json}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.ClaimPeriodEndView

import scala.concurrent.Future

class ClaimPeriodEndControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  val formProvider = new ClaimPeriodEndFormProvider(frontendAppConfig)

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = LocalDate.now(ZoneOffset.UTC)

  lazy val claimPeriodEndRoute = routes.ClaimPeriodEndController.onPageLoad().url

  override val emptyUserAnswers = UserAnswers(userAnswersId)

  val claimStart = LocalDate.of(2020, 3, 1)

  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(
      ClaimPeriodStartPage.toString -> JsString(claimStart.toString)
    )
  )

  private def form = formProvider(claimStart)

  val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, claimPeriodEndRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, claimPeriodEndRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(
        "endDate.day"   -> validAnswer.getDayOfMonth.toString,
        "endDate.month" -> validAnswer.getMonthValue.toString,
        "endDate.year"  -> validAnswer.getYear.toString
      )

  "ClaimPeriodEnd Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[ClaimPeriodEndView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(getRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(
        userAnswersId,
        Json.obj(
          ClaimPeriodStartPage.toString -> JsString("2020-03-10"),
          ClaimPeriodEndPage.toString   -> JsString(validAnswer.toString)
        )
      )

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[ClaimPeriodEndView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer))(getRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
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

    "redirect to the /claim-period-start if there is no claim-start stored in mongo" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(UserAnswers(userAnswersId)))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val result = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ClaimPeriodStartController.onPageLoad().url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, claimPeriodEndRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[ClaimPeriodEndView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm)(request, messages).toString

      application.stop()
    }
  }
}
