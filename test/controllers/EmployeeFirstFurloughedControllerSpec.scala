/*
 * Copyright 2021 HM Revenue & Customs
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

import base.{SpecBase, SpecBaseControllerSpecs}
import controllers.actions.DataRetrievalActionImpl
import forms.EmployeeFirstFurloughedFormProvider
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.EmployeeFirstFurloughedPage
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.EmployeeFirstFurloughedView
import play.api.libs.json.{JsString, Json}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EmployeeFirstFurloughedControllerSpec extends SpecBaseControllerSpecs {

  val formProvider = new EmployeeFirstFurloughedFormProvider()
  private def form: Form[LocalDate] = formProvider()
  val validAnswer = LocalDate.now(ZoneOffset.UTC)

  lazy val employeeFirstFurloughedStartRoute: String = routes.EmployeeFirstFurloughedController.onPageLoad().url

  override val emptyUserAnswers = UserAnswers(userAnswersId)

  val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, employeeFirstFurloughedStartRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, employeeFirstFurloughedStartRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(
        "value.day"   -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year"  -> validAnswer.getYear.toString
      )

  val view = app.injector.instanceOf[EmployeeFirstFurloughedView]

  def controller(stubbedAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) =
    new EmployeeFirstFurloughedController(
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
    )

  "EmployeeFirstFurloughed Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(getRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form)(getRequest, messages).toString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = UserAnswers(userAnswersId).set(EmployeeFirstFurloughedPage, validAnswer).success.value
      val result = controller(Some(userAnswers)).onPageLoad()(getRequest)

      status(result) mustBe OK
      contentAsString(result) mustEqual
        view(form.fill(validAnswer))(getRequest, messages).toString()

    }

    //TODO Will need to implement this test once the page has been wired up in the navigator
//    "redirect to the next page when valid data is submitted" in {
//
//      val existingAnswers = emptyUserAnswers.copy(data = Json.obj(EmployeeFirstFurloughedPage.toString -> JsString(validAnswer.toString)))
//      val result = controller(Some(existingAnswers)).onSubmit()(postRequest)
//
//      status(result) mustEqual SEE_OTHER
//      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/employeeFirstFurloughed"
//    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val request =
        FakeRequest(POST, employeeFirstFurloughedStartRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "value"))

      val result = controller().onSubmit()(request)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm)(request, messages).toString()

    }
  }
}
