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
import controllers.actions.DataRetrievalActionImpl
import forms.PreviousFurloughPeriodsFormProvider
import models.UserAnswers
import models.requests.DataRequest
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.PreviousFurloughPeriodsPage
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.PreviousFurloughPeriodsView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PreviousFurloughPeriodsControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

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

  val userAnswers =
    UserAnswers(userAnswersId)
      .set(PreviousFurloughPeriodsPage, true)
      .success
      .value

  "PreviousFurloughPeriods Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(getRequest)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(getRequest, messages).toString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val result = controller(Some(userAnswers)).onPageLoad()(getRequest)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true))(getRequest, messages).toString

    }

    "redirect to the next page when the value 'true' is submitted" in {

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))

      val result = controller(Some(userAnswers)).onSubmit()(postRequest)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.FirstFurloughDateController.onPageLoad().url
    }

    "redirect to the next page when the value 'false' is submitted " in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers))

      lazy val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest(POST, previousFurloughPeriodsRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(
            "value" -> "false"
          )

      val result = controller(Some(userAnswers)).onSubmit()(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.PayDateController.onPageLoad(1).url
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val request =
        FakeRequest(POST, previousFurloughPeriodsRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result      = controller(Some(userAnswers)).onSubmit()(request)
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
