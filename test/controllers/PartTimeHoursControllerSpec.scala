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
import forms.PartTimeHoursFormProvider
import models.FurloughStatus.FurloughOngoing
import models.PayMethod.Regular
import models.PaymentFrequency.Monthly
import models.requests.DataRequest
import models.{Hours, PartTimeHours, Periods}
import navigation.{FakeNavigator, Navigator}
import org.scalatestplus.mockito.MockitoSugar
import pages.PartTimeHoursPage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.PartTimeHoursView

class PartTimeHoursControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new PartTimeHoursFormProvider()
  val form = formProvider()

  def partTimeHoursRoute(idx: Int) = routes.PartTimeHoursController.onPageLoad(idx).url

  val partTimePeriods: List[Periods] = List(fullPeriod("2020,3,1", "2020,3,31"), fullPeriod("2020,4,1", "2020,4,30"))

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

  def getRequest(method: String, idx: Int) =
    FakeRequest(method, partTimeHoursRoute(idx)).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  "PartTimeHours Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[PartTimeHoursView]

      val request = getRequest("GET", 1)

      val result = route(application, request).value

      status(result) mustEqual OK

      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(form, period("2020,3,1", "2020,3,31"), 1)(dataRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val date = LocalDate.of(2020, 3, 1)

      val preValue = PartTimeHours(date, Hours(10.5))

      val updatedAnswers = userAnswers.set(PartTimeHoursPage, preValue, Some(1)).success.value

      val application = applicationBuilder(userAnswers = Some(updatedAnswers)).build()

      val view = application.injector.instanceOf[PartTimeHoursView]

      val request = getRequest("GET", 1)

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(preValue.hours), period("2020,3,1", "2020,3,31"), 1)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute))
          )
          .build()

      val request =
        getRequest(POST, 1)
          .withFormUrlEncodedBody(("value", "10.00"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "redirect to error page for GET when index is not valid" when {

      "index is negative" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = getRequest("GET", -1)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url

        application.stop()
      }

      "index is 0" in {
        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = getRequest("GET", 0)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url

        application.stop()
      }

      "index is too high" in {
        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = getRequest("GET", 4)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url

        application.stop()
      }
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        getRequest(POST, 1)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[PartTimeHoursView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, period("2020,3,1", "2020,3,31"), 1)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = getRequest("GET", 1)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        getRequest(POST, 1)
          .withFormUrlEncodedBody(("xxxx", "value 1"), ("xxxxx", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
