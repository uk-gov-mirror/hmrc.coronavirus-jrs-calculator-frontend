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
import config.featureSwitch.{FeatureSwitching, StatutoryLeaveFlow}
import forms.StatutoryLeavePayFormProvider
import models.requests.DataRequest
import models.{Amount, NormalMode, UserAnswers}
import navigation.Navigator
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.StatutoryLeavePayPage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.StatutoryLeavePayView

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class StatutoryLeavePayControllerSpec extends SpecBaseControllerSpecs with MockitoSugar with FeatureSwitching {

  val formProvider = new StatutoryLeavePayFormProvider()
  val form         = formProvider()

  lazy val statutoryLeavePayRoute = routes.StatutoryLeavePayController.onPageLoad().url

  val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, statutoryLeavePayRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val view = app.injector.instanceOf[StatutoryLeavePayView]

  val controller = new StatutoryLeavePayController(messagesApi,
                                                   mockSessionRepository,
                                                   navigator,
                                                   identifier,
                                                   dataRetrieval,
                                                   dataRequired,
                                                   formProvider,
                                                   component,
                                                   view)

  "StatutoryLeavePay Controller" must {

    "onPageLoad" should {

      "return OK and the correct view for a GET" in {
        val userAnswers = emptyUserAnswers
        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
        val result = controller.onPageLoad()(getRequest)

        status(result) mustEqual OK
        val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

        contentAsString(result) mustEqual
          view(form)(dataRequest, messages).toString
      }

      "populate the view correctly on a GET when the question has previously been answered" in {
        val amountThatShouldBePrePopulated = BigDecimal(420.10)
        val userAnswers = emptyUserAnswers
          .set(StatutoryLeavePayPage, Amount(amountThatShouldBePrePopulated.bigDecimal))
          .success
          .value
        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))

        val result = controller.onPageLoad()(getRequest)
        status(result) mustEqual OK
        val dataRequest = DataRequest(getRequest, userAnswers.id, userAnswers)

        contentAsString(result) mustEqual
          view(form.fill(Amount(amountThatShouldBePrePopulated.bigDecimal)))(dataRequest, messages).toString
      }

      "redirect to Session Expired for a GET if no existing data is found" in {
        when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
        val result = controller.onPageLoad()(getRequest)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
      }
    }

    "onSubmit" should {

      "redirect to the next page when valid data is submitted" in {
        val userAnswers = emptyUserAnswers
        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
        val request =
          FakeRequest(POST, statutoryLeavePayRoute)
            .withFormUrlEncodedBody(("value", "111"))

        val result = controller.onSubmit()(request)

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.PartTimeQuestionController.onPageLoad().url
      }

      "redirect to the root page when the feature switch is disabled" in {
        disable(StatutoryLeaveFlow)
        val userAnswers = emptyUserAnswers
        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
        val request =
          FakeRequest(POST, statutoryLeavePayRoute)
            .withFormUrlEncodedBody(("value", "111"))

        val result = controller.onSubmit()(request)

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.RootPageController.onPageLoad().url
        enable(StatutoryLeaveFlow)
      }

      "return a Bad Request and errors when invalid data is submitted" in {
        val userAnswers = emptyUserAnswers
        when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))
        val request =
          FakeRequest(POST, statutoryLeavePayRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val result      = controller.onSubmit()(request)
        val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm)(dataRequest, messages).toString
      }

      "redirect to Session Expired for a POST if no existing data is found" in {
        when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
        val request = FakeRequest(POST, statutoryLeavePayRoute)
        val result  = controller.onSubmit()(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
      }
    }
  }
}
