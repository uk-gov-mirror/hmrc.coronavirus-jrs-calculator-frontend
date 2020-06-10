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
import forms.PartTimePeriodsFormProvider
import models.FurloughStatus.FurloughOngoing
import models.PayMethod.Regular
import models.PaymentFrequency.Monthly
import models.Periods
import models.requests.DataRequest
import navigation.{FakeNavigator, Navigator}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.PartTimePeriodsView

class PartTimePeriodsControllerSpec extends SpecBaseWithApplication with MockitoSugar with CoreTestDataBuilder {

  def onwardRoute = Call("GET", "/foo")

  lazy val partTimePeriodsRoute = routes.PartTimePeriodsController.onPageLoad().url

  lazy val getRequest = FakeRequest(GET, partTimePeriodsRoute).withCSRFToken
    .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
  val formProvider = new PartTimePeriodsFormProvider()
  val form = formProvider()

  val validAnswer: List[Periods] = List(fullPeriod("2020,3,1", "2020,3,31"), fullPeriod("2020,4,1", "2020,4,30"))

  val baseUserAnswers = emptyUserAnswers
    .withClaimPeriodStart("2020, 3, 1")
    .withClaimPeriodEnd("2020, 4, 30")
    .withPaymentFrequency(Monthly)
    .withPayMethod(Regular)
    .withFurloughStatus(FurloughOngoing)
    .withFurloughStartDate("2020, 3, 1")
    .withLastPayDate("2020, 3, 31")
    .withPayDate(List("2020, 2, 29", "2020, 3, 31", "2020, 4, 30"))
    .withRegularPayAmount(2000)

  "PartTimePeriodsController" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[PartTimePeriodsView]

      status(result) mustEqual OK

      val dataRequest = DataRequest(getRequest, baseUserAnswers.id, baseUserAnswers)

      contentAsString(result) mustEqual
        view(form, validAnswer)(dataRequest, messages).toString

      application.stop()
    }

    "redirect for a GET when there is only one period" in {
      val userAnswers = emptyUserAnswers
        .withClaimPeriodStart("2020, 3, 1")
        .withClaimPeriodEnd("2020, 3, 31")
        .withPaymentFrequency(Monthly)
        .withPayMethod(Regular)
        .withFurloughStatus(FurloughOngoing)
        .withFurloughStartDate("2020, 3, 1")
        .withLastPayDate("2020, 3, 31")
        .withPayDate(List("2020, 2, 29", "2020, 3, 31"))
        .withRegularPayAmount(2000)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute))
          )
          .build()

      val result = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe onwardRoute.url

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = baseUserAnswers
        .withPartTimePeriods(validAnswer)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[PartTimePeriodsView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(form.fill(validAnswer.map(_.period.end)), validAnswer)(dataRequest, messages).toString

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
        FakeRequest(POST, partTimePeriodsRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody("value[0]" -> validAnswer.head.period.end.toString)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      val request =
        FakeRequest(POST, partTimePeriodsRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value[0]", "invalid value"))

      val boundForm = form.bind(Map("value[0]" -> "invalid value"))

      val view = application.injector.instanceOf[PartTimePeriodsView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      val dataRequest = DataRequest(request, baseUserAnswers.id, baseUserAnswers)

      contentAsString(result) mustEqual
        view(boundForm, validAnswer)(dataRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, partTimePeriodsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, partTimePeriodsRoute)
          .withFormUrlEncodedBody("value[0]" -> validAnswer.head.period.end.toString)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
