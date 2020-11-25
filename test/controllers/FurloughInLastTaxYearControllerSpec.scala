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
import forms.FurloughInLastTaxYearFormProvider
import models.requests.DataRequest
import models.{PayMethod, PaymentFrequency, UserAnswers}
import org.scalatestplus.mockito.MockitoSugar
import pages.FurloughInLastTaxYearPage
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.FurloughInLastTaxYearView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FurloughInLastTaxYearControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

  val formProvider = new FurloughInLastTaxYearFormProvider()
  val form = formProvider()

  val view = app.injector.instanceOf[FurloughInLastTaxYearView]

  lazy val furloughInLastTaxYearRoute = routes.FurloughInLastTaxYearController.onPageLoad().url

  lazy val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, furloughInLastTaxYearRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  lazy val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, furloughInLastTaxYearRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(
        "value" -> "true",
      )

  def controller(stubbedAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) =
    new FurloughInLastTaxYearController(
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

  val userAnswers =
    UserAnswers(userAnswersId)
      .withClaimPeriodStart(LocalDate.of(2020, 11, 1).toString)
      .withClaimPeriodEnd(LocalDate.of(2020, 11, 30).toString)
      .withFurloughStartDate(LocalDate.of(2020, 11, 1).toString)
      .withPaymentFrequency(PaymentFrequency.Weekly)
      .withPayMethod(PayMethod.Variable)

  "FurloughInLastTaxYear Controller" must {

    "return OK and the correct view for a GET" in {

      val result = controller(Some(userAnswers)).onPageLoad()(getRequest)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(getRequest, messages).toString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val updatedUserAnswers = UserAnswers(userAnswersId).set(FurloughInLastTaxYearPage, true).success.value

      val result = controller(Some(updatedUserAnswers)).onPageLoad()(getRequest)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true))(getRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {

      val result = controller(Some(userAnswers)).onSubmit()(postRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.CalculationUnsupportedController.multipleFurloughUnsupported().url
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val request =
        FakeRequest(POST, furloughInLastTaxYearRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller(Some(userAnswers)).onSubmit()(request)
      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm)(dataRequest, messages).toString
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
