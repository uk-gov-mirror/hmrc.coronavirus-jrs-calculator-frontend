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
import forms.PartTimeHoursFormProvider
import models.FurloughStatus.FurloughOngoing
import models.PayMethod.Regular
import models.PaymentFrequency.Monthly
import models.requests.DataRequest
import models.{FullPeriod, Hours, PartTimeHours, Periods, UsualHours}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.PartTimeHoursPage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.PartTimeHoursView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PartTimeHoursControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

  private val formProvider = new PartTimeHoursFormProvider()
  private val fullPeriodOne: FullPeriod = fullPeriod("2020,3,1", "2020,3,31")
  private val fullPeriodTwo: FullPeriod = fullPeriod("2020,4,1", "2020,4,30")

  private val usuals: Seq[UsualHours] =
    Seq(UsualHours(fullPeriodOne.period.end, Hours(160.0)), UsualHours(fullPeriodTwo.period.end, Hours(160.0)))
  val form = formProvider(usuals, fullPeriodOne)

  def partTimeHoursRoute(idx: Int) = routes.PartTimeHoursController.onPageLoad(idx).url
  val partTimePeriods: List[Periods] = List(fullPeriodOne, fullPeriodTwo)

  val userAnswers = emptyUserAnswers
    .withClaimPeriodStart("2020, 3, 1")
    .withClaimPeriodEnd("2020, 4, 30")
    .withPaymentFrequency(Monthly)
    .withPayMethod(Regular)
    .withFurloughStatus(FurloughOngoing)
    .withFurloughStartDate("2020, 3, 1")
    .withLastPayDate("2020, 3, 31")
    .withPayDate(List("2020, 2, 29", "2020, 3, 31", "2020, 4, 30"))
    .withRegularPayAmount(2000)
    .withPartTimePeriods(partTimePeriods)

  val endDates: List[LocalDate] = partTimePeriods.map(_.period.end)

  val period: Periods = fullPeriodOne

  def buildRequest(method: String, idx: Int) =
    FakeRequest(method, partTimeHoursRoute(idx)).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val view = app.injector.instanceOf[PartTimeHoursView]

  val controller = new PartTimeHoursController(
    messagesApi,
    mockSessionRepository,
    navigator,
    identifier,
    dataRetrieval,
    dataRequired,
    formProvider,
    component,
    view)

  "PartTimeHours Controller" must {
    "return OK and the correct view for a GET" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val request = buildRequest("GET", 1)
      val result = controller.onPageLoad(1)(request)
      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form, period, 1)(dataRequest, messages).toString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val date = LocalDate.of(2020, 3, 31)
      val preValue = PartTimeHours(date, Hours(10.5))
      val updatedAnswers = userAnswers.set(PartTimeHoursPage, preValue, Some(1)).success.value
      val request = buildRequest("GET", 1)
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(updatedAnswers))
      val result = controller.onPageLoad(1)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(preValue.hours), period, 1)(request, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val request =
        buildRequest(POST, 1)
          .withFormUrlEncodedBody(("value", "10.00"))

      val result = controller.onSubmit(1)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/part-time-normal-hours/2"
    }

    "redirect to error page for GET when index is not valid" when {

      "index is negative" in {
        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
        val request = buildRequest("GET", -1)
        val result = controller.onPageLoad(-1)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
      }

      "index is 0" in {
        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
        val request = buildRequest("GET", 0)
        val result = controller.onPageLoad(0)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
      }

      "index is too high" in {
        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
        val request = buildRequest("GET", 4)
        val result = controller.onPageLoad(4)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
      }
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
      val request =
        buildRequest(POST, 1)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))
      val result = controller.onSubmit(1)(request)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, period, 1)(request, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request = buildRequest("GET", 1)
      val result = controller.onPageLoad(1)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request =
        buildRequest(POST, 1)
          .withFormUrlEncodedBody(("xxxx", "value 1"), ("xxxxx", "value 2"))

      val result = controller.onSubmit(1)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
