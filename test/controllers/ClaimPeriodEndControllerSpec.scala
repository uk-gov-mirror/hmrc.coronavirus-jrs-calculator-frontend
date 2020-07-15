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

import base.SpecBaseControllerSpecs
import forms.ClaimPeriodEndFormProvider
import models.UserAnswers
import models.requests.DataRequest
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage}
import play.api.data.Form
import play.api.libs.json.{JsString, Json}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ClaimPeriodEndView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ClaimPeriodEndControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

  val formProvider = new ClaimPeriodEndFormProvider()

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = LocalDate.now(ZoneOffset.UTC)

  lazy val claimPeriodEndRoute = routes.ClaimPeriodEndController.onPageLoad().url

  override val emptyUserAnswers = UserAnswers(userAnswersId)

  val claimStart = LocalDate.of(2020, 3, 1)

  val userAnswers =
    emptyUserAnswers
      .withClaimPeriodStart(claimStart.toString)

  val view = app.injector.instanceOf[ClaimPeriodEndView]
  val controller = new ClaimPeriodEndController(
    messagesApi,
    mockSessionRepository,
    navigator,
    identifier,
    dataRetrieval,
    dataRequired,
    formProvider,
    component,
    view)

  private def form: Form[LocalDate] = formProvider(claimStart)

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
      val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))

      val result = controller.onPageLoad()(getRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form)(dataRequest, messages).toString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = UserAnswers(
        userAnswersId,
        Json.obj(
          ClaimPeriodStartPage.toString -> JsString("2020-03-10"),
          ClaimPeriodEndPage.toString   -> JsString(validAnswer.toString)
        )
      )

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val result = controller.onPageLoad()(getRequest)

      status(result) mustEqual OK

      val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(form.fill(validAnswer))(dataRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {
      val claimEnd = claimStart.plusDays(20)

      val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest(POST, claimPeriodEndRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(
            "endDate.day"   -> claimEnd.getDayOfMonth.toString,
            "endDate.month" -> claimEnd.getMonthValue.toString,
            "endDate.year"  -> claimEnd.getYear.toString
          )

      val result = controller.onSubmit()(postRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/furlough-start"
    }

    "redirect to the /claim-period-start if there is no claim-start stored in mongo" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(emptyUserAnswers))
      val result = controller.onPageLoad()(getRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ClaimPeriodStartController.onPageLoad().url
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val request =
        FakeRequest(POST, claimPeriodEndRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))

      val result = controller.onSubmit()(request)

      status(result) mustEqual BAD_REQUEST

      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(boundForm)(dataRequest, messages).toString
    }
  }
}
