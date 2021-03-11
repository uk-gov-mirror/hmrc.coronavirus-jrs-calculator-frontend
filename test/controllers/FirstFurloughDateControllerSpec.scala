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

import java.time.{LocalDate, ZoneOffset}

import assets.messages.PartTimeNormalHoursMessages.dateToString
import base.SpecBaseControllerSpecs
import config.featureSwitch.{ExtensionTwoNewStarterFlow, FeatureSwitching}
import controllers.actions.DataRetrievalActionImpl
import forms.FirstFurloughDateFormProvider
import models.EmployeeStarted.OnOrBefore1Feb2019
import models.PayMethod.Variable
import models.PaymentFrequency.Weekly
import models.{EmployeeStarted, UserAnswers}
import pages.{EmployeeStartedPage, FirstFurloughDatePage, FurloughStartDatePage, OnPayrollBefore30thOct2020Page, PreviousFurloughPeriodsPage}
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import views.html.FirstFurloughDateView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FirstFurloughDateControllerSpec extends SpecBaseControllerSpecs with FeatureSwitching {

  val formProvider      = new FirstFurloughDateFormProvider()
  val validAnswer       = LocalDate.now(ZoneOffset.UTC)
  val firstFurloughDate = validAnswer.plusMonths(1)

  private def form: Form[LocalDate] = formProvider(firstFurloughDate)

  lazy val firstFurLoughDateStartRoute: String = routes.FirstFurloughDateController.onPageLoad().url

  override val emptyUserAnswers = UserAnswers(userAnswersId)

  val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, firstFurLoughDateStartRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, firstFurLoughDateStartRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(
        "firstFurloughDate.day"   -> validAnswer.getDayOfMonth.toString,
        "firstFurloughDate.month" -> validAnswer.getMonthValue.toString,
        "firstFurloughDate.year"  -> validAnswer.getYear.toString
      )

  val view = app.injector.instanceOf[FirstFurloughDateView]

  val nov9th2020            = LocalDate.of(2020, 11, 9)
  val mar8th2020: LocalDate = LocalDate.of(2020, 3, 8)
  val may8th2021: LocalDate = LocalDate.of(2021, 5, 8)

  val nov1st2020            = LocalDate.of(2020, 11, 1)
  val mar1st2020: LocalDate = LocalDate.of(2020, 3, 1)
  val may1st2021: LocalDate = LocalDate.of(2021, 5, 1)

  def controller(stubbedAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) =
    new FirstFurloughDateController(
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

  def userAnswersEmployedBefore1stFeb2019(): UserAnswers =
    UserAnswers(userAnswersId)
      .withPayMethod(Variable)
      .withEmployeeStartedOnOrBefore1Feb2019()
      .set(FurloughStartDatePage, mar8th2020)
      .success
      .value

  def userAnswersEmployedAfter1stFeb2019(isOnPayrollBefore30thOct: Boolean): UserAnswers =
    UserAnswers(userAnswersId)
      .withFurloughStartDate(nov9th2020.toString)
      .withPayMethod(Variable)
      .withEmployeeStartedAfter1Feb2019()
      .withEmployeeStartDate("2020,3,20")
      .set(OnPayrollBefore30thOct2020Page, isOnPayrollBefore30thOct)
      .success
      .value

  def multiScenario(userAnswers: UserAnswers, resultDate: LocalDate) = {

    s"populate the view correctly on a GET when the question has previously been answered for ${dateToString(resultDate)}" in {

      val result = controller(
        Some(
          userAnswers
            .set(FirstFurloughDatePage, resultDate.plusDays(7))
            .success
            .value)).onPageLoad()(getRequest)

      status(result) mustBe OK
      contentAsString(result) mustEqual
        view(form.fill(resultDate.plusDays(7)), resultDate)(getRequest, messages).toString()
    }

    s"return a Bad Request and errors when invalid data is submitted for ${dateToString(resultDate)}" in {

      val request =
        FakeRequest(POST, firstFurLoughDateStartRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("firstFurloughDate", "invalid value"))

      val boundForm = form.bind(Map("firstFurloughDate" -> "invalid value"))

      val result = controller(Some(userAnswers)).onSubmit()(request)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm, resultDate)(request, messages).toString()
    }

  }

  "FirstFurloughDate Controller" must {

    "return OK and the correct view for a GET" when {

      "the user selected Yes to the employee working before Feb 2019 and yes on the previous furlough periods page" in {

        val result = controller(Some(userAnswersEmployedBefore1stFeb2019())).onPageLoad()(getRequest)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, mar1st2020)(getRequest, messages).toString()
      }

      "the user selected No to the employee Working before Feb 2019" must {
        "show 1st November 2020 content when the user answered Yes on OnPayrollBefore30thOct2020Page" in {
          val result = controller(Some(userAnswersEmployedAfter1stFeb2019(true))).onPageLoad()(getRequest)

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form, nov1st2020)(getRequest, messages).toString()
        }

        "show 1st May 2021 content when the user answered No on OnPayrollBefore30thOct2020Page" in {
          val result = controller(Some(userAnswersEmployedAfter1stFeb2019(false))).onPageLoad()(getRequest)

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form, may1st2021)(getRequest, messages).toString()
        }
      }
    }

    multiScenario(userAnswersEmployedBefore1stFeb2019(), mar1st2020)
    multiScenario(userAnswersEmployedAfter1stFeb2019(true), nov1st2020)
    multiScenario(userAnswersEmployedAfter1stFeb2019(false), may1st2021)

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
