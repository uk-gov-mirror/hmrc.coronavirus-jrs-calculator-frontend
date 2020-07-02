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
import forms.PayPeriodQuestionFormProvider
import models.ClaimPeriodQuestion.ClaimOnSamePeriod
import models.FurloughPeriodQuestion.FurloughedOnSamePeriod
import models.FurloughStatus.FurloughOngoing
import models.PayMethod.Regular
import models.PaymentFrequency.Monthly
import models.requests.DataRequest
import models.{PayPeriodQuestion, Period}
import navigation.{FakeNavigator, Navigator}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.PayPeriodQuestionView

class PayPeriodQuestionControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  private lazy val payPeriodQuestionRoute = routes.PayPeriodQuestionController.onPageLoad().url
  private lazy val payPeriodQuestionRoutePost = routes.PayPeriodQuestionController.onSubmit().url

  val formProvider = new PayPeriodQuestionFormProvider()
  val form = formProvider()

  val baseUserAnswers = emptyUserAnswers
    .withClaimPeriodStart("2020, 3, 1")
    .withClaimPeriodEnd("2020, 4, 30")
    .withClaimPeriodQuestion(ClaimOnSamePeriod)
    .withFurloughPeriodQuestion(FurloughedOnSamePeriod)
    .withPaymentFrequency(Monthly)
    .withPayMethod(Regular)
    .withFurloughStatus(FurloughOngoing)
    .withFurloughStartDate("2020, 3, 1")
    .withLastPayDate("2020, 3, 31")
    .withPayDate(List("2020, 2, 29", "2020, 3, 31", "2020, 4, 30"))

  val payPeriods = Seq(
    Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)),
    Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))
  )

  "PayPeriodQuestion Controller" must {

    "return OK and the correct view for a GET" in {
      val getRequest: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, payPeriodQuestionRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[PayPeriodQuestionView]

      status(result) mustEqual OK

      val dataRequest = DataRequest(getRequest, baseUserAnswers.id, baseUserAnswers)

      contentAsString(result) mustEqual
        view(form, payPeriods)(dataRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val getRequest: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, payPeriodQuestionRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

      val userAnswersUpdated = baseUserAnswers.withPayPeriodQuestion(PayPeriodQuestion.values.head)
      val application = applicationBuilder(userAnswers = Some(userAnswersUpdated)).build()

      val view = application.injector.instanceOf[PayPeriodQuestionView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      val dataRequest = DataRequest(getRequest, userAnswersUpdated.id, userAnswersUpdated)

      contentAsString(result) mustEqual
        view(form.fill(PayPeriodQuestion.values.head), payPeriods)(dataRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(baseUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute))
          )
          .build()

      val request =
        FakeRequest(POST, payPeriodQuestionRoutePost)
          .withFormUrlEncodedBody(("value", PayPeriodQuestion.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      val request =
        FakeRequest(POST, payPeriodQuestionRoutePost).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[PayPeriodQuestionView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      val dataRequest = DataRequest(request, baseUserAnswers.id, baseUserAnswers)

      contentAsString(result) mustEqual
        view(boundForm, payPeriods)(dataRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, payPeriodQuestionRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, payPeriodQuestionRoute)
          .withFormUrlEncodedBody(("value", PayPeriodQuestion.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
