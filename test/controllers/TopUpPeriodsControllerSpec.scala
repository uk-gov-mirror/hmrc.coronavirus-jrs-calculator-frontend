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
import forms.TopUpPeriodsFormProvider
import models.FurloughStatus.FurloughOngoing
import models.PayMethod.Regular
import models.PaymentFrequency.Monthly
import models.requests.DataRequest
import models.{Amount, FullPeriodCap, FurloughBreakdown, TopUpPeriod, UserAnswers}
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
import views.html.TopUpPeriodsView

import scala.concurrent.Future

class TopUpPeriodsControllerSpec extends SpecBaseWithApplication with MockitoSugar with CoreTestDataBuilder {

  def onwardRoute = Call("GET", "/foo")

  lazy val topupPeriodsRoute = routes.TopUpPeriodsController.onPageLoad().url

  lazy val getRequest = FakeRequest(GET, topupPeriodsRoute).withCSRFToken
    .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
  val formProvider = new TopUpPeriodsFormProvider()
  val form = formProvider()

  val dates = List(LocalDate.of(2020, 3, 31), LocalDate.of(2020, 4, 30))
  val periodBreakdowns: Seq[FurloughBreakdown] = Seq(
    fullPeriodFurloughBreakdown(
      1600.00,
      regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-31")),
      FullPeriodCap(2500.00)),
    fullPeriodFurloughBreakdown(
      1600.00,
      regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-30")),
      FullPeriodCap(2500.00))
  )

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

  "TopupPeriods Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[TopUpPeriodsView]

      status(result) mustEqual OK

      val dataRequest = DataRequest(getRequest, baseUserAnswers.id, baseUserAnswers)

      contentAsString(result) mustEqual
        view(form, periodBreakdowns)(dataRequest, messages).toString

      application.stop()
    }

    "redirect for a GET when there is only one period to top up" in {
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

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val result = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe onwardRoute.url

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val topUpPeriod = dates.map(TopUpPeriod(_, furloughGrant = Amount(100)))

      val userAnswers = baseUserAnswers
        .withTopUpPeriods(topUpPeriod)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[TopUpPeriodsView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(form.fill(dates), periodBreakdowns)(dataRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(baseUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request =
        FakeRequest(POST, topupPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]" -> dates.head.toString()))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      val request =
        FakeRequest(POST, topupPeriodsRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value[0]", "invalid value"))

      val boundForm = form.bind(Map("value[0]" -> "invalid value"))

      val view = application.injector.instanceOf[TopUpPeriodsView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      val dataRequest = DataRequest(request, baseUserAnswers.id, baseUserAnswers)

      contentAsString(result) mustEqual
        view(boundForm, periodBreakdowns)(dataRequest, messages).toString

      application.stop()
    }

    "redirect to error page for a GET if missing values for furlough pay calculation" in {

      val application = applicationBuilder(userAnswers = Some(UserAnswers("id"))).build()

      val request =
        FakeRequest(GET, topupPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]", dates.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url

      application.stop()
    }

    "redirect to error page for a POST if missing values for furlough pay calculation" in {

      val application = applicationBuilder(userAnswers = Some(UserAnswers("id"))).build()

      val request =
        FakeRequest(POST, topupPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]", dates.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url

      application.stop()
    }

    "redirect to error page for a POST if dates in furlough and input do not align" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      val request =
        FakeRequest(POST, topupPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]", dates(0).toString), ("value[1]", dates(1).toString), ("value[2]", "2020-05-30"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, topupPeriodsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, topupPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]", dates.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
