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
import forms.EmployeeRTISubmissionFormProvider
import models.requests.DataRequest
import models.{EmployeeRTISubmission, UserAnswers}
import pages.EmployeeRTISubmissionPage
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.EmployeeRTISubmissionView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EmployeeRTISubmissionControllerSpec extends SpecBaseControllerSpecs {

  lazy val employeeRTISubmissionRoute = routes.EmployeeRTISubmissionController.onPageLoad().url

  val formProvider = new EmployeeRTISubmissionFormProvider()
  val form = formProvider()

  val view = app.injector.instanceOf[EmployeeRTISubmissionView]

  lazy val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, employeeRTISubmissionRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  lazy val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, employeeRTISubmissionRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(
        "value" -> EmployeeRTISubmission.values.head.toString,
      )

  def controller(stubbedAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) =
    new EmployeeRTISubmissionController(
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

  val userAnswers =
    UserAnswers(userAnswersId)
      .withClaimPeriodStart(LocalDate.of(2020, 11, 1).toString)
      .withEmployeeStartDate(LocalDate.of(2020, 2, 2).toString)

  "EmployeeRTISubmission Controller" must {

    "return OK and the correct view for a GET" in {

      val result = controller(Some(userAnswers)).onPageLoad()(getRequest)

      status(result) mustEqual OK
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswersUpdated = userAnswers.set(EmployeeRTISubmissionPage, EmployeeRTISubmission.values.head).success.value

      val result = controller(Some(userAnswersUpdated)).onPageLoad()(getRequest)
      val dataRequest = DataRequest(getRequest, userAnswersUpdated.id, userAnswersUpdated)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(EmployeeRTISubmission.values.head))(dataRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {

      val result = controller(Some(userAnswers)).onSubmit()(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/pay-date/1"
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val request =
        FakeRequest(POST, employeeRTISubmissionRoute).withCSRFToken
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
