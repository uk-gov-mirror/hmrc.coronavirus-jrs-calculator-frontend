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
import forms.AnnualPayAmountFormProvider
import models.EmployeeStarted.{After1Feb2019, OnOrBefore1Feb2019}
import models.requests.DataRequest
import models.{AnnualPayAmount, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{AnnualPayAmountPage, EmployeeStartDatePage, EmployeeStartedPage, FurloughStartDatePage}
import play.api.inject.bind
import play.api.libs.json.{JsString, Json}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.AnnualPayAmountView

import scala.concurrent.Future

class AnnualPayAmountControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new AnnualPayAmountFormProvider()
  val form = formProvider()

  lazy val annualPayAmountRoute = routes.AnnualPayAmountController.onPageLoad().url

  val furloughStart = LocalDate.parse("2020-04-01")
  val uiDateToShow = furloughStart.minusDays(1)
  val empStart = LocalDate.parse("2020-02-01")

  lazy val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, annualPayAmountRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  lazy val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, annualPayAmountRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(("value", "123"))

  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(
      FurloughStartDatePage.toString -> JsString(furloughStart.toString),
      EmployeeStartDatePage.toString -> JsString(empStart.toString),
      EmployeeStartedPage.toString   -> JsString(After1Feb2019.toString)
    )
  )

  "AnnualPayAmountController" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[AnnualPayAmountView]

      status(result) mustEqual OK

      val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(form, uiDateToShow, After1Feb2019)(dataRequest, messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET if the EmployeeStarted is OnOrBefore1Feb2019" in {

      val updatedAnswers = userAnswers.set(EmployeeStartedPage, OnOrBefore1Feb2019).success.value
      val application = applicationBuilder(userAnswers = Some(updatedAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[AnnualPayAmountView]

      status(result) mustEqual OK

      val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(form, uiDateToShow, OnOrBefore1Feb2019)(dataRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswersUpdated = userAnswers.set(AnnualPayAmountPage, AnnualPayAmount(111)).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswersUpdated)).build()

      val view = application.injector.instanceOf[AnnualPayAmountView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      val dataRequest = DataRequest(getRequest, userAnswersUpdated.id, userAnswersUpdated)

      contentAsString(result) mustEqual
        view(form.fill(AnnualPayAmount(111)), uiDateToShow, After1Feb2019)(dataRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
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

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, annualPayAmountRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[AnnualPayAmountView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(boundForm, uiDateToShow, After1Feb2019)(dataRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, annualPayAmountRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, annualPayAmountRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
