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

import base.SpecBaseWithApplication
import forms.PaymentFrequencyFormProvider
import models.requests.DataRequest
import models.{PaymentFrequency, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.scalatestplus.mockito.MockitoSugar
import pages.PaymentFrequencyPage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.PaymentFrequencyView

class PaymentFrequencyControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val paymentFrequencyRoute = routes.PaymentFrequencyController.onPageLoad().url
  lazy val paymentFrequencyRoutePost = routes.PaymentFrequencyController.onSubmit().url

  val formProvider = new PaymentFrequencyFormProvider()
  val form = formProvider()

  lazy val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, paymentFrequencyRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  lazy val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, paymentFrequencyRoutePost).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(
        "value" -> "weekly"
      )

  "PaymentFrequency Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[PaymentFrequencyView]

      status(result) mustEqual OK

      val dataRequest = DataRequest(getRequest, emptyUserAnswers.id, emptyUserAnswers)

      contentAsString(result) mustEqual
        view(form)(dataRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(PaymentFrequencyPage, PaymentFrequency.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[PaymentFrequencyView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(form.fill(PaymentFrequency.values.head))(dataRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute))
          )
          .build()

      val result = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[PaymentFrequencyView]

      val request =
        FakeRequest(POST, paymentFrequencyRoutePost).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      val dataRequest = DataRequest(request, emptyUserAnswers.id, emptyUserAnswers)

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
