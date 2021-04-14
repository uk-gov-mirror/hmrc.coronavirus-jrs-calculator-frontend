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
import views.html.helper.{additionalPaymentUpToEightyPercent, calculationDate}

import java.time.LocalDate

class CalculationDateSpec extends ViewBehaviours {

  val headingHelper = app.injector.instanceOf[calculationDate]

  object Selectors extends BaseSelectors

  val version = "2.0"

  "calculationDate" must {

    val html         = headingHelper(version)
    implicit val doc = asDocument(dummyView(html))

    behave like pageWithExpectedMessages(
      Seq(
        Selectors.p(1) -> dateAndCalculatorVersion(dateToString(LocalDate.now()), version),
        Selectors.p(2) -> disclaimerTopPage
      ))
  }
}
