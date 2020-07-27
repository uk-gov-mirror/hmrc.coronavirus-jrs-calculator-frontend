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
import forms.TopUpPeriodsFormProvider
import models.FurloughStatus.FurloughOngoing
import models.PayMethod.Regular
import models.PaymentFrequency.Monthly
import models.requests.DataRequest
import models.{Amount, FullPeriodCap, FurloughBreakdown, TopUpPeriod}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TopUpPeriodsView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TopUpPeriodsControllerSpec extends SpecBaseControllerSpecs with MockitoSugar with CoreTestDataBuilder {

  lazy val topupPeriodsRoute = routes.TopUpPeriodsController.onPageLoad().url

  lazy val getRequest = FakeRequest(GET, topupPeriodsRoute).withCSRFToken
    .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
  val formProvider = new TopUpPeriodsFormProvider()
  val form = formProvider()

  val dates = List(LocalDate.of(2020, 3, 31), LocalDate.of(2020, 4, 30))
  val periodBreakdowns: Seq[FurloughBreakdown] = Seq(
    fullPeriodFurloughBreakdown(
      1600.00,
      regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-31")),
      FullPeriodCap(2500.00)),
    fullPeriodFurloughBreakdown(
      1600.00,
      regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-30")),
      FullPeriodCap(2500.00))
  )

  val baseUserAnswers = emptyUserAnswers
    .withClaimPeriodStart("2020, 3, 1")
    .withClaimPeriodEnd("2020, 4, 30")
    .withPaymentFrequency(Monthly)
    .withPayMethod(Regular)
    .withFurloughStatus(FurloughOngoing)
    .withFurloughStartDate("2020, 3, 1")
    .withLastPayDate("2020, 3, 31")
    .withPayDate(List("2020, 2, 29", "2020, 3, 31", "2020, 4, 30"))
    .withRegularPayAmount(2000)

  val view = app.injector.instanceOf[TopUpPeriodsView]

  val controller = new TopUpPeriodsController(
    messagesApi,
    mockSessionRepository,
    navigator,
    identifier,
    dataRetrieval,
    dataRequired,
    formProvider,
    component,
    view)

  "TopupPeriods Controller" must {

    "return OK and the correct view for a GET" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(baseUserAnswers))
      val result = controller.onPageLoad()(getRequest)
      val dataRequest = DataRequest(getRequest, baseUserAnswers.id, baseUserAnswers)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, periodBreakdowns)(dataRequest, messages).toString
    }

    "redirect for a GET when there is only one period to top up" in {
      val userAnswers = emptyUserAnswers
        .withClaimPeriodStart("2020, 3, 1")
        .withClaimPeriodEnd("2020, 3, 31")
        .withPaymentFrequency(Monthly)
        .withPayMethod(Regular)
        .withFurloughStatus(FurloughOngoing)
        .withFurloughStartDate("2020, 3, 1")
        .withLastPayDate("2020, 3, 31")
        .withPayDate(List("2020, 2, 29", "2020, 3, 31"))
        .withRegularPayAmount(2000)

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val result = controller.onPageLoad()(getRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe "/job-retention-scheme-calculator/topup-pay-amount/1"
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val topUpPeriod = dates.map(TopUpPeriod(_, furloughGrant = Amount(100)))
      val userAnswers = baseUserAnswers.withTopUpPeriods(topUpPeriod)
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val result = controller.onPageLoad()(getRequest)
      val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(dates), periodBreakdowns)(dataRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(baseUserAnswers))
      val request =
        FakeRequest(POST, topupPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]" -> dates.head.toString()))

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/topup-pay-amount/1"
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(baseUserAnswers))
      val request =
        FakeRequest(POST, topupPeriodsRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value[0]", "invalid value"))

      val boundForm = form.bind(Map("value[0]" -> "invalid value"))
      val result = controller.onSubmit()(request)
      val dataRequest = DataRequest(request, baseUserAnswers.id, baseUserAnswers)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm, periodBreakdowns)(dataRequest, messages).toString
    }

    "redirect to error page for a GET if missing values for furlough pay calculation" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(emptyUserAnswers))
      val request =
        FakeRequest(GET, topupPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]", dates.head.toString))
      val result = controller.onPageLoad()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
    }

    "redirect to error page for a POST if missing values for furlough pay calculation" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(emptyUserAnswers))
      val request =
        FakeRequest(POST, topupPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]", dates.head.toString))

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
    }

    "redirect to error page for a POST if dates in furlough and input do not align" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(baseUserAnswers))
      val request =
        FakeRequest(POST, topupPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]", dates(0).toString), ("value[1]", dates(1).toString), ("value[2]", "2020-05-30"))

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request = FakeRequest(GET, topupPeriodsRoute)
      val result = controller.onPageLoad()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request =
        FakeRequest(POST, topupPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]", dates.head.toString))

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
