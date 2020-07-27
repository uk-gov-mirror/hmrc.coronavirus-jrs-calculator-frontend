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

import base.{CoreTestDataBuilder, SpecBaseControllerSpecs}
import controllers.actions.DataRetrievalActionImpl
import forms.AdditionalPaymentPeriodsFormProvider
import models.FurloughStatus.FurloughOngoing
import models.PayMethod.Regular
import models.PaymentFrequency.Monthly
import models.requests.DataRequest
import models.{FullPeriodCap, FurloughBreakdown, UserAnswers}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswerPersistence
import views.html.AdditionalPaymentPeriodsView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AdditionalPaymentPeriodsControllerSpec extends SpecBaseControllerSpecs with CoreTestDataBuilder {

  lazy val additionalPaymentPeriodsRoute = routes.AdditionalPaymentPeriodsController.onPageLoad().url

  lazy val getRequest = FakeRequest(GET, additionalPaymentPeriodsRoute).withCSRFToken
    .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
  val formProvider = new AdditionalPaymentPeriodsFormProvider()
  val form = formProvider()

  val validAnswer = List(LocalDate.of(2020, 3, 31), LocalDate.of(2020, 4, 30))
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

  val view = app.injector.instanceOf[AdditionalPaymentPeriodsView]

  def controller(stubbedAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) =
    new AdditionalPaymentPeriodsController(
      messagesApi,
      mockSessionRepository,
      navigator,
      identifier,
      new DataRetrievalActionImpl(mockSessionRepository) {
        override protected val identifierRetrieval: String => Future[Option[UserAnswers]] =
          _ => Future.successful(stubbedAnswers)
      },
      dataRequired,
      formProvider,
      component,
      view
    ) {
      override val userAnswerPersistence = new UserAnswerPersistence(_ => Future.successful(true))
    }

  "AdditionalPaymentPeriodsController" must {
    "return OK and the correct view for a GET" in {
      val result = controller(Some(baseUserAnswers)).onPageLoad()(getRequest)
      val dataRequest = DataRequest(getRequest, baseUserAnswers.id, baseUserAnswers)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form, periodBreakdowns)(dataRequest, messages).toString
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

      val result = controller(Some(userAnswers)).onPageLoad()(getRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe "/job-retention-scheme-calculator/additional-pay-amount/1"
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = baseUserAnswers
        .withAdditionalPaymentPeriods(validAnswer.map(_.toString))
      val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)
      val result = controller(Some(userAnswers)).onPageLoad()(getRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(validAnswer), periodBreakdowns)(dataRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {
      val request =
        FakeRequest(POST, additionalPaymentPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]" -> validAnswer.head.toString()))

      val result = controller(Some(baseUserAnswers)).onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/additional-pay-amount/1"
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val request =
        FakeRequest(POST, additionalPaymentPeriodsRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value[0]", "invalid value"))

      val boundForm = form.bind(Map("value[0]" -> "invalid value"))
      val dataRequest = DataRequest(request, baseUserAnswers.id, baseUserAnswers)
      val result = controller(Some(baseUserAnswers)).onSubmit()(request)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm, periodBreakdowns)(dataRequest, messages).toString
    }

    "redirect to error page for a GET if missing values for furlough pay calculation" in {
      val request =
        FakeRequest(GET, additionalPaymentPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]", validAnswer.head.toString))

      val result = controller().onPageLoad()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
    }

    "redirect to error page for a POST if missing values for furlough pay calculation" in {
      val request =
        FakeRequest(POST, additionalPaymentPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]", validAnswer.head.toString))

      val result = controller().onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
    }

    "redirect to error page for a POST if dates in furlough and input do not align" in {

      val request =
        FakeRequest(POST, additionalPaymentPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]", validAnswer(0).toString), ("value[1]", validAnswer(1).toString), ("value[2]", "2020-05-30"))

      val result = controller(Some(baseUserAnswers)).onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ErrorController.somethingWentWrong().url
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val request = FakeRequest(GET, additionalPaymentPeriodsRoute)
      val result = controller(None).onPageLoad()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val request =
        FakeRequest(POST, additionalPaymentPeriodsRoute)
          .withFormUrlEncodedBody(("value[0]", validAnswer.head.toString))

      val result = controller(None).onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
