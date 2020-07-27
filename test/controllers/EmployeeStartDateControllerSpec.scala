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
import forms.EmployeeStartDateFormProvider
import models.UserAnswers
import models.requests.DataRequest
import pages.EmployeeStartDatePage
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.EmployeeStartDateView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EmployeeStartDateControllerSpec extends SpecBaseControllerSpecs {

  val validAnswer = LocalDate.of(2020, 2, 1)
  val furloughStart = LocalDate.of(2020, 3, 18)

  val formProvider = new EmployeeStartDateFormProvider()
  private def form = formProvider(furloughStart)

  lazy val employeeStartDateRoute = routes.EmployeeStartDateController.onPageLoad().url

  val userAnswers =
    UserAnswers(userAnswersId).withFurloughStartDate(furloughStart.toString)

  lazy val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, employeeStartDateRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  lazy val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, employeeStartDateRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(
        "value.day"   -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year"  -> validAnswer.getYear.toString
      )

  val view = app.injector.instanceOf[EmployeeStartDateView]

  def controller(stubbedAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) =
    new EmployeeStartDateController(
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

  "EmployeeStartDate Controller" must {
    "return OK and the correct view for a GET" in {
      val result = controller(Some(userAnswers)).onPageLoad()(getRequest)

      status(result) mustEqual OK

      val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)
      contentAsString(result) mustEqual view(form)(dataRequest, messages).toString
    }

    "redirect to /furlough-start if its already not found in userAnswers" in {
      val result = controller().onPageLoad()(getRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.FurloughStartDateController.onPageLoad().url
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers1 = userAnswers.set(EmployeeStartDatePage, validAnswer).success.value
      val result = controller(Some(userAnswers1)).onPageLoad()(getRequest)
      val dataRequest = DataRequest(getRequest, userAnswers1.id, userAnswers1)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(validAnswer))(dataRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {
      val result = controller(Some(userAnswers)).onSubmit()(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/pay-date/1"
    }

    "redirect POST to /furlough-start if its already not found in userAnswers" in {
      val result = controller().onSubmit()(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.FurloughStartDateController.onPageLoad().url
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val request =
        FakeRequest(POST, employeeStartDateRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller(Some(userAnswers)).onSubmit()(request)
      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm)(dataRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(None).onPageLoad()(getRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val result = controller(None).onSubmit()(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
