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
import forms.FurloughEndDateFormProvider
import models.Period
import models.requests.DataRequest
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage, FurloughEndDatePage, FurloughStartDatePage}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.FurloughEndDateView

import scala.concurrent.Future

class FurloughEndDateControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  val formProvider = new FurloughEndDateFormProvider(frontendAppConfig)
  private val claimPeriodStart = LocalDate.of(2020, 3, 1)
  private val claimPeriodEnd = LocalDate.of(2020, 5, 1)
  private val furloughStart = LocalDate.of(2020, 4, 1)
  private def form = formProvider(Period(claimPeriodStart, claimPeriodEnd), furloughStart)

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = furloughStart.plusDays(21)

  lazy val furloughEndDateRoute = routes.FurloughEndDateController.onPageLoad().url

  val userAnswersWithClaimStartAndEnd = emptyUserAnswers
    .set(ClaimPeriodStartPage, claimPeriodStart)
    .success
    .value
    .set(ClaimPeriodEndPage, claimPeriodEnd)
    .success
    .value
    .set(FurloughStartDatePage, furloughStart)
    .success
    .value

  lazy val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, furloughEndDateRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  lazy val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, furloughEndDateRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(
        "value.day"   -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year"  -> validAnswer.getYear.toString
      )

  "FurloughEndDate Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswersWithClaimStartAndEnd)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[FurloughEndDateView]

      status(result) mustEqual OK

      val dataRequest = DataRequest(getRequest, userAnswersWithClaimStartAndEnd.id, userAnswersWithClaimStartAndEnd)

      contentAsString(result) mustEqual
        view(form)(dataRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = userAnswersWithClaimStartAndEnd.set(FurloughEndDatePage, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[FurloughEndDateView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(form.fill(validAnswer))(dataRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswersWithClaimStartAndEnd))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val result = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(userAnswersWithClaimStartAndEnd)).build()

      val request =
        FakeRequest(POST, furloughEndDateRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[FurloughEndDateView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      val dataRequest = DataRequest(request, userAnswersWithClaimStartAndEnd.id, userAnswersWithClaimStartAndEnd)

      contentAsString(result) mustEqual
        view(boundForm)(dataRequest, messages).toString

      application.stop()
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

      val result = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
