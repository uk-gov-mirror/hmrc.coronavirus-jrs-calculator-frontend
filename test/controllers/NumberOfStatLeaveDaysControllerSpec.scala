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
import forms.NumberOfStatLeaveDaysFormProvider
import models.{EmployeeStarted, NormalMode, UserAnswers}
import org.scalatestplus.mockito.MockitoSugar
import pages.{EmployeeStartDatePage, EmployeeStartedPage, FirstFurloughDatePage, FurloughStartDatePage, HasEmployeeBeenOnStatutoryLeavePage, NumberOfStatLeaveDaysPage, OnPayrollBefore30thOct2020Page}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.NumberOfStatLeaveDaysView
import viewmodels.NumberOfStatLeaveDaysHelper
import views.ViewUtils.dateToString
import java.time.LocalDate

import assets.messages.BeenOnStatutoryLeaveMessages
import models.requests.DataRequest
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.data.Form
import utils.LocalDateHelpers.{apr5th2020, apr6th2019, apr6th2020, feb1st2020}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NumberOfStatLeaveDaysControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

  val helper       = app.injector.instanceOf[NumberOfStatLeaveDaysHelper]
  val view         = app.injector.instanceOf[NumberOfStatLeaveDaysView]
  val postAction   = controllers.routes.NumberOfStatLeaveDaysController.onSubmit()
  val formProvider = new NumberOfStatLeaveDaysFormProvider()

  def form(boundaryStart: LocalDate, boundaryEnd: LocalDate): Form[Int] = formProvider(boundaryStart, boundaryEnd)

  lazy val numberOfDaysOnStatutoryLeaveRoute = routes.NumberOfStatLeaveDaysController.onPageLoad().url

  val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, numberOfDaysOnStatutoryLeaveRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val controller = new NumberOfStatLeaveDaysController(
    messagesApi,
    sessionRepository = mockSessionRepository,
    navigator = navigator,
    identify = identifier,
    getData = dataRetrieval,
    requireData = dataRequired,
    formProvider = formProvider,
    helper = helper,
    controllerComponents = component,
    view = view
  )

  "NumberOfDaysOnStatLeave Controller" when {

    "onPageLoad" when {

      "employee is type 3" when {

        "the day before employee is first furloughed is before the 5th April 2020" must {

          "return OK and the correct value for a GET" in {

            val firstFurloughDate = LocalDate.parse("2020-04-05")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val boundaryStartDate = apr6th2019
            val boundaryEndDate   = firstFurloughDate.minusDays(1)
            val boundaryStart     = dateToString(apr6th2019)
            val boundaryEnd       = dateToString(firstFurloughDate.minusDays(1))

            val userAnswers = emptyUserAnswers
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
              .success
              .value
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val result      = controller.onPageLoad()(getRequest)
            val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

            status(result) mustEqual OK
            contentAsString(result) mustEqual
              view(form(boundaryStartDate, boundaryEndDate), postAction, boundaryStart, boundaryEnd)(dataRequest, messages).toString

          }
        }

        "day before employee is first furloughed is after 5th Aptil 2020" must {

          "return OK and the correct view for a GET" in {

            val firstFurloughDate = LocalDate.parse("2020-04-06")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val boundaryStartDate = apr6th2019
            val boundaryEndDate   = apr5th2020
            val boundaryStart     = dateToString(apr6th2019)
            val boundaryEnd       = dateToString(apr5th2020)

            val userAnswers = emptyUserAnswers
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
              .success
              .value
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val result      = controller.onPageLoad()(getRequest)
            val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

            status(result) mustEqual OK
            contentAsString(result) mustEqual
              view(form(boundaryStartDate, boundaryEndDate), postAction, boundaryStart, boundaryEnd)(dataRequest, messages).toString
          }
        }
      }

      "employee is type 4" when {

        "the day before employee is first furloughed in before 5th April 2020" must {

          "return OK and the correct view for a GET" in {

            val employeeStartDate = feb1st2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2020-04-05")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val boundaryStartDate = feb1st2020.minusDays(1)
            val boundaryEndDate   = firstFurloughDate.minusDays(1)

            val userAnswers = emptyUserAnswers
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
              .success
              .value
              .set(EmployeeStartDatePage, employeeStartDate)
              .success
              .value
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val result      = controller.onPageLoad()(getRequest)
            val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

            status(result) mustEqual OK
            contentAsString(result) mustEqual
              view(
                form = form(boundaryStartDate, boundaryEndDate),
                postAction = postAction,
                boundaryStart = dateToString(boundaryStartDate),
                boundaryEnd = dateToString(boundaryEndDate)
              )(dataRequest, messages).toString
          }
        }

        "the day before the employee is first furloughed is after 5th April 2020" must {

          "return OK and the correct view for a GET" in {

            val employeeStartDate = feb1st2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2020-04-06")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val boundaryStartDate = employeeStartDate
            val boundaryEndDate   = apr5th2020

            val userAnswers = emptyUserAnswers
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
              .success
              .value
              .set(EmployeeStartDatePage, employeeStartDate)
              .success
              .value
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val result      = controller.onPageLoad()(getRequest)
            val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

            status(result) mustEqual OK
            contentAsString(result) mustEqual
              view(form(boundaryStartDate, boundaryEndDate), postAction, dateToString(boundaryStartDate), dateToString(boundaryEndDate))(
                dataRequest,
                messages).toString
          }
        }
      }

      "employee is type 5a" when {

        "employee started before the 6 April 2020 and first furloughed 1 Jan 2021" must {

          "return OK and the correct view for a GET " in {

            val employeeStartDate = apr6th2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2021-01-01")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val boundaryStartDate = apr6th2020
            val boundaryEndDate   = firstFurloughDate.minusDays(1)

            val userAnswers = emptyUserAnswers
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
              .success
              .value
              .set(EmployeeStartDatePage, employeeStartDate)
              .success
              .value
              .set(OnPayrollBefore30thOct2020Page, true)
              .success
              .value
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val result      = controller.onPageLoad()(getRequest)
            val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

            status(result) mustEqual OK
            contentAsString(result) mustEqual
              view(form(boundaryStartDate, boundaryEndDate), postAction, dateToString(boundaryStartDate), dateToString(boundaryEndDate))(
                dataRequest,
                messages).toString()
          }
        }
        "employment started after 6 April 2020 and first furloughed 1 Jan 2021" must {

          "return OK and the correct view for a GET" in {

            val employeeStartDate = apr6th2020.plusDays(1)
            val firstFurloughDate = LocalDate.parse("2021-01-01")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val boundaryStartDate = apr6th2020.plusDays(1)
            val boundaryEndDate   = firstFurloughDate.minusDays(1)

            val userAnswers = emptyUserAnswers
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
              .success
              .value
              .set(EmployeeStartDatePage, employeeStartDate)
              .success
              .value
              .set(OnPayrollBefore30thOct2020Page, true)
              .success
              .value
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val result      = controller.onPageLoad()(getRequest)
            val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

            status(result) mustEqual OK
            contentAsString(result) mustEqual
              view(form(boundaryStartDate, boundaryEndDate), postAction, dateToString(boundaryStartDate), dateToString(boundaryEndDate))(
                dataRequest,
                messages).toString
          }

        }
      }

      "employee is type 5b" when {

        "employment started before 6 April 2020 and first furloughed 01 May 2021" must {

          "return OK and get the correct view for a GET" in {

            val employeeStartDate = apr6th2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2021-05-01")
            val furloughStartDate = LocalDate.parse("2021-05-21")
            val boundaryStart     = dateToString(apr6th2020)
            val boundaryEnd       = dateToString(firstFurloughDate.minusDays(1))
            val boundaryStartDate = apr6th2020
            val boundaryEndDate   = firstFurloughDate.minusDays(1)

            val userAnswers = emptyUserAnswers
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
              .success
              .value
              .set(EmployeeStartDatePage, employeeStartDate)
              .success
              .value
              .set(OnPayrollBefore30thOct2020Page, false)
              .success
              .value
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val result      = controller.onPageLoad()(getRequest)
            val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

            status(result) mustEqual OK
            contentAsString(result) mustEqual
              view(form(boundaryStartDate, boundaryEndDate), postAction, boundaryStart, boundaryEnd)(dataRequest, messages).toString()

          }
        }
        "employment started after 6 April 2020 and first furloughed 01 May 2021" must {

          "return OK and the correct view for a GET" in {

            val employeeStartDate = apr6th2020.plusDays(1)
            val firstFurloughDate = LocalDate.parse("2021-05-01")
            val furloughStartDate = LocalDate.parse("2021-05-21")
            val boundaryStartDate = employeeStartDate
            val boundaryEndDate   = firstFurloughDate.minusDays(1)

            val userAnswers = emptyUserAnswers
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
              .success
              .value
              .set(EmployeeStartDatePage, employeeStartDate)
              .success
              .value
              .set(OnPayrollBefore30thOct2020Page, false)
              .success
              .value
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val result      = controller.onPageLoad()(getRequest)
            val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

            status(result) mustEqual OK
            contentAsString(result) mustEqual
              view(
                form = form(boundaryStartDate, boundaryEndDate),
                postAction = postAction,
                boundaryStart = dateToString(boundaryStartDate),
                boundaryEnd = dateToString(boundaryEndDate)
              )(dataRequest, messages).toString
          }
        }
      }
    }

    "onSubmit" when {

      "employeeType is type3" must {

        "day before employee is first furloughed is before 5th April 2020" must {

          "redirect to the next page when valid data is submitted" in {

            val firstFurloughDate = LocalDate.parse("2020-04-05")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val employeeStartDate = apr6th2019

            val userAnswers = emptyUserAnswers
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
              .success
              .value
              .set(EmployeeStartDatePage, employeeStartDate)
              .success
              .value
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val request =
              FakeRequest(POST, numberOfDaysOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "20"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual "/job-retention-scheme-calculator"
          }

          "return a Bad Request and errors when invalid data is submitted" in {

            val firstFurloughDate = LocalDate.parse("2020-04-05")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val boundaryStartDate = apr6th2019
            val boundaryEndDate   = firstFurloughDate.minusDays(1)

            val userAnswers = emptyUserAnswers
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
              .success
              .value
              .set(EmployeeStartDatePage, boundaryStartDate)
              .success
              .value
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val request =
              FakeRequest(POST, numberOfDaysOnStatutoryLeaveRoute)
                .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                .withFormUrlEncodedBody(("value", "invalid value"))

            val boundForm   = form(boundaryStartDate, boundaryEndDate).bind(Map("value" -> "invalid value"))
            val result      = controller.onSubmit()(request)
            val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

            status(result) mustEqual BAD_REQUEST
            contentAsString(result) mustEqual
              view(boundForm, postAction, dateToString(boundaryStartDate), dateToString(boundaryEndDate))(dataRequest, messages).toString
          }
        }

        "day before employee is first furloughed is after 5th April 2020" must {

          "redirect to the next page when valid data is submitted" in {

            val firstFurloughDate = LocalDate.parse("2020-04-06")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val employeeStarDate  = apr6th2019

            val userAnswers = emptyUserAnswers
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartDatePage, employeeStarDate)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val request =
              FakeRequest(POST, numberOfDaysOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "10"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual "/job-retention-scheme-calculator"
          }

          "return a Bad Request and errors when invalid data is submitted" in {

            val firstFurloughDate = LocalDate.parse("2020-04-06")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val boundaryStartDate = apr6th2019
            val boundaryEndDate   = apr5th2020

            val userAnswers = emptyUserAnswers
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartDatePage, apr6th2019)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val request =
              FakeRequest(POST, numberOfDaysOnStatutoryLeaveRoute)
                .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                .withFormUrlEncodedBody(("value", "invalid value"))

            val boundForm   = form(boundaryStartDate, boundaryEndDate).bind(Map("value" -> "invalid value"))
            val result      = controller.onSubmit()(request)
            val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

            status(result) mustEqual BAD_REQUEST
            contentAsString(result) mustEqual
              view(boundForm, postAction, dateToString(boundaryStartDate), dateToString(boundaryEndDate))(dataRequest, messages).toString
          }
        }
      }

      "employeeType is type4" must {

        "day before employee is first furloughed is before 5th April 2020" must {

          "redirect to the next page when valid data is submitted" in {

            val employeeStartDate = feb1st2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2020-04-05")
            val furloughStartDate = LocalDate.parse("2021-03-01")

            val userAnswers = emptyUserAnswers
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
              .success
              .value
              .set(EmployeeStartDatePage, employeeStartDate)
              .success
              .value
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val request =
              FakeRequest(POST, numberOfDaysOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "5"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual "/job-retention-scheme-calculator"
          }
          "return a Bad Request and errors when invalid data is submitted" in {

            val employeeStartDate = feb1st2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2020-04-05")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val boundaryStartDate = employeeStartDate
            val boundaryEndDate   = firstFurloughDate.minusDays(1)

            val userAnswers = emptyUserAnswers
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
              .success
              .value
              .set(EmployeeStartDatePage, employeeStartDate)
              .success
              .value
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val request =
              FakeRequest(POST, numberOfDaysOnStatutoryLeaveRoute)
                .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                .withFormUrlEncodedBody(("value", "invalid value"))

            val boundForm   = form(boundaryStartDate, boundaryEndDate).bind(Map("value" -> "invalid value"))
            val result      = controller.onSubmit()(request)
            val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

            status(result) mustEqual BAD_REQUEST
            contentAsString(result) mustEqual
              view(boundForm, postAction, dateToString(boundaryStartDate), dateToString(boundaryEndDate))(dataRequest, messages).toString
          }
        }
        "the day before the employee is first furloughed is after 5th April 2020" must {

          "redirect to the next page when valid data is submitted" in {

            val employeeStartDate = feb1st2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2020-04-06")
            val furloughStartDate = LocalDate.parse("2021-03-01")

            val userAnswers = emptyUserAnswers
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartDatePage, employeeStartDate)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val request =
              FakeRequest(POST, numberOfDaysOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "20"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual "/job-retention-scheme-calculator"
          }
          "return a Bad Request and errors when invalid data is submitted" in {

            val employeeStartDate = feb1st2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2020-04-06")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val boundaryStartDate = employeeStartDate
            val boundaryEndDate   = apr5th2020

            val userAnswers = emptyUserAnswers
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartDatePage, employeeStartDate)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val request =
              FakeRequest(POST, numberOfDaysOnStatutoryLeaveRoute)
                .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                .withFormUrlEncodedBody(("value", "invalid value"))

            val boundForm   = form(boundaryStartDate, boundaryEndDate).bind(Map("value" -> "invalid value"))
            val result      = controller.onSubmit()(request)
            val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

            status(result) mustEqual BAD_REQUEST
            contentAsString(result) mustEqual
              view(
                form = boundForm,
                postAction = postAction,
                boundaryStart = dateToString(boundaryStartDate),
                boundaryEnd = dateToString(boundaryEndDate)
              )(dataRequest, messages).toString
          }
        }
      }

      "employeeType is type5a" must {

        "employment started before 6 April 2020 and first furloughed 1 Jan 2021" must {

          "redirect to the net page when valid data is submitted" in {

            val employeeStartDate = apr6th2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2021-01-01")
            val furloughStartDate = LocalDate.parse("2021-03-01")

            val userAnswers = emptyUserAnswers
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
              .success
              .value
              .set(EmployeeStartDatePage, employeeStartDate)
              .success
              .value
              .set(OnPayrollBefore30thOct2020Page, true)
              .success
              .value
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val request =
              FakeRequest(POST, numberOfDaysOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "5"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual "/job-retention-scheme-calculator"
          }
        }

      }
    }
  }
}
