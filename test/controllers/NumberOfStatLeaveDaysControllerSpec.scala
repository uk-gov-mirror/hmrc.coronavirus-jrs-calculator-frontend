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
import forms.NumberOfStatLeaveDaysFormProvider
import messages.NumberOfStatLeaveDaysMessages
import models.EmployeeStarted
import models.requests.DataRequest
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.LocalDateHelpers.{apr5th2020, apr6th2019, apr6th2020, feb1st2020}
import viewmodels.{BeenOnStatutoryLeaveHelper, NumberOfStatLeaveDaysHelper}
import views.ViewUtils.dateToString
import views.html.NumberOfStatLeaveDaysView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NumberOfStatLeaveDaysControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

  val formHelper    = app.injector.instanceOf[NumberOfStatLeaveDaysHelper]
  val contentHelper = app.injector.instanceOf[BeenOnStatutoryLeaveHelper]
  val view          = app.injector.instanceOf[NumberOfStatLeaveDaysView]
  val postAction    = controllers.routes.NumberOfStatLeaveDaysController.onSubmit()
  val formProvider  = new NumberOfStatLeaveDaysFormProvider()

  def form(boundaryStart: LocalDate, boundaryEnd: LocalDate): Form[Int] = formProvider(boundaryStart, boundaryEnd)

  lazy val numberOfDaysOnStatutoryLeaveRoute = routes.NumberOfStatLeaveDaysController.onPageLoad().url

  val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, numberOfDaysOnStatutoryLeaveRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val controller = new NumberOfStatLeaveDaysController(
    messagesApi = messagesApi,
    sessionRepository = mockSessionRepository,
    navigator = navigator,
    identify = identifier,
    getData = dataRetrieval,
    requireData = dataRequired,
    formProvider = formProvider,
    formHelper = formHelper,
    contentHelper = contentHelper,
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
              view(form(boundaryStartDate, boundaryEndDate), postAction, dateToString(boundaryStartDate), dateToString(boundaryEndDate))(
                dataRequest,
                messages).toString

          }
        }

        "day before employee is first furloughed is after 5th April 2020" must {

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
            val result      = controller.onPageLoad()(getRequest)
            val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

            status(result) mustEqual OK
            contentAsString(result) mustEqual
              view(
                form = form(boundaryStartDate, boundaryEndDate),
                postAction = postAction,
                boundaryStart = NumberOfStatLeaveDaysMessages.dayEmploymentStarted,
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
              view(
                form(boundaryStartDate, boundaryEndDate),
                postAction,
                NumberOfStatLeaveDaysMessages.dayEmploymentStarted,
                dateToString(boundaryEndDate)
              )(dataRequest, messages).toString
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
              view(
                form(boundaryStartDate, boundaryEndDate),
                postAction,
                boundaryStart = NumberOfStatLeaveDaysMessages.dayEmploymentStarted,
                dateToString(boundaryEndDate)
              )(dataRequest, messages).toString
          }

        }
      }

      "employee is type 5b" when {

        "employment started before 6 April 2020 and first furloughed 01 May 2021" must {

          "return OK and get the correct view for a GET" in {

            val employeeStartDate = apr6th2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2021-05-01")
            val furloughStartDate = LocalDate.parse("2021-05-21")
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
              view(
                form = form(boundaryStartDate, boundaryEndDate),
                postAction = postAction,
                boundaryStart = dateToString(boundaryStartDate),
                boundaryEnd = dateToString(boundaryEndDate)
              )(dataRequest, messages).toString()

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
                boundaryStart = NumberOfStatLeaveDaysMessages.dayEmploymentStarted,
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
            redirectLocation(result).value mustEqual routes.StatutoryLeavePayController.onPageLoad().url
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
            redirectLocation(result).value mustEqual routes.StatutoryLeavePayController.onPageLoad().url
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
            redirectLocation(result).value mustEqual routes.StatutoryLeavePayController.onPageLoad().url
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
              view(
                form = boundForm,
                postAction = postAction,
                boundaryStart = NumberOfStatLeaveDaysMessages.dayEmploymentStarted,
                boundaryEnd = dateToString(boundaryEndDate)
              )(dataRequest, messages).toString
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
            redirectLocation(result).value mustEqual routes.StatutoryLeavePayController.onPageLoad().url
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
                boundaryStart = NumberOfStatLeaveDaysMessages.dayEmploymentStarted,
                boundaryEnd = dateToString(boundaryEndDate)
              )(dataRequest, messages).toString
          }
        }
      }

      "employeeType is type5a" must {

        "employment started before 6 April 2020 and first furloughed 1 Jan 2021" must {

          "redirect to the next page when valid data is submitted" in {

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
            redirectLocation(result).value mustEqual routes.StatutoryLeavePayController.onPageLoad().url
          }
        }

      }

      "employeeType is type5b" must {

        "employment started after 30 Oct 2020 and first furloughed 1 Jan 2021" must {

          "redirect to the next page when valid data is submitted" in {

            val employeeStartDate = LocalDate.of(2020, 11, 1).minusDays(1)
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
              .set(OnPayrollBefore30thOct2020Page, false)
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
            redirectLocation(result).value mustEqual routes.StatutoryLeavePayController.onPageLoad().url
          }
        }
      }

    }
  }
}
