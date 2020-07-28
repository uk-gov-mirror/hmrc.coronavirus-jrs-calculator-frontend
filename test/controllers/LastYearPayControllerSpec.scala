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
import forms.LastYearPayFormProvider
import models.requests.DataRequest
import models.{Amount, LastYearPayment, UserAnswers}
import pages.LastYearPayPage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswerPersistence
import views.html.LastYearPayView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LastYearPayControllerSpec extends SpecBaseControllerSpecs {

  val formProvider = new LastYearPayFormProvider()
  val form = formProvider()

  val variableMonthlyUserAnswers = variableMonthlyPartial

  val validAnswer = Amount(BigDecimal(100))

  val validDate = LocalDate.of(2019, 3, 1)

  lazy val lastYearPayRoute = routes.LastYearPayController.onPageLoad(1).url

  lazy val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, lastYearPayRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  def getRequest(idx: Int) =
    FakeRequest(GET, routes.LastYearPayController.onPageLoad(idx).url).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val view = app.injector.instanceOf[LastYearPayView]

  def controller(stubbedAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) = new LastYearPayController(
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
    view) {
      override val userAnswerPersistence = new UserAnswerPersistence(_ => Future.successful(true))
    }

  "LastYearPay Controller" must {

    "redirect to error page for GET when extract from user answers fails" in {
      val result = controller().onPageLoad(1)(getRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
    }

    "redirect to error page for GET when index is not valid" when {
      "index is negative" in {
        val result = controller(Some(variableMonthlyUserAnswers)).onPageLoad(-1)(getRequest(-1))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
      }

      "index is 0" in {
        val result = controller(Some(variableMonthlyUserAnswers)).onPageLoad(0)(getRequest(0))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
      }

      "index is too high" in {
        val result = controller(Some(variableMonthlyUserAnswers)).onPageLoad(3)(getRequest(3))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
      }
    }

    "return OK and the correct view for a GET" in {
      val request = getRequest(1)
      val result = controller(Some(variableMonthlyUserAnswers)).onPageLoad(1)(request)
      val dataRequest = DataRequest(request, variableMonthlyUserAnswers.id, variableMonthlyUserAnswers)
      val expectedView = view(form, 1, period("2019, 3, 1", "2019, 3, 31"))(dataRequest, messages).toString

      status(result) mustEqual OK
      contentAsString(result) mustEqual expectedView
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = variableMonthlyUserAnswers.set(LastYearPayPage, LastYearPayment(validDate, validAnswer)).success.value
      val request = getRequest(1)
      val result = controller(Some(userAnswers)).onPageLoad(1)(request)
      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(validAnswer), 1, period("2019, 3, 1", "2019, 3, 31"))(dataRequest, messages).toString
    }

    "redirect to error page for POST when extract from user answers fails" in {
      val request =
        FakeRequest(POST, lastYearPayRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", validAnswer.value.toString()))

      val result = controller().onSubmit(1)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
    }

    "redirect to the next page when valid data is submitted" when {

      "No existing data is present" in {
        val request =
          FakeRequest(POST, lastYearPayRoute).withCSRFToken
            .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
            .withFormUrlEncodedBody(("value", validAnswer.value.toString()))

        val result = controller(Some(variableMonthlyUserAnswers)).onSubmit(1)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/last-year-pay/2"
      }

      "Existing data is present" in {
        val request =
          FakeRequest(POST, lastYearPayRoute).withCSRFToken
            .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
            .withFormUrlEncodedBody(("value", validAnswer.value.toString()))

        val result = controller(Some(variableMonthlyUserAnswers)).onSubmit(1)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/last-year-pay/2"
      }
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val request =
        FakeRequest(POST, lastYearPayRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))
      val dataRequest = DataRequest(request, variableMonthlyUserAnswers.id, variableMonthlyUserAnswers)
      val result = controller(Some(variableMonthlyUserAnswers)).onSubmit(1)(request)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm, 1, period("2019, 3, 1", "2019, 3, 31"))(dataRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val request = FakeRequest(GET, lastYearPayRoute)
      val result = controller(None).onPageLoad(1)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val request =
        FakeRequest(POST, lastYearPayRoute)
          .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = controller(None).onSubmit(1)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
