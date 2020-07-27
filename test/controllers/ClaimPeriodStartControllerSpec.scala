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
import controllers.actions.DataRetrievalActionImpl
import forms.ClaimPeriodStartFormProvider
import models.UserAnswers
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage}
import play.api.data.Form
import play.api.libs.json.{JsString, Json}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ClaimPeriodStartView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ClaimPeriodStartControllerSpec extends SpecBaseControllerSpecs {

  val formProvider = new ClaimPeriodStartFormProvider()
  private def form: Form[LocalDate] = formProvider()
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

  val view = app.injector.instanceOf[ClaimPeriodStartView]

  def controller(stubbedAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) =
    new ClaimPeriodStartController(
      messagesApi,
      mockSessionRepository,
      navigator,
      identifier,
      new DataRetrievalActionImpl(mockSessionRepository) {
        override protected val identifierRetrieval: String => Future[Option[UserAnswers]] =
          _ => Future.successful(stubbedAnswers)
      },
      formProvider,
      component,
      view
    )

  "ClaimPeriodStart Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(getRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form)(getRequest, messages).toString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = UserAnswers(userAnswersId).set(ClaimPeriodStartPage, validAnswer).success.value
      val result = controller(Some(userAnswers)).onPageLoad()(getRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(validAnswer))(getRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted and delete any existing mongo cache" in {
      val existingUserAnswers = emptyUserAnswers.copy(data = Json.obj(ClaimPeriodEndPage.toString -> JsString(validAnswer.toString)))
      val result = controller(Some(existingUserAnswers)).onSubmit()(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/claim-period-end"
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val request =
        FakeRequest(POST, claimPeriodStartRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("startDate", "invalid value"))

      val boundForm = form.bind(Map("startDate" -> "invalid value"))

      val result = controller().onSubmit()(request)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm)(request, messages).toString
    }
  }
}
