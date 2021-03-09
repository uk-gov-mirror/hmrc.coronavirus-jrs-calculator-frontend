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

package views

import assets.messages.StartPageMessages
import models.requests.DataRequest
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.StartPageView

class StartPageViewSpec extends ViewBehaviours {

  object Selectors extends BaseSelectors {
    val whatCanYouClaim = "#whatCanYouClaim"
    val about           = "#about"
    val whoCanUse       = "#whoCanUse"
    val whoCannotUse    = "#whoCannotUse"
    val beforeYouStart  = "#beforeYouStart"
    val julyOnwards     = "#julyOnwards"
  }

  val messageKeyPrefix    = "startPage"
  val view: StartPageView = injector.instanceOf[StartPageView]

  val expectedContent = Seq(
    Selectors.h1                                                          -> StartPageMessages.heading,
    Selectors.tableHeaderCol(1)                                           -> StartPageMessages.WhatYouCanClaim.Table.column1Heading,
    Selectors.h2(Selectors.whatCanYouClaim, 1)                            -> StartPageMessages.WhatYouCanClaim.h2,
    Selectors.p(Selectors.whatCanYouClaim, 1)                             -> StartPageMessages.WhatYouCanClaim.p1,
    Selectors.p(Selectors.whatCanYouClaim, 2)                             -> StartPageMessages.WhatYouCanClaim.p2,
    Selectors.p(Selectors.whatCanYouClaim, 3)                             -> StartPageMessages.WhatYouCanClaim.p3,
    Selectors.p(Selectors.whatCanYouClaim, 4)                             -> StartPageMessages.WhatYouCanClaim.p4,
    Selectors.h2(Selectors.about, 1)                                      -> StartPageMessages.AboutCalculator.h2,
    Selectors.p(Selectors.about, 1)                                       -> StartPageMessages.AboutCalculator.p1,
    Selectors.p(Selectors.about, 2)                                       -> StartPageMessages.AboutCalculator.p2,
    Selectors.h3(s"${Selectors.about} ${Selectors.whoCanUse}", 1)         -> StartPageMessages.AboutCalculator.UseItFor.h3,
    Selectors.bullet(s"${Selectors.about} ${Selectors.whoCanUse}", 1)     -> StartPageMessages.AboutCalculator.UseItFor.bullet1,
    Selectors.bullet(s"${Selectors.about} ${Selectors.whoCanUse}", 2)     -> StartPageMessages.AboutCalculator.UseItFor.bullet2,
    Selectors.bullet(s"${Selectors.about} ${Selectors.whoCanUse}", 3)     -> StartPageMessages.AboutCalculator.UseItFor.bullet3,
    Selectors.bullet(s"${Selectors.about} ${Selectors.whoCanUse}", 4)     -> StartPageMessages.AboutCalculator.UseItFor.bullet4,
    Selectors.h3(s"${Selectors.about} ${Selectors.whoCannotUse}", 1)      -> StartPageMessages.AboutCalculator.CannotBeUsedFor.h3,
    Selectors.bullet(s"${Selectors.about} ${Selectors.whoCannotUse}", 1)  -> StartPageMessages.AboutCalculator.CannotBeUsedFor.bullet1,
    Selectors.bullet(s"${Selectors.about} ${Selectors.whoCannotUse}", 2)  -> StartPageMessages.AboutCalculator.CannotBeUsedFor.bullet2,
    Selectors.bullet(s"${Selectors.about} ${Selectors.whoCannotUse}", 3)  -> StartPageMessages.AboutCalculator.CannotBeUsedFor.bullet3,
    Selectors.bullet(s"${Selectors.about} ${Selectors.whoCannotUse}", 4)  -> StartPageMessages.AboutCalculator.CannotBeUsedFor.bullet4,
    Selectors.bullet(s"${Selectors.about} ${Selectors.whoCannotUse}", 5)  -> StartPageMessages.AboutCalculator.CannotBeUsedFor.bullet5,
    Selectors.bullet(s"${Selectors.about} ${Selectors.whoCannotUse}", 6)  -> StartPageMessages.AboutCalculator.CannotBeUsedFor.bullet6,
    Selectors.bullet(s"${Selectors.about} ${Selectors.whoCannotUse}", 7)  -> StartPageMessages.AboutCalculator.CannotBeUsedFor.bullet7,
    Selectors.bullet(s"${Selectors.about} ${Selectors.whoCannotUse}", 8)  -> StartPageMessages.AboutCalculator.CannotBeUsedFor.bullet8,
    Selectors.bullet(s"${Selectors.about} ${Selectors.whoCannotUse}", 9)  -> StartPageMessages.AboutCalculator.CannotBeUsedFor.bullet9,
    Selectors.bullet(s"${Selectors.about} ${Selectors.whoCannotUse}", 10) -> StartPageMessages.AboutCalculator.CannotBeUsedFor.bullet10,
    Selectors.bullet(s"${Selectors.about} ${Selectors.whoCannotUse}", 11) -> StartPageMessages.AboutCalculator.CannotBeUsedFor.bullet11,
    Selectors.p(s"${Selectors.about} ${Selectors.whoCannotUse}", 1)       -> StartPageMessages.AboutCalculator.CannotBeUsedFor.p1,
    Selectors.p(s"${Selectors.about} ${Selectors.whoCannotUse}", 2)       -> StartPageMessages.AboutCalculator.CannotBeUsedFor.p2,
    Selectors.h2(Selectors.beforeYouStart, 1)                             -> StartPageMessages.BeforeYouStart.h2,
    Selectors.p(Selectors.beforeYouStart, 1)                              -> StartPageMessages.BeforeYouStart.p1,
    Selectors.bullet(Selectors.beforeYouStart, 1)                         -> StartPageMessages.BeforeYouStart.bullet1,
    Selectors.bullet(Selectors.beforeYouStart, 2)                         -> StartPageMessages.BeforeYouStart.bullet2,
    Selectors.bullet(Selectors.beforeYouStart, 3)                         -> StartPageMessages.BeforeYouStart.bullet3,
    Selectors.bullet(Selectors.beforeYouStart, 4)                         -> StartPageMessages.BeforeYouStart.bullet4,
    Selectors.bullet(Selectors.beforeYouStart, 5)                         -> StartPageMessages.BeforeYouStart.bullet5,
    Selectors.bullet(Selectors.beforeYouStart, 6)                         -> StartPageMessages.BeforeYouStart.bullet6,
    Selectors.bullet(Selectors.beforeYouStart, 7)                         -> StartPageMessages.BeforeYouStart.bullet7,
    Selectors.p(Selectors.beforeYouStart, 2)                              -> StartPageMessages.BeforeYouStart.p2,
    Selectors
      .bullet(s"${Selectors.beforeYouStart} ${Selectors.julyOnwards}", 1) -> StartPageMessages.BeforeYouStart.JulyOnwardBullets.bullet1,
    Selectors
      .bullet(s"${Selectors.beforeYouStart} ${Selectors.julyOnwards}", 2) -> StartPageMessages.BeforeYouStart.JulyOnwardBullets.bullet2,
    Selectors
      .bullet(s"${Selectors.beforeYouStart} ${Selectors.julyOnwards}", 3) -> StartPageMessages.BeforeYouStart.JulyOnwardBullets.bullet3
  )

  "PaymentFrequencyViewSpec" when {

    implicit val request: DataRequest[_] = fakeDataRequest()

    def applyView(): HtmlFormat.Appendable = view(controllers.routes.ClaimPeriodStartController.onPageLoad())

    implicit val doc: Document = asDocument(applyView())

    behave like normalPage(messageKeyPrefix)
    behave like pageWithHeading(heading = StartPageMessages.heading)
    behave like pageWithExpectedMessages(expectedContent)
  }
}
