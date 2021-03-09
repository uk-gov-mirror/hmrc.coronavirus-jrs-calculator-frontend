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
import config.FrontendAppConfig
import config.featureSwitch.{FeatureSwitching, ShowNewStartPage}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.{RootPageView, StartPageView}

import scala.concurrent.Future

class RootPageControllerSpec extends SpecBaseControllerSpecs with MockitoSugar with FeatureSwitching {

  class Setup {
    val view = injector.instanceOf[RootPageView]
    val newStartView = injector.instanceOf[StartPageView]
    val appConfig = injector.instanceOf[FrontendAppConfig]

    val controller = new RootPageController(messagesApi, component, view, newStartView)(appConfig)

    when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(emptyUserAnswers))

    val request = FakeRequest(GET, routes.RootPageController.onPageLoad().url)
  }

  "RootPage Controller" when {

    "The new start page feature switch is disabled" must {

      "return OK and the old view for a GET" in new Setup {

        disable(ShowNewStartPage)
      val result  = controller.start()(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view()(request, messages).toString
      }
    }

    "The new start page feature switch is enabled" must {

      "return OK and the new view for a GET" in new Setup {

        enable(ShowNewStartPage)

        val result = controller.start()(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual newStartView(controllers.routes.ClaimPeriodStartController.onPageLoad())(request, messages).toString
      }
    }
  }
}
