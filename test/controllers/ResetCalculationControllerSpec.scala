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

import base.SpecBaseControllerSpecs
import controllers.actions.{DataRequiredActionImpl, FakeDataRetrievalAction}
import models.UserAnswers
import models.requests.OptionalDataRequest
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.CoreTestData
import views.html.ResetCalculationView

import scala.concurrent.ExecutionContext.Implicits.global

class ResetCalculationControllerSpec extends SpecBaseControllerSpecs with CoreTestData {

  lazy val resetCalculationRoute = routes.ResetCalculationController.onPageLoad().url
  val view = app.injector.instanceOf[ResetCalculationView]

  "resetCalculation Controller" must {

    "return OK and the correct view for a GET" in {
      val controller = new ResetCalculationController(
        messagesApi,
        identifier,
        new FakeDataRetrievalAction(Some(emptyUserAnswers)),
        dataRequired,
        component,
        view)
      val request = FakeRequest(GET, resetCalculationRoute).withCSRFToken
        .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

      val result = controller.onPageLoad()(request)

      status(result) mustEqual OK

      contentAsString(result) mustEqual view()(request, messages).toString
    }

    "redirect to Session Expired for a GET&POST if no existing data is found" in {
      val controller = new ResetCalculationController(
        messagesApi,
        identifier,
        new FakeDataRetrievalAction(Some(emptyUserAnswers)),
        new DataRequiredActionImpl() {
          override def retrieveData[A](request: OptionalDataRequest[A]): Option[UserAnswers] = None
        },
        component,
        view
      )

      val request = FakeRequest(GET, resetCalculationRoute)

      val getResult = controller.onPageLoad()(request)
      val postResult = controller.onSubmit()(request)

      status(getResult) mustEqual SEE_OTHER
      status(postResult) mustEqual SEE_OTHER

      redirectLocation(getResult).value mustEqual routes.SessionExpiredController.onPageLoad().url
      redirectLocation(postResult).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
