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

import assets.messages.BeenOnStatutoryLeaveMessages
import base.SpecBaseControllerSpecs
import config.featureSwitch.{FeatureSwitching, StatutoryLeaveFlow}
import forms.HasEmployeeBeenOnStatutoryLeaveFormProvider
import models.EmployeeStarted
import models.requests.DataRequest
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{EmployeeStartDatePage, EmployeeStartedPage, FirstFurloughDatePage, FurloughStartDatePage, OnPayrollBefore30thOct2020Page}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.LocalDateHelpers.{apr5th2020, apr6th2019, apr6th2020, feb1st2020}
import viewmodels.BeenOnStatutoryLeaveHelper
import views.ViewUtils.dateToString
import views.html.HasEmployeeBeenOnStatutoryLeaveView

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class HasEmployeeBeenOnStatutoryLeaveControllerSpec extends SpecBaseControllerSpecs with MockitoSugar with FeatureSwitching {

  val helper       = app.injector.instanceOf[BeenOnStatutoryLeaveHelper]
  val view         = app.injector.instanceOf[HasEmployeeBeenOnStatutoryLeaveView]
  val postAction   = controllers.routes.HasEmployeeBeenOnStatutoryLeaveController.onSubmit()
  val formProvider = new HasEmployeeBeenOnStatutoryLeaveFormProvider()

  def form(boundaryStart: String, boundaryEnd: String) = formProvider(boundaryStart, boundaryEnd)

  lazy val hasEmployeeBeenOnStatutoryLeaveRoute = routes.HasEmployeeBeenOnStatutoryLeaveController.onPageLoad().url

  val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, hasEmployeeBeenOnStatutoryLeaveRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val controller = new HasEmployeeBeenOnStatutoryLeaveController(messagesApi,
                                                                 mockSessionRepository,
                                                                 navigator,
                                                                 identifier,
                                                                 dataRetrieval,
                                                                 dataRequired,
                                                                 formProvider,
                                                                 helper,
                                                                 component,
                                                                 view)

  "HasEmployeeBeenOnStatutoryLeave Controller" when {

    "onPageLoad" when {

      "employee is type 3" when {

        "the day before employee is first furloughed is before 5th April 2020" must {

          "return OK and the correct view for a GET" in {

            val firstFurloughDate = LocalDate.parse("2020-04-05")
            val furloughStartDate = LocalDate.parse("2021-03-01")
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
              view(form(boundaryStart, boundaryEnd), postAction, boundaryStart, boundaryEnd)(dataRequest, messages).toString
          }
        }

        "day before employee is first furloughed is after 5th April 2020" must {

          "return OK and the correct view for a GET" in {

            val firstFurloughDate = LocalDate.parse("2020-04-06")
            val furloughStartDate = LocalDate.parse("2021-03-01")
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
              view(form(boundaryStart, boundaryEnd), postAction, boundaryStart, boundaryEnd)(dataRequest, messages).toString
          }
        }
      }

      "employee is type 4" when {

        "the day before employee is first furloughed is before 5th April 2020" must {

          "return OK and the correct view for a GET" in {

            val employeeStartDate = feb1st2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2020-04-05")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val boundaryStart     = BeenOnStatutoryLeaveMessages.dayEmploymentStarted
            val boundaryEnd       = dateToString(firstFurloughDate.minusDays(1))

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
              view(form(boundaryStart, boundaryEnd), postAction, boundaryStart, boundaryEnd)(dataRequest, messages).toString
          }
        }

        "day before employee is first furloughed is after 5th April 2020" must {

          "return OK and the correct view for a GET" in {

            val employeeStartDate = feb1st2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2020-04-06")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val boundaryStart     = BeenOnStatutoryLeaveMessages.dayEmploymentStarted
            val boundaryEnd       = dateToString(apr5th2020)

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
              view(form(boundaryStart, boundaryEnd), postAction, boundaryStart, boundaryEnd)(dataRequest, messages).toString
          }
        }
      }

      "employee is type 5a" when {

        "employment started before 6 April 2020 and first furloughed 1 Jan 2021" must {

          "return OK and the correct view for a GET" in {

            val employeeStartDate = apr6th2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2021-01-01")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val boundaryStart     = dateToString(apr6th2020)
            val boundaryEnd       = dateToString(firstFurloughDate.minusDays(1))

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
              view(form(boundaryStart, boundaryEnd), postAction, boundaryStart, boundaryEnd)(dataRequest, messages).toString
          }
        }

        "employment started after 6 April 2020 and first furloughed 1 Jan 2021" must {

          "return OK and the correct view for a GET" in {

            val employeeStartDate = apr6th2020.plusDays(1)
            val firstFurloughDate = LocalDate.parse("2021-01-01")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val boundaryStart     = BeenOnStatutoryLeaveMessages.dayEmploymentStarted
            val boundaryEnd       = dateToString(firstFurloughDate.minusDays(1))

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
              view(form(boundaryStart, boundaryEnd), postAction, boundaryStart, boundaryEnd)(dataRequest, messages).toString
          }
        }
      }

      "employee is type 5b" when {

        "employment started before 6 April 2020 and first furloughed 01 May 2021" must {

          "return OK and the correct view for a GET" in {

            val employeeStartDate = apr6th2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2021-05-01")
            val furloughStartDate = LocalDate.parse("2021-05-21")
            val boundaryStart     = dateToString(apr6th2020)
            val boundaryEnd       = dateToString(firstFurloughDate.minusDays(1))

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
              view(form(boundaryStart, boundaryEnd), postAction, boundaryStart, boundaryEnd)(dataRequest, messages).toString
          }
        }

        "employment started after 6 April 2020 and first furloughed 01 May 2021" must {

          "return OK and the correct view for a GET" in {

            val employeeStartDate = apr6th2020.plusDays(1)
            val firstFurloughDate = LocalDate.parse("2021-05-01")
            val furloughStartDate = LocalDate.parse("2021-05-21")
            val boundaryStart     = BeenOnStatutoryLeaveMessages.dayEmploymentStarted
            val boundaryEnd       = dateToString(firstFurloughDate.minusDays(1))

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
              view(form(boundaryStart, boundaryEnd), postAction, boundaryStart, boundaryEnd)(dataRequest, messages).toString
          }
        }
      }
    }

    "onSubmit" when {

      "employeeType is type3" must {

        "day before employee is first furloughed is before 5th April 2020" must {

          "redirect to the next page when valid data is submitted" in {
            enable(StatutoryLeaveFlow)
            val firstFurloughDate = LocalDate.parse("2020-04-05")
            val furloughStartDate = LocalDate.parse("2021-03-01")

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
            val request =
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "true"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.NumberOfStatLeaveDaysController.onPageLoad().url
          }

          "return a Bad Request and errors when invalid data is submitted" in {

            val firstFurloughDate = LocalDate.parse("2020-04-05")
            val furloughStartDate = LocalDate.parse("2021-03-01")
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
            val request =
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                .withFormUrlEncodedBody(("value", "invalid value"))

            val boundForm   = form(boundaryStart, boundaryEnd).bind(Map("value" -> "invalid value"))
            val result      = controller.onSubmit()(request)
            val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

            status(result) mustEqual BAD_REQUEST
            contentAsString(result) mustEqual
              view(boundForm, postAction, boundaryStart, boundaryEnd)(dataRequest, messages).toString
          }

          "route back to the root page when the Statutory Leave flow feature switch is disabled" in {
            disable(StatutoryLeaveFlow)
            val firstFurloughDate = LocalDate.parse("2020-04-05")
            val furloughStartDate = LocalDate.parse("2021-03-01")

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
            val request =
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "true"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.RootPageController.onPageLoad().url
            enable(StatutoryLeaveFlow)
          }
        }

        "day before employee is first furloughed is after 5th April 2020" must {

          "redirect to the next page when valid data is submitted" in {

            val firstFurloughDate = LocalDate.parse("2020-04-06")
            val furloughStartDate = LocalDate.parse("2021-03-01")

            val userAnswers = emptyUserAnswers
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val request =
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "true"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.NumberOfStatLeaveDaysController.onPageLoad().url
          }

          "return a Bad Request and errors when invalid data is submitted" in {

            val firstFurloughDate = LocalDate.parse("2020-04-06")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val boundaryStart     = dateToString(apr6th2019)
            val boundaryEnd       = dateToString(apr5th2020)

            val userAnswers = emptyUserAnswers
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val request =
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                .withFormUrlEncodedBody(("value", "invalid value"))

            val boundForm   = form(boundaryStart, boundaryEnd).bind(Map("value" -> "invalid value"))
            val result      = controller.onSubmit()(request)
            val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

            status(result) mustEqual BAD_REQUEST
            contentAsString(result) mustEqual
              view(boundForm, postAction, boundaryStart, boundaryEnd)(dataRequest, messages).toString
          }

          "route back to the root page when the Statutory Leave flow feature switch is disabled" in {
            disable(StatutoryLeaveFlow)
            val firstFurloughDate = LocalDate.parse("2020-04-06")
            val furloughStartDate = LocalDate.parse("2021-03-01")

            val userAnswers = emptyUserAnswers
              .set(FirstFurloughDatePage, firstFurloughDate)
              .success
              .value
              .set(FurloughStartDatePage, furloughStartDate)
              .success
              .value
              .set(EmployeeStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
              .success
              .value

            when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
            val request =
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "true"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.RootPageController.onPageLoad().url
            enable(StatutoryLeaveFlow)
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
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "true"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.NumberOfStatLeaveDaysController.onPageLoad().url
          }

          "return a Bad Request and errors when invalid data is submitted" in {

            val employeeStartDate = feb1st2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2020-04-05")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val boundaryStart     = BeenOnStatutoryLeaveMessages.dayEmploymentStarted
            val boundaryEnd       = dateToString(firstFurloughDate.minusDays(1))

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
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                .withFormUrlEncodedBody(("value", "invalid value"))

            val boundForm   = form(boundaryStart, boundaryEnd).bind(Map("value" -> "invalid value"))
            val result      = controller.onSubmit()(request)
            val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

            status(result) mustEqual BAD_REQUEST
            contentAsString(result) mustEqual
              view(boundForm, postAction, boundaryStart, boundaryEnd)(dataRequest, messages).toString
          }

          "route back to the root page when the Statutory Leave flow feature switch is disabled" in {
            disable(StatutoryLeaveFlow)
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
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "true"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.RootPageController.onPageLoad().url
            enable(StatutoryLeaveFlow)
          }
        }

        "day before employee is first furloughed is after 5th April 2020" must {

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
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "true"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.NumberOfStatLeaveDaysController.onPageLoad().url
          }

          "return a Bad Request and errors when invalid data is submitted" in {

            val employeeStartDate = feb1st2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2020-04-06")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val boundaryStart     = BeenOnStatutoryLeaveMessages.dayEmploymentStarted
            val boundaryEnd       = dateToString(apr5th2020)

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
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                .withFormUrlEncodedBody(("value", "invalid value"))

            val boundForm   = form(boundaryStart, boundaryEnd).bind(Map("value" -> "invalid value"))
            val result      = controller.onSubmit()(request)
            val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

            status(result) mustEqual BAD_REQUEST
            contentAsString(result) mustEqual
              view(boundForm, postAction, boundaryStart, boundaryEnd)(dataRequest, messages).toString
          }

          "route back to the root page when the Statutory Leave flow feature switch is disabled" in {
            disable(StatutoryLeaveFlow)
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
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "true"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.RootPageController.onPageLoad().url
            enable(StatutoryLeaveFlow)
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
            val request = FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
              .withFormUrlEncodedBody(("value", "true"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.NumberOfStatLeaveDaysController.onPageLoad().url
          }

          "return a Bad Request and errors when invalid data is submitted" in {

            val employeeStartDate = apr6th2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2021-01-01")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val boundaryStart     = dateToString(apr6th2020)
            val boundaryEnd       = dateToString(firstFurloughDate.minusDays(1))

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
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                .withFormUrlEncodedBody(("value", "invalid value"))

            val boundForm   = form(boundaryStart, boundaryEnd).bind(Map("value" -> "invalid value"))
            val result      = controller.onSubmit()(request)
            val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

            status(result) mustEqual BAD_REQUEST
            contentAsString(result) mustEqual
              view(boundForm, postAction, boundaryStart, boundaryEnd)(dataRequest, messages).toString
          }

          "route back to the root page when the Statutory Leave flow feature switch is disabled" in {
            disable(StatutoryLeaveFlow)
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
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "true"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.RootPageController.onPageLoad().url
            enable(StatutoryLeaveFlow)
          }
        }

        "employment started after 6 April 2020 and first furloughed 1 Jan 2021" must {

          "redirect to the next page when valid data is submitted" in {

            val employeeStartDate = apr6th2020.plusDays(1)
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
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "true"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.NumberOfStatLeaveDaysController.onPageLoad().url
          }

          "return a Bad Request and errors when invalid data is submitted" in {

            val employeeStartDate = apr6th2020.plusDays(1)
            val firstFurloughDate = LocalDate.parse("2021-01-01")
            val furloughStartDate = LocalDate.parse("2021-03-01")
            val boundaryStart     = BeenOnStatutoryLeaveMessages.dayEmploymentStarted
            val boundaryEnd       = dateToString(firstFurloughDate.minusDays(1))

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
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                .withFormUrlEncodedBody(("value", "invalid value"))

            val boundForm   = form(boundaryStart, boundaryEnd).bind(Map("value" -> "invalid value"))
            val result      = controller.onSubmit()(request)
            val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

            status(result) mustEqual BAD_REQUEST
            contentAsString(result) mustEqual
              view(boundForm, postAction, boundaryStart, boundaryEnd)(dataRequest, messages).toString
          }

          "route back to the root page when the Statutory Leave flow feature switch is disabled" in {
            disable(StatutoryLeaveFlow)
            val employeeStartDate = apr6th2020.plusDays(1)
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
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "true"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.RootPageController.onPageLoad().url
            enable(StatutoryLeaveFlow)
          }
        }
      }

      "employeeType is type5b" must {

        "employment started before 6 April 2020 and first furloughed 01 May 2021" must {

          "redirect to the next page when valid data is submitted" in {

            val employeeStartDate = apr6th2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2021-05-01")
            val furloughStartDate = LocalDate.parse("2021-05-21")

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
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "true"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.NumberOfStatLeaveDaysController.onPageLoad().url
          }

          "return a Bad Request and errors when invalid data is submitted" in {

            val employeeStartDate = apr6th2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2021-05-01")
            val furloughStartDate = LocalDate.parse("2021-05-21")
            val boundaryStart     = dateToString(apr6th2020)
            val boundaryEnd       = dateToString(firstFurloughDate.minusDays(1))

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
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                .withFormUrlEncodedBody(("value", "invalid value"))

            val boundForm   = form(boundaryStart, boundaryEnd).bind(Map("value" -> "invalid value"))
            val result      = controller.onSubmit()(request)
            val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

            status(result) mustEqual BAD_REQUEST
            contentAsString(result) mustEqual
              view(boundForm, postAction, boundaryStart, boundaryEnd)(dataRequest, messages).toString
          }

          "route back to the root page when the Statutory Leave flow feature switch is disabled" in {
            disable(StatutoryLeaveFlow)
            val employeeStartDate = apr6th2020.minusDays(1)
            val firstFurloughDate = LocalDate.parse("2021-05-01")
            val furloughStartDate = LocalDate.parse("2021-05-21")

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
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "true"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.RootPageController.onPageLoad().url
            enable(StatutoryLeaveFlow)
          }
        }

        "employment started after 6 April 2020 and first furloughed 01 May 2021" must {

          "redirect to the next page when valid data is submitted" in {

            val employeeStartDate = apr6th2020.plusDays(1)
            val firstFurloughDate = LocalDate.parse("2021-05-01")
            val furloughStartDate = LocalDate.parse("2021-05-21")

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
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "true"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.NumberOfStatLeaveDaysController.onPageLoad().url
          }

          "return a Bad Request and errors when invalid data is submitted" in {

            val employeeStartDate = apr6th2020.plusDays(1)
            val firstFurloughDate = LocalDate.parse("2021-05-01")
            val furloughStartDate = LocalDate.parse("2021-05-21")
            val boundaryStart     = BeenOnStatutoryLeaveMessages.dayEmploymentStarted
            val boundaryEnd       = dateToString(firstFurloughDate.minusDays(1))

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
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                .withFormUrlEncodedBody(("value", "invalid value"))

            val boundForm   = form(boundaryStart, boundaryEnd).bind(Map("value" -> "invalid value"))
            val result      = controller.onSubmit()(request)
            val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

            status(result) mustEqual BAD_REQUEST
            contentAsString(result) mustEqual
              view(boundForm, postAction, boundaryStart, boundaryEnd)(dataRequest, messages).toString
          }

          "route back to the root page when the Statutory Leave flow feature switch is disabled" in {
            disable(StatutoryLeaveFlow)
            val employeeStartDate = apr6th2020.plusDays(1)
            val firstFurloughDate = LocalDate.parse("2021-05-01")
            val furloughStartDate = LocalDate.parse("2021-05-21")

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
              FakeRequest(POST, hasEmployeeBeenOnStatutoryLeaveRoute)
                .withFormUrlEncodedBody(("value", "true"))

            val result = controller.onSubmit()(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.RootPageController.onPageLoad().url
            enable(StatutoryLeaveFlow)
          }
        }
      }
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request = FakeRequest(GET, hasEmployeeBeenOnStatutoryLeaveRoute)
      val result  = controller.onPageLoad()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request =
        FakeRequest(method = POST, path = hasEmployeeBeenOnStatutoryLeaveRoute)
          .withFormUrlEncodedBody(("value", "111"))

      val result = controller.onSubmit()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
