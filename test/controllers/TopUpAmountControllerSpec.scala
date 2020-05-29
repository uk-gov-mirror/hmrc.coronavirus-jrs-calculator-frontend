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

import base.{CoreTestDataBuilder, SpecBaseWithApplication}
import controllers.actions.FeatureFlag.TopUpJourneyFlag
import forms.TopUpAmountFormProvider
import models.requests.DataRequest
import models.{Amount, TopUpPayment, TopUpPeriod, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.TopUpAmountView

import scala.concurrent.Future

class TopUpAmountControllerSpec extends SpecBaseWithApplication with MockitoSugar with CoreTestDataBuilder {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new TopUpAmountFormProvider()
  val form = formProvider()

  def topUpAmountRoute(idx: Int) = routes.TopUpAmountController.onPageLoad(idx).url
  def getRequest(method: String, idx: Int) =
    FakeRequest(method, topUpAmountRoute(idx)).withCSRFToken.asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  "TopUpAmount Controller" must {

    "return OK and the correct view for a GET" in {
      val topUpPeriod = TopUpPeriod(LocalDate.of(2020, 3, 31), Amount(100))

      val userAnswers = mandatoryAnswersOnRegularMonthly.withTopUpPeriods(List(topUpPeriod))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = getRequest(GET, 1)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TopUpAmountView]

      status(result) mustEqual OK

      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(form, topUpPeriod, 1)(dataRequest, messages).toString

      application.stop()
    }

    "redirect to error page for GET when index is not valid" when {

      "index is negative" in {
        val topUpPeriod = TopUpPeriod(LocalDate.of(2020, 3, 31), Amount(100))

        val userAnswers = mandatoryAnswersOnRegularMonthly.withTopUpPeriods(List(topUpPeriod))

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val result = route(application, getRequest(GET, -1)).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url

        application.stop()
      }

      "index is 0" in {
        val topUpPeriod = TopUpPeriod(LocalDate.of(2020, 3, 31), Amount(100))

        val userAnswers = mandatoryAnswersOnRegularMonthly.withTopUpPeriods(List(topUpPeriod))

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val result = route(application, getRequest(GET, 0)).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url

        application.stop()
      }

      "index is too high" in {
        val topUpPeriod = TopUpPeriod(LocalDate.of(2020, 3, 31), Amount(100))

        val userAnswers = mandatoryAnswersOnRegularMonthly.withTopUpPeriods(List(topUpPeriod))

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val result = route(application, getRequest(GET, 3)).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url

        application.stop()
      }
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val topUpAmount = TopUpPayment(LocalDate.of(2020, 3, 31), Amount(25))

      val topUpPeriod = TopUpPeriod(LocalDate.of(2020, 3, 31), Amount(100))

      val userAnswers = mandatoryAnswersOnRegularMonthly
        .withTopUpPeriods(List(topUpPeriod))
        .withTopUpAmount(topUpAmount, Some(1))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = getRequest(GET, 1)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TopUpAmountView]

      status(result) mustEqual OK

      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(form.fill(Amount(25)), topUpPeriod, 1)(dataRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val topUpPeriod = TopUpPeriod(LocalDate.of(2020, 3, 31), Amount(100))

      val userAnswers = mandatoryAnswersOnRegularMonthly.withTopUpPeriods(List(topUpPeriod))

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request =
        getRequest(POST, 1)
          .withFormUrlEncodedBody(("value", "100.00"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val topUpPeriod = TopUpPeriod(LocalDate.of(2020, 3, 31), Amount(100))

      val userAnswers = mandatoryAnswersOnRegularMonthly.withTopUpPeriods(List(topUpPeriod))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        getRequest(POST, 1).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[TopUpAmountView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(boundForm, topUpPeriod, 1)(dataRequest, messages).toString

      application.stop()
    }

    "redirect to 404 page for a GET if topups flag is disabled" in {

      val application = applicationBuilder(config = Map(TopUpJourneyFlag.key -> false), userAnswers = Some(UserAnswers("id"))).build()

      val request = getRequest(GET, 1)

      val result = route(application, request).value

      status(result) mustEqual NOT_FOUND

      application.stop()
    }

    "redirect to 404 page for a POST if topups flag is disabled" in {

      val application = applicationBuilder(config = Map(TopUpJourneyFlag.key -> false), userAnswers = Some(UserAnswers("id"))).build()

      val request =
        getRequest(POST, 1)
          .withFormUrlEncodedBody(("value", "100.00"))

      val result = route(application, request).value

      status(result) mustEqual NOT_FOUND

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = getRequest(GET, 1)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        getRequest(POST, 1)
          .withFormUrlEncodedBody(("value", "100.00"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
