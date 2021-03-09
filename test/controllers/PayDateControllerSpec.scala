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

import java.time.LocalDate

import base.SpecBaseControllerSpecs
import forms.PayDateFormProvider
import models.PaymentFrequency.Weekly
import models.requests.DataRequest
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.PayDatePage
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.PayDateView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PayDateControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

  lazy val payDateRoute = routes.PayDateController.onPageLoad(1).url
  lazy val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, payDateRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val formProvider      = new PayDateFormProvider()
  val claimStartDate    = LocalDate.of(2020, 3, 5)
  val furloughStartDate = LocalDate.of(2020, 3, 5)
  val userAnswersWithStartDate = emptyUserAnswers
    .withClaimPeriodStart("2020, 3, 5")
    .withClaimPeriodEnd("2020, 3, 31")
    .withFurloughStartDate("2020, 3, 5")
    .withFurloughStatus()
    .withPaymentFrequency(Weekly)

  val validAnswer = LocalDate.of(2020, 3, 3)

  def postRequest(date: LocalDate, route: String = payDateRoute): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, route).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(
        "value.day"   -> date.getDayOfMonth.toString,
        "value.month" -> date.getMonthValue.toString,
        "value.year"  -> date.getYear.toString
      )

  private def form = formProvider()

  val view = app.injector.instanceOf[PayDateView]

  val controller = new PayDateController(messagesApi,
                                         mockSessionRepository,
                                         navigator,
                                         identifier,
                                         dataRetrieval,
                                         dataRequired,
                                         formProvider,
                                         component,
                                         view)

  "PayDate Controller" must {

    "return OK and the correct view for a GET" when {

      "furlough start date is the same as claim start date" in {
        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswersWithStartDate))
        val result      = controller.onPageLoad(1)(getRequest)
        val dataRequest = DataRequest(getRequest, userAnswersWithStartDate.id, userAnswersWithStartDate)

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(form, 1, claimStartDate)(dataRequest, messages).toString
      }

      "furlough start date is before the claim start date" in {
        val modifiedUserAnswers = userAnswersWithStartDate
          .withFurloughStartDate(claimStartDate.minusDays(1).toString)

        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(modifiedUserAnswers))
        val result      = controller.onPageLoad(1)(getRequest)
        val dataRequest = DataRequest(getRequest, modifiedUserAnswers.id, modifiedUserAnswers)

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(form, 1, claimStartDate)(dataRequest, messages).toString
      }

      "furlough start date is after the claim start date" in {
        val modifiedUserAnswers = userAnswersWithStartDate
          .withFurloughStartDate(claimStartDate.plusDays(1).toString)

        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(modifiedUserAnswers))

        val result      = controller.onPageLoad(1)(getRequest)
        val dataRequest = DataRequest(getRequest, modifiedUserAnswers.id, modifiedUserAnswers)

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(form, 1, claimStartDate.plusDays(1))(dataRequest, messages).toString
      }
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = userAnswersWithStartDate.set(PayDatePage, validAnswer, Some(1)).success.value
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val result      = controller.onPageLoad(1)(getRequest)
      val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(validAnswer), 1, claimStartDate)(dataRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswersWithStartDate))
      val result = controller.onSubmit(1)(postRequest(validAnswer))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/pay-periods-list"
    }

    "generate dates when not monthly" in {
      val userAnswers = emptyUserAnswers
        .withClaimPeriodStart("2020, 7, 1")
        .withClaimPeriodEnd("2020, 7, 31")
        .withFurloughStartDate("2020, 3, 20")
        .withFurloughStatus()
        .withPaymentFrequency(Weekly)
        .withPayMethod()

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val result = controller.onSubmit(1)(postRequest(LocalDate.of(2020, 6, 26)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.PayPeriodsListController.onPageLoad().url
    }

    "return a Bad Request and errors when invalid data is submitted" when {

      "data does not bind" in {
        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswersWithStartDate))
        val request =
          FakeRequest(POST, payDateRoute).withCSRFToken
            .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm   = form.bind(Map("value" -> "invalid value"))
        val result      = controller.onSubmit(1)(request)
        val dataRequest = DataRequest(request, userAnswersWithStartDate.id, userAnswersWithStartDate)

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual
          view(boundForm, 1, claimStartDate)(dataRequest, messages).toString
      }

      "first date is not before effective start date (claim start date = furlough start date)" in {
        val dateBeforeStart = claimStartDate.plusDays(1)
        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswersWithStartDate))
        val request = postRequest(dateBeforeStart)
        val result  = controller.onSubmit(1)(request)

        status(result) mustEqual BAD_REQUEST
      }

      "first date is not before effective start date (claim start date is before furlough start date)" in {
        val dateBeforeStart = LocalDate.of(2020, 3, 2).plusDays(1)
        val modifiedUserAnswers = userAnswersWithStartDate
          .withClaimPeriodStart(LocalDate.of(2020, 3, 1).toString)
          .withFurloughStartDate(LocalDate.of(2020, 3, 2).toString)
        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(modifiedUserAnswers))
        val request = postRequest(dateBeforeStart)
        val result  = controller.onSubmit(1)(request)

        status(result) mustEqual BAD_REQUEST
      }

      "first date is not before effective start date (claim start date is after furlough start date)" in {
        val dateBeforeStart = LocalDate.of(2020, 3, 2).plusDays(1)
        val modifiedUserAnswers = userAnswersWithStartDate
          .withClaimPeriodStart(LocalDate.of(2020, 3, 2).toString)
          .withFurloughStartDate(LocalDate.of(2020, 3, 1).toString)

        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(modifiedUserAnswers))
        val request = postRequest(dateBeforeStart)
        val result  = controller.onSubmit(1)(request)

        status(result) mustEqual BAD_REQUEST
      }

      "second date is not in claim period" in {
        val dateBeforePrevious = LocalDate.of(2020, 3, 2)
        val userAnswers = userAnswersWithStartDate
          .withPayDate(List(LocalDate.of(2020, 3, 3).toString))
        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
        val request = postRequest(dateBeforePrevious, routes.PayDateController.onPageLoad(2).url)
        val result  = controller.onSubmit(2)(request)

        status(result) mustEqual BAD_REQUEST
      }
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val result = controller.onPageLoad(1)(getRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val result = controller.onSubmit(1)(postRequest(validAnswer))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
