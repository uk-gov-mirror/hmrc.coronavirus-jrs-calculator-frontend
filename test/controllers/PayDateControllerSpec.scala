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

import base.SpecBaseWithApplication
import forms.PayDateFormProvider
import models.UserAnswers
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{ClaimPeriodStartPage, FurloughStartDatePage, PayDatePage}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.PayDateView

import scala.concurrent.Future

class PayDateControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  lazy val payDateRoute = routes.PayDateController.onPageLoad(1).url
  lazy val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, payDateRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val formProvider = new PayDateFormProvider()
  val claimStartDate = LocalDate.of(2020, 3, 5)
  val furloughStartDate = LocalDate.of(2020, 3, 5)
  val userAnswersWithStartDate = UserAnswers(userAnswersId)
    .set(ClaimPeriodStartPage, claimStartDate)
    .success
    .value
    .set(FurloughStartDatePage, furloughStartDate)
    .success
    .value
  val validAnswer = LocalDate.of(2020, 3, 3)

  def onwardRoute = Call("GET", "/foo")

  def postRequest(date: LocalDate, route: String = payDateRoute): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, route).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(
        "value.day"   -> date.getDayOfMonth.toString,
        "value.month" -> date.getMonthValue.toString,
        "value.year"  -> date.getYear.toString
      )

  private def form = formProvider()

  "PayDate Controller" must {

    "return OK and the correct view for a GET" when {

      "furlough start date is the same as claim start date" in {
        val application = applicationBuilder(userAnswers = Some(userAnswersWithStartDate)).build()

        val result = route(application, getRequest).value

        val view = application.injector.instanceOf[PayDateView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, 1, claimStartDate)(getRequest, messages).toString

        application.stop()
      }

      "furlough start date is before the claim start date" in {
        val modifiedUserAnswers = userAnswersWithStartDate
          .set(FurloughStartDatePage, claimStartDate.minusDays(1))
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(modifiedUserAnswers)).build()

        val result = route(application, getRequest).value

        val view = application.injector.instanceOf[PayDateView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, 1, claimStartDate)(getRequest, messages).toString

        application.stop()
      }

      "furlough start date is after the claim start date" in {
        val modifiedUserAnswers = userAnswersWithStartDate
          .set(FurloughStartDatePage, claimStartDate.plusDays(1))
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(modifiedUserAnswers)).build()

        val result = route(application, getRequest).value

        val view = application.injector.instanceOf[PayDateView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, 1, claimStartDate.plusDays(1))(getRequest, messages).toString

        application.stop()
      }

    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = userAnswersWithStartDate.set(PayDatePage, validAnswer, Some(1)).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[PayDateView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), 1, claimStartDate)(getRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswersWithStartDate))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val result = route(application, postRequest(validAnswer)).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" when {

      "data does not bind" in {
        val application = applicationBuilder(userAnswers = Some(userAnswersWithStartDate)).build()

        val request =
          FakeRequest(POST, payDateRoute).withCSRFToken
            .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[PayDateView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, 1, claimStartDate)(request, messages).toString

        application.stop()
      }

      "first date is not before effective start date (claim start date = furlough start date)" in {
        val dateBeforeStart = claimStartDate.plusDays(1)

        val application = applicationBuilder(userAnswers = Some(userAnswersWithStartDate)).build()

        val request = postRequest(dateBeforeStart)

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        application.stop()
      }

      "first date is not before effective start date (claim start date is before furlough start date)" in {
        val dateBeforeStart = LocalDate.of(2020, 3, 2).plusDays(1)

        val modifiedUserAnswers = userAnswersWithStartDate
          .set(ClaimPeriodStartPage, LocalDate.of(2020, 3, 1))
          .success
          .value
          .set(FurloughStartDatePage, LocalDate.of(2020, 3, 2))
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(modifiedUserAnswers)).build()

        val request = postRequest(dateBeforeStart)

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        application.stop()
      }

      "first date is not before effective start date (claim start date is after furlough start date)" in {
        val dateBeforeStart = LocalDate.of(2020, 3, 2).plusDays(1)

        val modifiedUserAnswers = userAnswersWithStartDate
          .set(ClaimPeriodStartPage, LocalDate.of(2020, 3, 2))
          .success
          .value
          .set(FurloughStartDatePage, LocalDate.of(2020, 3, 1))
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(modifiedUserAnswers)).build()

        val request = postRequest(dateBeforeStart)

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        application.stop()
      }

      "second date is not in claim period" in {
        val dateBeforePrevious = LocalDate.of(2020, 3, 2)

        val userAnswers = userAnswersWithStartDate
          .set(PayDatePage, LocalDate.of(2020, 3, 3), Some(1))
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = postRequest(dateBeforePrevious, routes.PayDateController.onPageLoad(2).url)

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        application.stop()
      }

    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, postRequest(validAnswer)).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
