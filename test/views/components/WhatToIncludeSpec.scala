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

package views.components

import assets.messages.WhatToIncludeMessages
import views.BaseSelectors
import views.behaviours.ViewBehaviours
import views.html.helper.whatToInclude

class WhatToIncludeSpec extends ViewBehaviours {

  "WhatToInclude" must {
    val view         = injector.instanceOf[whatToInclude]
    val cylb         = false
    implicit val doc = asDocument(view.apply(cylb))

    object Selectors extends BaseSelectors {
      override val p: Int => String      = i => s"p:nth-of-type($i)"
      override val bullet: Int => String = i => s"ul:nth-child(2) > li:nth-child($i)"
      val bullet2: Int => String         = i => s"ul:nth-child(4) > li:nth-child($i)"
      "#main-content > div > div > div.govuk-\\!-margin-bottom-6 > form > div.form-group > details > div > ul:nth-child(4) > li:nth-child(1)"
      val subBullets: Int => String = i => s"ul:nth-child(4) > li:nth-child(1) > ul > li:nth-child($i)"
      val summary                   = "summary > span"
    }

    def expectedWhatToIncludeContent(cylb: Boolean = false) = Seq(
      Selectors.summary   -> WhatToIncludeMessages.summary,
      Selectors.p(1)      -> WhatToIncludeMessages.include,
      Selectors.bullet(1) -> WhatToIncludeMessages.inclL1(cylb),
      Selectors.bullet(2) -> WhatToIncludeMessages.includeL2,
      Selectors.bullet(3) -> WhatToIncludeMessages.includeL3,
      Selectors.bullet(4) -> WhatToIncludeMessages.includeL4,
      Selectors.bullet(5) -> WhatToIncludeMessages.includeL5,
      Selectors.p(2)      -> WhatToIncludeMessages.doNotInclude,
      Selectors
        .bullet2(1)           -> s"${WhatToIncludeMessages.dontInclL1(cylb)} ${WhatToIncludeMessages.doNotIncludeL1L1} ${WhatToIncludeMessages.doNotIncludeL1L2} ${WhatToIncludeMessages.doNotIncludeL1L3}",
      Selectors.subBullets(1) -> WhatToIncludeMessages.doNotIncludeL1L1,
      Selectors.subBullets(2) -> WhatToIncludeMessages.doNotIncludeL1L2,
      Selectors.subBullets(3) -> WhatToIncludeMessages.doNotIncludeL1L3,
      Selectors.bullet2(2)    -> WhatToIncludeMessages.doNotIncludeL2,
      Selectors.bullet2(3)    -> WhatToIncludeMessages.doNotIncludeL3
    )

    behave like pageWithExpectedMessages(expectedWhatToIncludeContent(cylb))
  }
}
