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
import forms.LastPayDateFormProvider
import models.UserAnswers
import models.requests.DataRequest
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{LastPayDatePage, PayDatePage}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.LastPayDateView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LastPayDateControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

  val formProvider = new LastPayDateFormProvider()
  //TODO This should be a date from user answers based on pay date loop
  private def form = formProvider(LocalDate.now())

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = LocalDate.now()

  lazy val lastPayDateRoute = routes.LastPayDateController.onPageLoad().url

  val userAnswers = UserAnswers(userAnswersId).set(PayDatePage, validAnswer, Some(1)).success.value

  lazy val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, lastPayDateRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  lazy val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, lastPayDateRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(
        "value.day"   -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year"  -> validAnswer.getYear.toString
      )

  val view = app.injector.instanceOf[LastPayDateView]

  val controller = new LastPayDateController(
    messagesApi,
    mockSessionRepository,
    navigator,
    identifier,
    dataRetrieval,
    dataRequired,
    formProvider,
    component,
    view)

  "LastPayDate Controller" must {

    "return OK and the correct view for a GET" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))

      val result = controller.onPageLoad()(getRequest)

      status(result) mustEqual OK

      val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(form, validAnswer)(dataRequest, messages).toString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswersUpdated = userAnswers.set(LastPayDatePage, LocalDate.now()).success.value

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswersUpdated))

      val result = controller.onPageLoad()(getRequest)

      status(result) mustEqual OK

      val dataRequest = DataRequest(getRequest, userAnswersUpdated.id, userAnswersUpdated)

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), validAnswer)(dataRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))

      val result = controller.onSubmit()(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/pay-method"
    }

    "redirect to the /pay-date/1 when there is no pay-dates saved already in mongo" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(emptyUserAnswers))
      val result = controller.onSubmit()(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.PayDateController.onPageLoad(1).url
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))

      val request =
        FakeRequest(POST, lastPayDateRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller.onSubmit()(request)

      status(result) mustEqual BAD_REQUEST

      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(boundForm, validAnswer)(dataRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)

      val result = controller.onPageLoad()(getRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)

      val result = controller.onSubmit()(postRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
