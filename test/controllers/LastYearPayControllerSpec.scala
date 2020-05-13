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
import forms.LastYearPayFormProvider
import models.{Amount, CylbPayment}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import pages.LastYearPayPage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.LastYearPayView

import scala.concurrent.Future

class LastYearPayControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  val formProvider = new LastYearPayFormProvider()
  val form = formProvider()

  def onwardRoute = Call("GET", "/foo")

  val variableMonthlyUserAnswers = variableMonthlyPartial

  val validAnswer = Amount(BigDecimal(100))

  val validDate = LocalDate.of(2019, 3, 1)

  lazy val lastYearPayRoute = routes.LastYearPayController.onPageLoad(1).url

  lazy val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, lastYearPayRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  def getRequest(idx: Int) =
    FakeRequest(GET, routes.LastYearPayController.onPageLoad(idx).url).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  "LastYearPay Controller" must {

    "redirect to error page for GET when extract from user answers fails" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url

      application.stop()
    }

    "redirect to error page for GET when index is not valid" when {

      "index is negative" in {
        val application = applicationBuilder(userAnswers = Some(variableMonthlyUserAnswers)).build()

        val result = route(application, getRequest(-1)).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url

        application.stop()
      }

      "index is 0" in {
        val application = applicationBuilder(userAnswers = Some(variableMonthlyUserAnswers)).build()

        val result = route(application, getRequest(0)).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url

        application.stop()
      }

      "index is too high" in {
        val application = applicationBuilder(userAnswers = Some(variableMonthlyUserAnswers)).build()

        val result = route(application, getRequest(3)).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url

        application.stop()
      }
    }

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(variableMonthlyUserAnswers)).build()

      val request = getRequest(1)

      val result = route(application, request).value

      val view = application.injector.instanceOf[LastYearPayView]

      val expectedView = view(form, 1, LocalDate.of(2019, 3, 20), true)(request, messages).toString

      status(result) mustEqual OK

      contentAsString(result) mustEqual expectedView

      application.stop()
    }

    "redirect GET to coming soon if variable journey feature is disabled" in {

      val application = applicationBuilder(userAnswers = Some(variableMonthlyUserAnswers), Map("variable.journey.enabled" -> false)).build()

      val request = getRequest(1)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ComingSoonController.onPageLoad().url

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = variableMonthlyUserAnswers.set(LastYearPayPage, CylbPayment(validDate, validAnswer)).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[LastYearPayView]

      val request = getRequest(1)

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), 1, LocalDate.of(2019, 3, 20), true)(request, messages).toString

      application.stop()
    }

    "redirect to error page for POST when extract from user answers fails" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request =
        FakeRequest(POST, lastYearPayRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", validAnswer.value.toString()))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url

      application.stop()
    }

    "redirect to the next page when valid data is submitted" when {

      "No existing data is present" in {
        val mockSessionRepository = mock[SessionRepository]

        when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

        val application =
          applicationBuilder(userAnswers = Some(variableMonthlyUserAnswers))
            .overrides(
              bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
              bind[SessionRepository].toInstance(mockSessionRepository)
            )
            .build()

        val request =
          FakeRequest(POST, lastYearPayRoute).withCSRFToken
            .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
            .withFormUrlEncodedBody(("value", validAnswer.value.toString()))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url

        application.stop()
      }

      "Existing data is present" in {
        val mockSessionRepository = mock[SessionRepository]

        when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

        val application =
          applicationBuilder(userAnswers = Some(variableMonthlyUserAnswers))
            .overrides(
              bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
              bind[SessionRepository].toInstance(mockSessionRepository)
            )
            .build()

        val request =
          FakeRequest(POST, lastYearPayRoute).withCSRFToken
            .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
            .withFormUrlEncodedBody(("value", validAnswer.value.toString()))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url

        application.stop()
      }

    }

    "redirect POST to coming soon if variable journey feature is disabled" in {
      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(variableMonthlyUserAnswers), Map("variable.journey.enabled" -> false))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request =
        FakeRequest(POST, lastYearPayRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", validAnswer.value.toString()))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ComingSoonController.onPageLoad().url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(variableMonthlyUserAnswers)).build()

      val request =
        FakeRequest(POST, lastYearPayRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[LastYearPayView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, 1, LocalDate.of(2019, 3, 20), true)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, lastYearPayRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, lastYearPayRoute)
          .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
