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

import base.{CoreTestDataBuilder, SpecBaseWithApplication}
import forms.PayPeriodsListFormProvider
import models.PayPeriodsList
import models.requests.DataRequest
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.PayPeriodsListView

class PayPeriodsListControllerSpec extends SpecBaseWithApplication with MockitoSugar with CoreTestDataBuilder {

  def onwardRoute = Call("GET", "/foo")

  lazy val payPeriodsListRoute = routes.PayPeriodsListController.onPageLoad().url

  lazy val getRequest = FakeRequest(GET, payPeriodsListRoute).withCSRFToken
    .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val formProvider = new PayPeriodsListFormProvider()
  val form = formProvider()

  "PayPeriodsList Controller" must {

    "return OK and the correct view for a GET" in {
      val userAnswers = emptyUserAnswers
        .withClaimPeriodStart("2020, 7, 1")
        .withClaimPeriodEnd("2020, 7, 31")
        .withFurloughStartDate("2020, 3, 20")
        .withFurloughStatus()
        .withPayMethod()
        .withPayDate(List("2020, 6, 30", "2020, 7, 31"))

      val claimPeriod = period("2020, 7, 1", "2020, 7, 31")
      val periods = Seq(fullPeriod("2020, 7, 1", "2020, 7, 31"))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = DataRequest(getRequest, userAnswers.id, userAnswers)

      val result = route(application, request).value

      val view = application.injector.instanceOf[PayPeriodsListView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, periods, claimPeriod)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = emptyUserAnswers
        .withClaimPeriodStart("2020, 7, 1")
        .withClaimPeriodEnd("2020, 7, 31")
        .withFurloughStartDate("2020, 3, 20")
        .withFurloughStatus()
        .withPayDate(List("2020, 6, 30", "2020, 7, 31"))
        .withPayPeriodsList()

      val claimPeriod = period("2020, 7, 1", "2020, 7, 31")
      val periods = Seq(fullPeriod("2020, 7, 1", "2020, 7, 31"))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[PayPeriodsListView]

      val request = getRequest
      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(PayPeriodsList.values.head), periods, claimPeriod)(request, messages).toString

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val userAnswers = emptyUserAnswers
        .withClaimPeriodStart("2020, 7, 1")
        .withClaimPeriodEnd("2020, 7, 31")
        .withFurloughStartDate("2020, 3, 20")
        .withFurloughStatus()
        .withPayDate(List("2020, 6, 30", "2020, 7, 31"))

      val claimPeriod = period("2020, 7, 1", "2020, 7, 31")
      val periods = Seq(fullPeriod("2020, 7, 1", "2020, 7, 31"))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, payPeriodsListRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[PayPeriodsListView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, periods, claimPeriod)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, payPeriodsListRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, payPeriodsListRoute)
          .withFormUrlEncodedBody(("value", PayPeriodsList.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
