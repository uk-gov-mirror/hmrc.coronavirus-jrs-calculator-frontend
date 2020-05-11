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
import controllers.actions.FeatureFlag._
import forms.AdditionalPaymentPeriodsFormProvider
import models.FurloughStatus.FurloughOngoing
import models.PayMethod.Regular
import models.PaymentFrequency.Monthly
import models.{Amount, FullPeriodBreakdown, PeriodBreakdown, Salary, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.AdditionalPaymentPeriodsView

import scala.concurrent.Future

class AdditionalPaymentPeriodsControllerSpec extends SpecBaseWithApplication with MockitoSugar with CoreTestDataBuilder {

  def onwardRoute = Call("GET", "/foo")

  lazy val additionalPaymentPeriodsRoute = routes.AdditionalPaymentPeriodsController.onPageLoad().url

  lazy val getRequest = FakeRequest(GET, additionalPaymentPeriodsRoute).withCSRFToken
    .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
  val formProvider = new AdditionalPaymentPeriodsFormProvider()
  val form = formProvider()

  val validAnswer = List(LocalDate.of(2020, 3, 31), LocalDate.of(2020, 4, 30))
  val periodBreakdowns: Seq[PeriodBreakdown] = Seq(
    FullPeriodBreakdown(Amount(1600.00), fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-31")),
    FullPeriodBreakdown(Amount(1600.00), fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-30"))
  )

  val baseUserAnswers = UserAnswers("id")
    .setValue(ClaimPeriodStartPage, LocalDate.of(2020, 3, 1))
    .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 4, 30))
    .setValue(PaymentFrequencyPage, Monthly)
    .setValue(PayMethodPage, Regular)
    .setValue(FurloughStatusPage, FurloughOngoing)
    .setValue(FurloughStartDatePage, LocalDate.of(2020, 3, 1))
    .setValue(LastPayDatePage, LocalDate.of(2020, 3, 31))
    .setValue(PayDatePage, LocalDate.of(2020, 2, 29), Some(1))
    .setValue(PayDatePage, LocalDate.of(2020, 3, 31), Some(2))
    .setValue(PayDatePage, LocalDate.of(2020, 4, 30), Some(3))
    .setValue(SalaryQuestionPage, Salary(2000))

  "TopupPeriods Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[AdditionalPaymentPeriodsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, periodBreakdowns)(getRequest, messages).toString

      application.stop()
    }

    "redirect for a GET when there is only one period to top up" in {
      val userAnswers = UserAnswers("id")
        .setValue(ClaimPeriodStartPage, LocalDate.of(2020, 3, 1))
        .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 3, 31))
        .setValue(PaymentFrequencyPage, Monthly)
        .setValue(PayMethodPage, Regular)
        .setValue(FurloughStatusPage, FurloughOngoing)
        .setValue(FurloughStartDatePage, LocalDate.of(2020, 3, 1))
        .setValue(LastPayDatePage, LocalDate.of(2020, 3, 31))
        .setValue(PayDatePage, LocalDate.of(2020, 2, 29), Some(1))
        .setValue(PayDatePage, LocalDate.of(2020, 3, 31), Some(2))
        .setValue(SalaryQuestionPage, Salary(2000))

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

      val userAnswers = baseUserAnswers
        .setValue(AdditionalPaymentPeriodsPage, validAnswer)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[AdditionalPaymentPeriodsView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), periodBreakdowns)(getRequest, messages).toString

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
        FakeRequest(POST, additionalPaymentPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]" -> validAnswer.head.toString()))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      val request =
        FakeRequest(POST, additionalPaymentPeriodsRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value[0]", "invalid value"))

      val boundForm = form.bind(Map("value[0]" -> "invalid value"))

      val view = application.injector.instanceOf[AdditionalPaymentPeriodsView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, periodBreakdowns)(request, messages).toString

      application.stop()
    }

    "redirect to 404 page for a GET if topups flag is disabled" in {

      val application = applicationBuilder(config = Map(TopUpJourneyFlag.key -> false), userAnswers = Some(UserAnswers("id"))).build()

      val request = FakeRequest(GET, additionalPaymentPeriodsRoute)

      val result = route(application, request).value

      status(result) mustEqual NOT_FOUND

      application.stop()
    }

    "redirect to 404 page for a POST if topups flag is disabled" in {

      val application = applicationBuilder(config = Map(TopUpJourneyFlag.key -> false), userAnswers = Some(UserAnswers("id"))).build()

      val request =
        FakeRequest(POST, additionalPaymentPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]", validAnswer.head.toString))

      val result = route(application, request).value

      status(result) mustEqual NOT_FOUND

      application.stop()
    }

    "redirect to error page for a GET if missing values for furlough pay calculation" in {

      val application = applicationBuilder(userAnswers = Some(UserAnswers("id"))).build()

      val request =
        FakeRequest(GET, additionalPaymentPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]", validAnswer.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url

      application.stop()
    }

    "redirect to error page for a POST if missing values for furlough pay calculation" in {

      val application = applicationBuilder(userAnswers = Some(UserAnswers("id"))).build()

      val request =
        FakeRequest(POST, additionalPaymentPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]", validAnswer.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url

      application.stop()
    }

    "redirect to error page for a POST if dates in furlough and input do not align" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      val request =
        FakeRequest(POST, additionalPaymentPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]", validAnswer(0).toString), ("value[1]", validAnswer(1).toString), ("value[2]", "2020-05-30"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, additionalPaymentPeriodsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, additionalPaymentPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]", validAnswer.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
