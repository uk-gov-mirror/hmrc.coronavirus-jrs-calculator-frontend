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

import base.{CoreTestDataBuilder, SpecBaseControllerSpecs}
import forms.TopUpAmountFormProvider
import models.requests.DataRequest
import models.{Amount, TopUpPayment, TopUpPeriod}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TopUpAmountView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TopUpAmountControllerSpec extends SpecBaseControllerSpecs with MockitoSugar with CoreTestDataBuilder {

  val formProvider = new TopUpAmountFormProvider()
  val form = formProvider()

  def topUpAmountRoute(idx: Int) = routes.TopUpAmountController.onPageLoad(idx).url
  def getRequest(method: String, idx: Int) =
    FakeRequest(method, topUpAmountRoute(idx)).withCSRFToken.asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val view = app.injector.instanceOf[TopUpAmountView]

  val controller = new TopUpAmountController(
    messagesApi,
    mockSessionRepository,
    navigator,
    identifier,
    dataRetrieval,
    dataRequired,
    formProvider,
    component,
    view)

  "TopUpAmount Controller" must {

    "return OK and the correct view for a GET" in {
      val topUpPeriod = TopUpPeriod(LocalDate.of(2020, 3, 31), Amount(100))
      val userAnswers = mandatoryAnswersOnRegularMonthly.withTopUpPeriods(List(topUpPeriod))
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val request = getRequest(GET, 1)
      val result = controller.onPageLoad(1)(request)
      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form, topUpPeriod, 1)(dataRequest, messages).toString
    }

    "redirect to error page for GET when index is not valid" when {

      "index is negative" in {
        val topUpPeriod = TopUpPeriod(LocalDate.of(2020, 3, 31), Amount(100))
        val userAnswers = mandatoryAnswersOnRegularMonthly.withTopUpPeriods(List(topUpPeriod))
        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
        val result = controller.onPageLoad(-1)(getRequest(GET, -1))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
      }

      "index is 0" in {
        val topUpPeriod = TopUpPeriod(LocalDate.of(2020, 3, 31), Amount(100))
        val userAnswers = mandatoryAnswersOnRegularMonthly.withTopUpPeriods(List(topUpPeriod))
        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
        val result = controller.onPageLoad(0)(getRequest(GET, 0))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
      }

      "index is too high" in {
        val topUpPeriod = TopUpPeriod(LocalDate.of(2020, 3, 31), Amount(100))
        val userAnswers = mandatoryAnswersOnRegularMonthly.withTopUpPeriods(List(topUpPeriod))
        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
        val result = controller.onPageLoad(3)(getRequest(GET, 3))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
      }
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val topUpAmount = TopUpPayment(LocalDate.of(2020, 3, 31), Amount(25))
      val topUpPeriod = TopUpPeriod(LocalDate.of(2020, 3, 31), Amount(100))

      val userAnswers = mandatoryAnswersOnRegularMonthly
        .withTopUpPeriods(List(topUpPeriod))
        .withTopUpAmount(topUpAmount, Some(1))

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val request = getRequest(GET, 1)
      val result = controller.onPageLoad(1)(request)
      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(Amount(25)), topUpPeriod, 1)(dataRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {
      val topUpPeriod = TopUpPeriod(LocalDate.of(2020, 3, 31), Amount(100))
      val userAnswers = mandatoryAnswersOnRegularMonthly.withTopUpPeriods(List(topUpPeriod))
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val request =
        getRequest(POST, 1)
          .withFormUrlEncodedBody(("value", "100.00"))

      val result = controller.onSubmit(1)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/additional-pay-question"
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val topUpPeriod = TopUpPeriod(LocalDate.of(2020, 3, 31), Amount(100))
      val userAnswers = mandatoryAnswersOnRegularMonthly.withTopUpPeriods(List(topUpPeriod))
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val request =
        getRequest(POST, 1).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))
      val result = controller.onSubmit(1)(request)
      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm, topUpPeriod, 1)(dataRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request = getRequest(GET, 1)
      val result = controller.onPageLoad(1)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request =
        getRequest(POST, 1)
          .withFormUrlEncodedBody(("value", "100.00"))

      val result = controller.onSubmit(1)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
