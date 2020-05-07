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

package controllers.actions

import base.SpecBaseWithApplication
import controllers.actions.FeatureFlag.{TopUpJourneyFlag, VariableJourneyFlag}
import controllers.routes
import handlers.ErrorHandler
import play.api.mvc.Results
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global

class FeatureFlagActionSpec extends SpecBaseWithApplication {

  class Harness(identify: IdentifierAction, flagAction: FeatureFlagAction) {
    def onPageLoad() = (identify andThen flagAction) {
      Results.Ok
    }
  }

  val eh = injector.instanceOf[ErrorHandler]

  "FeatureFlagAction" must {

    "Allow requests when no feature flag is provided" in {
      val application = applicationBuilder().build()

      val action = new FeatureFlagAction(None, application.configuration, eh, implicitly)

      val identify = application.injector.instanceOf[IdentifierAction]

      val controller = new Harness(identify, action)

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "Allow requests when feature flag is true" in {
      val application = applicationBuilder(config = Map(VariableJourneyFlag.key -> true)).build()

      val action = new FeatureFlagAction(Some(VariableJourneyFlag), application.configuration, eh, implicitly)

      val identify = application.injector.instanceOf[IdentifierAction]

      val controller = new Harness(identify, action)

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "Redirect to coming soon when feature flag is false" in {
      val application = applicationBuilder(config = Map(VariableJourneyFlag.key -> false)).build()

      val action = new FeatureFlagAction(Some(VariableJourneyFlag), application.configuration, eh, implicitly)

      val identify = application.injector.instanceOf[IdentifierAction]

      val controller = new Harness(identify, action)

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER

      redirectLocation(result).get mustBe routes.ComingSoonController.onPageLoad().url
    }

    "Return 404 when topup flag is false" in {
      val application = applicationBuilder(config = Map(TopUpJourneyFlag.key -> false)).build()

      val action = new FeatureFlagAction(Some(TopUpJourneyFlag), application.configuration, eh, implicitly)

      val identify = application.injector.instanceOf[IdentifierAction]

      val controller = new Harness(identify, action)

      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustBe NOT_FOUND
    }

  }
}
