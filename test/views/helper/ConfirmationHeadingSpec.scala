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
import views.html.helper.confirmationHeading

import java.time.LocalDate

class ConfirmationHeadingSpec extends ViewBehaviours {

  val headingHelper = app.injector.instanceOf[confirmationHeading]

  object Selectors extends BaseSelectors

  val claimPeriod = Period(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 8))
  val claimAmount = 500

  "headingHelper" must {

    "render the expected 80% heading when not passed a custom percentage" must {

      val html         = headingHelper(claimPeriod, claimAmount)
      implicit val doc = asDocument(dummyView(html))

      behave like pageWithExpectedMessages(
        Seq(
          Selectors.h1   -> heading,
          Selectors.p(1) -> ConfirmationBlock.p1(80),
          Selectors.p(2) -> ConfirmationBlock.p2(claimPeriod),
          Selectors.p(3) -> ConfirmationBlock.claimAmount(claimAmount)
        ))
    }

    "render the supplied 70% heading when passed a custom percentage of 70%" must {

      val html         = headingHelper(claimPeriod, claimAmount, SeventyPercent)
      implicit val doc = asDocument(dummyView(html))

      behave like pageWithExpectedMessages(
        Seq(
          Selectors.h1   -> heading,
          Selectors.p(1) -> ConfirmationBlock.p1(70),
          Selectors.p(2) -> ConfirmationBlock.p2(claimPeriod),
          Selectors.p(3) -> ConfirmationBlock.claimAmount(claimAmount)
        ))
    }

  }

}
