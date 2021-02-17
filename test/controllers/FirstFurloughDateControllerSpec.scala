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

import base.SpecBaseControllerSpecs
import controllers.actions.DataRetrievalActionImpl
import forms.FirstFurloughDateFormProvider
import models.PayMethod.Variable
import models.PaymentFrequency.Weekly
import models.{EmployeeStarted, UserAnswers}
import pages.FirstFurloughDatePage
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import views.html.FirstFurloughDateView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FirstFurloughDateControllerSpec extends SpecBaseControllerSpecs {

  val formProvider = new FirstFurloughDateFormProvider()
  private def form: Form[LocalDate] = formProvider()
  val validAnswer = LocalDate.now(ZoneOffset.UTC)

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

  "FirstFurloughDate Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(getRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form)(getRequest, messages).toString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = UserAnswers(userAnswersId).set(FirstFurloughDatePage, validAnswer).success.value
      val result = controller(Some(userAnswers)).onPageLoad()(getRequest)

      status(result) mustBe OK
      contentAsString(result) mustEqual
        view(form.fill(validAnswer))(getRequest, messages).toString()

    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val request =
        FakeRequest(POST, firstFurLoughDateStartRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("firstFurloughDate", "invalid value"))

      val boundForm = form.bind(Map("firstFurloughDate" -> "invalid value"))

      val result = controller().onSubmit()(request)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm)(request, messages).toString()

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
