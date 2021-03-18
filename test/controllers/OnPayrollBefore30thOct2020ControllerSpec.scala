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

import base.SpecBaseControllerSpecs
import forms.OnPayrollBefore30thOct2020FormProvider
import models.PayMethod.Variable
import models.UserAnswers
import models.requests.DataRequest
import org.mockito.Matchers.any
import org.mockito.Matchers.{eq => argEqual}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{EmployeeStartDatePage, OnPayrollBefore30thOct2020Page}
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.OnPayrollBefore30thOct2020View

import java.time.LocalDate
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class OnPayrollBefore30thOct2020ControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

  val view         = app.injector.instanceOf[OnPayrollBefore30thOct2020View]
  val formProvider = new OnPayrollBefore30thOct2020FormProvider()
  val form         = formProvider()

  def onwardRoute = Call("GET", "/foo")

  lazy val onPageLoadUrl: String = routes.OnPayrollBefore30thOct2020Controller.onPageLoad().url

  lazy val postAction: Call      = routes.OnPayrollBefore30thOct2020Controller.onSubmit()
  lazy val postActionUrl: String = routes.OnPayrollBefore30thOct2020Controller.onSubmit().url

  val controller = new OnPayrollBefore30thOct2020Controller(
    messagesApi = messagesApi,
    sessionRepository = mockSessionRepository,
    navigator = navigator,
    identify = identifier,
    getData = dataRetrieval,
    requireData = dataRequired,
    formProvider = formProvider,
    controllerComponents = component,
    view = view
  )

  val userAnswers = emptyUserAnswers
    .withPayMethod(Variable)

  "OnPayrollBefore30thOct2020 Controller" must {

    "calling onPageLoad()" when {

      "employee start date is after 30th October" must {

        "set the answer to false and redirect to the next page as determined by the navigator" in {

          val userAnswers    = emptyUserAnswers.set(EmployeeStartDatePage, LocalDate.of(2020, 10, 31)).get
          val updatedAnswers = userAnswers.set(OnPayrollBefore30thOct2020Page, false).get

          when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
          when(mockSessionRepository.set(argEqual(updatedAnswers))) thenReturn Future.successful(true)

          val request = FakeRequest(GET, onPageLoadUrl).withCSRFToken.asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          val result  = controller.onPageLoad()(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(navigator.nextPage(OnPayrollBefore30thOct2020Page, updatedAnswers).url)
        }
      }

      "employee start date is before 1st September 2020" must {

        "set the answer to true and redirect to the next page as determined by the navigator" in {

          val userAnswers    = emptyUserAnswers.set(EmployeeStartDatePage, LocalDate.of(2020, 8, 30)).get
          val updatedAnswers = userAnswers.set(OnPayrollBefore30thOct2020Page, true).get

          when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
          when(mockSessionRepository.set(argEqual(updatedAnswers))) thenReturn Future.successful(true)

          val request = FakeRequest(GET, onPageLoadUrl).withCSRFToken.asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          val result  = controller.onPageLoad()(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(navigator.nextPage(OnPayrollBefore30thOct2020Page, updatedAnswers).url)
        }
      }

      "employee start date is between 1st and 30th October" must {

        "return OK and the correct view for a GET" in {

          val userAnswers = emptyUserAnswers.set(EmployeeStartDatePage, LocalDate.of(2020, 9, 1)).get

          when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))

          val request = FakeRequest(GET, onPageLoadUrl).withCSRFToken
            .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          val result      = controller.onPageLoad()(request)
          val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form, postAction)(dataRequest, messages).toString
        }

        "populate the view correctly on a GET when the question has previously been answered" in {

          val userAnswers = UserAnswers(userAnswersId)
            .set(EmployeeStartDatePage, LocalDate.of(2020, 9, 1))
            .get
            .set(OnPayrollBefore30thOct2020Page, true)
            .success
            .value

          when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))

          val request     = FakeRequest(GET, onPageLoadUrl).withCSRFToken.asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          val result      = controller.onPageLoad()(request)
          val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

          status(result) mustEqual OK
          contentAsString(result) mustEqual
            view(form.fill(true), postAction)(dataRequest, messages).toString
        }
      }

      "employee start date is not answered" must {

        "return OK and the correct view for a GET" in {

          when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(emptyUserAnswers))

          val request = FakeRequest(GET, onPageLoadUrl).withCSRFToken
            .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          val result      = controller.onPageLoad()(request)
          val dataRequest = DataRequest(request, emptyUserAnswers.id, emptyUserAnswers)

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form, postAction)(dataRequest, messages).toString
        }

        "populate the view correctly on a GET when the question has previously been answered" in {

          val userAnswers = UserAnswers(userAnswersId).set(OnPayrollBefore30thOct2020Page, true).success.value

          when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))

          val request     = FakeRequest(GET, onPageLoadUrl).withCSRFToken.asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          val result      = controller.onPageLoad()(request)
          val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

          status(result) mustEqual OK
          contentAsString(result) mustEqual
            view(form.fill(true), postAction)(dataRequest, messages).toString
        }
      }

      "redirect to Session Expired for a GET if no existing data is found" in {

        when(mockSessionRepository.get(any())) thenReturn Future.successful(None)

        val request = FakeRequest(GET, onPageLoadUrl).withCSRFToken.asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
        val result  = controller.onPageLoad()(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
      }
    }

    "calling onSubmit()" should {

      "redirect to the next page when valid data is submitted" in {

        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))

        val request = {
          FakeRequest(POST, postActionUrl)
            .withFormUrlEncodedBody(("value", "true"))
            .withCSRFToken
            .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
        }

        val result = controller.onSubmit()(request)

        status(result) mustEqual SEE_OTHER
        // TODO: redirectLocation(result).value mustEqual "/job-retention-scheme-calculator/ <next page relative url>"
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))

        val request = {
          FakeRequest(POST, postActionUrl)
            .withFormUrlEncodedBody(("value", "invalid value"))
            .withCSRFToken
            .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
        }

        val boundForm   = form.bind(Map("value" -> "invalid value"))
        val result      = controller.onSubmit()(request)
        val dataRequest = DataRequest(request, emptyUserAnswers.id, emptyUserAnswers)

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual
          view(boundForm, postAction)(dataRequest, messages).toString
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        when(mockSessionRepository.get(any())) thenReturn Future.successful(None)

        val request = {
          FakeRequest(POST, postActionUrl)
            .withFormUrlEncodedBody(("value", "yes"))
            .withCSRFToken
            .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
        }

        val result = controller.onSubmit()(request)

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
      }
    }
  }
}
