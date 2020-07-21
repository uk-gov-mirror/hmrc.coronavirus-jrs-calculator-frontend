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
import forms.PayPeriodQuestionFormProvider
import models.ClaimPeriodQuestion.ClaimOnSamePeriod
import models.FurloughPeriodQuestion.FurloughedOnSamePeriod
import models.FurloughStatus.FurloughOngoing
import models.PayMethod.Regular
import models.PaymentFrequency.Monthly
import models.requests.DataRequest
import models.{PayPeriodQuestion, Period}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.PayPeriodQuestionView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PayPeriodQuestionControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

  private lazy val payPeriodQuestionRoute = routes.PayPeriodQuestionController.onPageLoad().url
  private lazy val payPeriodQuestionRoutePost = routes.PayPeriodQuestionController.onSubmit().url

  val formProvider = new PayPeriodQuestionFormProvider()
  val form = formProvider()

  val baseUserAnswers = emptyUserAnswers
    .withClaimPeriodStart("2020, 3, 1")
    .withClaimPeriodEnd("2020, 4, 30")
    .withClaimPeriodQuestion(ClaimOnSamePeriod)
    .withFurloughPeriodQuestion(FurloughedOnSamePeriod)
    .withPaymentFrequency(Monthly)
    .withPayMethod(Regular)
    .withFurloughStatus(FurloughOngoing)
    .withFurloughStartDate("2020, 3, 1")
    .withLastPayDate("2020, 3, 31")
    .withPayDate(List("2020, 2, 29", "2020, 3, 31", "2020, 4, 30"))

  val payPeriods = Seq(
    Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)),
    Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))
  )

  val view = app.injector.instanceOf[PayPeriodQuestionView]

  val controller = new PayPeriodQuestionController(
    messagesApi,
    mockSessionRepository,
    navigator,
    identifier,
    dataRetrieval,
    dataRequired,
    formProvider,
    component,
    view)

  "PayPeriodQuestion Controller" must {

    "return OK and the correct view for a GET" in {
      val getRequest: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, payPeriodQuestionRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(baseUserAnswers))
      val dataRequest = DataRequest(getRequest, baseUserAnswers.id, baseUserAnswers)
      val result = controller.onPageLoad()(getRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form, payPeriods)(dataRequest, messages).toString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val getRequest: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, payPeriodQuestionRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

      val userAnswersUpdated = baseUserAnswers.withPayPeriodQuestion(PayPeriodQuestion.values.head)
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswersUpdated))
      val dataRequest = DataRequest(getRequest, userAnswersUpdated.id, userAnswersUpdated)
      val result = controller.onPageLoad()(getRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(PayPeriodQuestion.values.head), payPeriods)(dataRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(baseUserAnswers))
      val request =
        FakeRequest(POST, payPeriodQuestionRoutePost)
          .withFormUrlEncodedBody(("value", PayPeriodQuestion.values.head.toString))

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/pay-method"
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(baseUserAnswers))
      val request =
        FakeRequest(POST, payPeriodQuestionRoutePost).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))
      val dataRequest = DataRequest(request, baseUserAnswers.id, baseUserAnswers)
      val result = controller.onSubmit()(request)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm, payPeriods)(dataRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request = FakeRequest(GET, payPeriodQuestionRoute)
      val result = controller.onPageLoad()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request =
        FakeRequest(POST, payPeriodQuestionRoute)
          .withFormUrlEncodedBody(("value", PayPeriodQuestion.values.head.toString))

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
