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

package views.helper

import messages.JRSExtensionConfirmationMessages._
import models._
import views.BaseSelectors
import views.behaviours.ViewBehaviours
import views.html.helper.{additionalPaymentUpToEightyPercent, confirmationNextSteps}

import java.time.LocalDate

class ConfirmationNextStepsSpec extends ViewBehaviours {

  val nextSteps = app.injector.instanceOf[confirmationNextSteps]

  val claimPeriod = Period(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 8))

  object Selectors extends BaseSelectors

  "confirmationNextSteps" must {

    val html         = nextSteps(claimPeriod)
    implicit val doc = asDocument(dummyView(html))

    behave like pageWithExpectedMessages(
      Seq(
        Selectors.h2(1)       -> h2NextSteps,
        Selectors.numbered(1) -> nextStepsListMessages(1, claimPeriod),
        Selectors.numbered(2) -> nextStepsListMessages(2, claimPeriod),
        Selectors.numbered(3) -> nextStepsListMessages(3, claimPeriod),
        Selectors.numbered(4) -> nextStepsListMessages(4, claimPeriod),
        Selectors.numbered(5) -> nextStepsListMessages(5, claimPeriod)
      ))
  }
}
