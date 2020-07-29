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

import base.SpecBaseControllerSpecs
import controllers.actions.DataRetrievalActionImpl
import forms.FurloughStartDateFormProvider
import models.UserAnswers
import models.requests.DataRequest
import pages.FurloughStartDatePage
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.FurloughStartDateView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FurloughStartDateControllerSpec extends SpecBaseControllerSpecs {

  val formProvider = new FurloughStartDateFormProvider()
  private val claimPeriodStart = LocalDate.of(2020, 3, 1)
  private val claimPeriodEnd = LocalDate.of(2020, 6, 1)
  private def form = formProvider(claimPeriodEnd)

  val validAnswer = LocalDate.of(2020, 4, 1)

  lazy val furloughStartDateRoute = routes.FurloughStartDateController.onPageLoad().url

  val userAnswersWithClaimStartAndEnd = emptyUserAnswers
    .withClaimPeriodStart(claimPeriodStart.toString)
    .withClaimPeriodEnd(claimPeriodEnd.toString)

  lazy val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, furloughStartDateRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  lazy val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, furloughStartDateRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(
        "value.day"   -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year"  -> validAnswer.getYear.toString
      )

  val view = app.injector.instanceOf[FurloughStartDateView]

  def controller(stubbedAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) =
    new FurloughStartDateController(
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
    )

  "FurloughStartDate Controller" must {

    "return OK and the correct view for a GET" in {
      val dataRequest = DataRequest(getRequest, userAnswersWithClaimStartAndEnd.id, userAnswersWithClaimStartAndEnd)

      val result = controller(Some(userAnswersWithClaimStartAndEnd)).onPageLoad()(getRequest)
      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, claimPeriodStart)(dataRequest, messages).toString
    }

    "return OK and the correct view for a GET with different contents if 1st July or after" in {
      val userAnswers = emptyUserAnswers.withClaimPeriodStart("2020,7,1").withClaimPeriodEnd("2020,7,14")
      val result = controller(Some(userAnswers)).onPageLoad()(getRequest)

      status(result) mustEqual OK
      val actualContent = contentAsString(result)
      actualContent must include("When was this employee originally furloughed?")
      actualContent must include("<title> When was this employee originally furloughed?")
      //TODO not include is not the greatest test Vs hidden
      actualContent must not include ("This is the date this employee started furlough. It could be before or after the start date of this claim.")
      actualContent must not include ("We’re asking because your claim could include employees who were furloughed on different dates.")
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = userAnswersWithClaimStartAndEnd.set(FurloughStartDatePage, validAnswer).success.value
      val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)
      val result = controller(Some(userAnswers)).onPageLoad()(getRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(validAnswer), claimPeriodStart)(dataRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {
      val result = controller(Some(userAnswersWithClaimStartAndEnd)).onSubmit()(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/furlough-ongoing"
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val request =
        FakeRequest(POST, furloughStartDateRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val dataRequest = DataRequest(request, userAnswersWithClaimStartAndEnd.id, userAnswersWithClaimStartAndEnd)
      val result = controller(Some(userAnswersWithClaimStartAndEnd)).onSubmit()(request)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm, claimPeriodStart)(dataRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(None).onPageLoad()(getRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val result = controller(None).onSubmit()(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
