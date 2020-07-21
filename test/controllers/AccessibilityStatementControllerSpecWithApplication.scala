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
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import views.html.AccessibilityStatementView

import scala.concurrent.Future

class AccessibilityStatementControllerSpecWithApplication extends SpecBaseControllerSpecs {

  val view = app.injector.instanceOf[AccessibilityStatementView]

  val controller = new AccessibilityStatementController(component, view)

  "Accessibility Statement Controller" must {
    "return OK and the correct view for a GET" in {
      val problemUri = "foo"
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)
      val request = FakeRequest(GET, routes.AccessibilityStatementController.onPageLoad(problemUri).url)

      val result = controller.onPageLoad(problemUri)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(problemUri)(request, messages).toString
    }

    "sanitise input" in {
      val problemUri = "<script/>"
      val encodedUri = HtmlFormat.escape(problemUri).toString
      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)

      val request = FakeRequest(GET, routes.AccessibilityStatementController.onPageLoad(problemUri).url)
      val result = controller.onPageLoad(problemUri)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(encodedUri)(request, messages).toString
    }
  }
}
