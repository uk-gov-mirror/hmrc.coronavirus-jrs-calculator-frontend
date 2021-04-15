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
import views.html.helper.{additionalPaymentUpToEightyPercent, confirmationHeading}

import java.time.LocalDate

class AdditionalPaymentUpToEightyPercentSpec extends ViewBehaviours {

  val headingHelper = app.injector.instanceOf[additionalPaymentUpToEightyPercent]

  object Selectors extends BaseSelectors

  val calcResult = PhaseTwoFurloughCalculationResult(
    500,
    Seq(
      PhaseTwoFurloughBreakdown(
        grant = Amount(500),
        paymentWithPeriod = RegularPaymentWithPhaseTwoPeriod(
          regularPay = Amount(500),
          referencePay = Amount(500),
          phaseTwoPeriod = PhaseTwoPeriod(
            periodWithPaymentDate = FullPeriodWithPaymentDate(
              period = FullPeriod(Period(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 8))),
              paymentDate = PaymentDate(LocalDate.of(2020, 12, 31))
            ),
            actualHours = None,
            usualHours = None
          )
        ),
        furloughCap = FullPeriodCap(2500)
      )
    )
  )

  "additionalPaymentUpToEightyPercent" must {

    "Only render the Still Pay NIC and Pensions message" must {

      val html         = headingHelper(calcResult, EightyPercent)
      implicit val doc = asDocument(dummyView(html))

      behave like pageWithExpectedMessages(
        Seq(
          Selectors.p(1) -> AdditionalPaymentBlock.stillPayNICandPension
        ))
    }

    "render the expected 70% top up content" must {

      val html         = headingHelper(calcResult, SeventyPercent)
      implicit val doc = asDocument(dummyView(html))

      behave like pageWithExpectedMessages(
        Seq(
          Selectors.p(1) -> AdditionalPaymentBlock.p1(62.5), // 500 - ((500 / 80) * 70)
          Selectors.p(2) -> AdditionalPaymentBlock.stillPayNICandPension
        ))
    }

    "render the expected 60% top up content" must {

      val html         = headingHelper(calcResult, SixtyPercent)
      implicit val doc = asDocument(dummyView(html))

      behave like pageWithExpectedMessages(
        Seq(
          Selectors.p(1) -> AdditionalPaymentBlock.p1(125), // 500 - ((500 / 80) * 60)
          Selectors.p(2) -> AdditionalPaymentBlock.stillPayNICandPension
        ))
    }
  }
}
