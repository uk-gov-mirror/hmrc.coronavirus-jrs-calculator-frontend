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
import forms.FurloughPartialPayFormProvider
import models.PaymentFrequency.Weekly
import models.requests.DataRequest
import models.{FurloughPartialPay, UserAnswers}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.VariableLengthPartialPayView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PartialPayBeforeFurloughControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

  val formProvider = new FurloughPartialPayFormProvider()
  val form = formProvider()

  lazy val pageLoadBeforeFurloughRoute = routes.PartialPayBeforeFurloughController.onPageLoad().url

  lazy val submitBeforeFurloughRoute = routes.PartialPayBeforeFurloughController.onSubmit().url

  def getRequest(url: String): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, url).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  def postRequest(url: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, url).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(("value", "123"))

  val payPeriod1 = LocalDate.of(2020, 3, 22)
  val payPeriod2 = LocalDate.of(2020, 3, 29)
  val payPeriod3 = LocalDate.of(2020, 4, 5)
  val furloughStartDate = LocalDate.of(2020, 3, 27)
  val claimPeriodEnd = LocalDate.of(2020, 4, 4)
  val claimPeriodStart = LocalDate.of(2020, 3, 27)

  val userAnswers = UserAnswers(userAnswersId)
    .withPayDate(List(payPeriod1, payPeriod2, payPeriod3).map(_.toString))
    .withClaimPeriodStart(claimPeriodStart.toString)
    .withClaimPeriodEnd(claimPeriodEnd.toString)
    .withFurloughStartDate(furloughStartDate.toString)
    .withPaymentFrequency(Weekly)

  val view = app.injector.instanceOf[VariableLengthPartialPayView]

  val controller = new PartialPayBeforeFurloughController(
    messagesApi,
    mockSessionRepository,
    navigator,
    identifier,
    dataRetrieval,
    dataRequired,
    formProvider,
    component,
    view)

  "PartialPayBeforeFurloughController" must {

    "return OK and the correct view for a GET" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val request = getRequest(pageLoadBeforeFurloughRoute)
      val result = controller.onPageLoad()(request)
      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form, LocalDate.of(2020, 3, 23), LocalDate.of(2020, 3, 26), routes.PartialPayBeforeFurloughController.onSubmit())(
          dataRequest,
          messages).toString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val answers = userAnswers.withPartialPayBeforeFurlough(111)
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(answers))
      val request = getRequest(pageLoadBeforeFurloughRoute)
      val result = controller.onPageLoad()(request)
      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(
          form.fill(FurloughPartialPay(111)),
          LocalDate.of(2020, 3, 23),
          LocalDate.of(2020, 3, 26),
          routes.PartialPayBeforeFurloughController.onSubmit()
        )(dataRequest, messages).toString
    }

    "redirect to something went wrong when there no saved data for PayDatePage in mongo for GET" in {
      val modifiedUserAnswers = userAnswers.remove(PayDatePage).success.value
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(modifiedUserAnswers))
      val result = controller.onPageLoad()(getRequest(submitBeforeFurloughRoute))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
    }

    "redirect to something went wrong when there no saved data for FurloughStartDate in mongo for GET" in {
      val modifiedUserAnswers = userAnswers.remove(FurloughStartDatePage).success.value
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(modifiedUserAnswers))
      val result = controller.onPageLoad()(getRequest(submitBeforeFurloughRoute))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
    }

    "redirect to the next page when valid data is submitted" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val result = controller.onSubmit()(postRequest(submitBeforeFurloughRoute))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/variable-pay-partial-pay-after-furlough"
    }

    "redirect to something went wrong when there no saved data for PayDatePage in mongo for POST" in {
      val modifiedUserAnswers = userAnswers.remove(PayDatePage).success.value
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(modifiedUserAnswers))
      val result = controller.onSubmit()(postRequest(submitBeforeFurloughRoute))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
    }

    "redirect to something went wrong when there no saved data for FurloughStartDate in mongo for POST" in {
      val modifiedUserAnswers = userAnswers.remove(FurloughStartDatePage).success.value
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(modifiedUserAnswers))
      val result = controller.onSubmit()(postRequest(submitBeforeFurloughRoute))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val request =
        FakeRequest(POST, submitBeforeFurloughRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))
      val result = controller.onSubmit()(request)
      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm, LocalDate.of(2020, 3, 23), LocalDate.of(2020, 3, 26), routes.PartialPayBeforeFurloughController.onSubmit())(
          dataRequest,
          messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val result = controller.onPageLoad()(FakeRequest(GET, pageLoadBeforeFurloughRoute))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request =
        FakeRequest(POST, submitBeforeFurloughRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
