/*
 * Copyright 2021 HM Revenue & Customs
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
import config.featureSwitch.{ExtensionTwoNewStarterFlow, FeatureSwitching}
import controllers.actions.DataRetrievalActionImpl
import forms.PreviousFurloughPeriodsFormProvider
import models.EmployeeStarted.OnOrBefore1Feb2019
import models.PayMethod.Variable
import models.UserAnswers
import models.requests.DataRequest
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{EmployeeStartedPage, FurloughStartDatePage, OnPayrollBefore30thOct2020Page, PreviousFurloughPeriodsPage}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.PreviousFurloughPeriodsView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PreviousFurloughPeriodsControllerSpec extends SpecBaseControllerSpecs with MockitoSugar with FeatureSwitching {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new PreviousFurloughPeriodsFormProvider()
  val form         = formProvider()

  val view = app.injector.instanceOf[PreviousFurloughPeriodsView]

  lazy val previousFurloughPeriodsRoute = routes.PreviousFurloughPeriodsController.onPageLoad().url

  lazy val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, previousFurloughPeriodsRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  lazy val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, previousFurloughPeriodsRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(
        "value" -> "true"
      )

  def controller(stubbedAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) =
    new PreviousFurloughPeriodsController(
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

  val nov8th2020            = LocalDate.of(2020, 11, 8)
  val mar8th2020: LocalDate = LocalDate.of(2020, 3, 8)
  val may8th2021: LocalDate = LocalDate.of(2021, 5, 8)

  val nov1st2020            = LocalDate.of(2020, 11, 1)
  val mar1st2020: LocalDate = LocalDate.of(2020, 3, 1)
  val may1st2021: LocalDate = LocalDate.of(2021, 5, 1)

  def userAnswersEmployedBefore1stFeb2019(): UserAnswers =
    UserAnswers(userAnswersId)
      .withPayMethod(Variable)
      .withEmployeeStartDate("2019,1,1")
      .set(EmployeeStartedPage, OnOrBefore1Feb2019)
      .success
      .value

  def userAnswersWithPreviousFurloughPeriodsPageSet(userAnswers: => UserAnswers): UserAnswers =
    userAnswers
      .set(PreviousFurloughPeriodsPage, true)
      .success
      .value

  def userAnswersEmployedAfter1stFeb2019(isOnPayrollBefore30thOct: Boolean): UserAnswers =
    UserAnswers(userAnswersId)
      .withEmployeeStartedAfter1Feb2019()
      .withEmployeeStartDate("2020,3,1")
      .withPayMethod(Variable)
      .set(OnPayrollBefore30thOct2020Page, isOnPayrollBefore30thOct)
      .success
      .value

  "PreviousFurloughPeriods Controller" must {

    "return OK and the correct view for a GET - showing 1st November 2020 when Feature Switch is disabled" in {
      val userAnswers = emptyUserAnswers
        .withEmployeeStartedAfter1Feb2019()
      disable(ExtensionTwoNewStarterFlow)
      val result = controller(Some(userAnswers)).onPageLoad()(getRequest)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, nov1st2020)(getRequest, messages).toString
      enable(ExtensionTwoNewStarterFlow)
    }

    "return OK and the correct view for a GET" in {
      val result = controller(Some(userAnswersEmployedAfter1stFeb2019(true))).onPageLoad()(getRequest)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, nov1st2020)(getRequest, messages).toString
    }

    "return OK and the correct view for a GET when the user selected Yes to employee working for them before Feb 2019 - showing March 2020" in {
      val result = controller(Some(userAnswersEmployedBefore1stFeb2019())).onPageLoad()(getRequest)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mar1st2020)(getRequest, messages).toString
    }

    "return OK and the correct view for a GET when the user selected No to employee working for them before Feb 2019" must {
      "show 1st November 2020 when the user answered Yes on OnPayrollBefore30thOct2020Page" in {
        val result = controller(Some(userAnswersEmployedAfter1stFeb2019(true))).onPageLoad()(getRequest)
        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(form, nov1st2020)(getRequest, messages).toString
      }

      "show 1st May 2021 when the user answered No on OnPayrollBefore30thOct2020Page" in {
        val result = controller(Some(userAnswersEmployedAfter1stFeb2019(false))).onPageLoad()(getRequest)
        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(form, may1st2021)(getRequest, messages).toString
      }
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val result = controller(Some(userAnswersWithPreviousFurloughPeriodsPageSet(userAnswersEmployedBefore1stFeb2019())))
        .onPageLoad()(getRequest)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), mar1st2020)(getRequest, messages).toString

    }

    "redirect to the next page when the value 'true' is submitted" in {

      when(mockSessionRepository.get(any())) thenReturn Future.successful(
        Some(userAnswersWithPreviousFurloughPeriodsPageSet(userAnswersEmployedBefore1stFeb2019)))

      val result = controller(Some(userAnswersWithPreviousFurloughPeriodsPageSet(userAnswersEmployedBefore1stFeb2019)))
        .onSubmit()(postRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.FirstFurloughDateController.onPageLoad().url
    }

    "redirect to the next page when the value 'false' is submitted " in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(
        Some(userAnswersWithPreviousFurloughPeriodsPageSet(userAnswersEmployedBefore1stFeb2019)))

      lazy val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest(POST, previousFurloughPeriodsRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(
            "value" -> "false"
          )

      val result = controller(Some(userAnswersWithPreviousFurloughPeriodsPageSet(userAnswersEmployedBefore1stFeb2019)))
        .onSubmit()(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.PayDateController.onPageLoad(1).url
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val userAnswers = userAnswersWithPreviousFurloughPeriodsPageSet(userAnswersEmployedBefore1stFeb2019)
      val request =
        FakeRequest(POST, previousFurloughPeriodsRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result      = controller(Some(userAnswers)).onSubmit()(request)
      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, mar1st2020)(dataRequest, messages).toString
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
