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

package viewmodels

import assets.messages.{BaseMessages, StartPageMessages}
import views.BaseSelectors
import views.behaviours.ViewBehaviours

class StartPageCalcTableSpec extends ViewBehaviours {

  lazy val calcTable = app.injector.instanceOf[StartPageCalcTable]
  implicit lazy val document = asDocument(calcTable.renderTable)

  object Selectors extends BaseSelectors

  "StartPageCalcTable" must {

    behave like pageWithExpectedMessages(
      Seq(
        Selectors.tableHeaderCol(1)   -> StartPageMessages.WhatYouCanClaim.Table.column1Heading,
        Selectors.tableHeaderCol(2)   -> StartPageMessages.WhatYouCanClaim.Table.column2Heading,
        Selectors.tableHeaderCol(3)   -> StartPageMessages.WhatYouCanClaim.Table.column3Heading,
        Selectors.tableRowXcolY(1, 1) -> BaseMessages.Month.may,
        Selectors.tableRowXcolY(1, 2) -> StartPageMessages.WhatYouCanClaim.Table.mayJunGovt,
        Selectors.tableRowXcolY(1, 3) -> StartPageMessages.WhatYouCanClaim.Table.naughtPercent,
        Selectors.tableRowXcolY(2, 1) -> BaseMessages.Month.june,
        Selectors.tableRowXcolY(2, 2) -> StartPageMessages.WhatYouCanClaim.Table.mayJunGovt,
        Selectors.tableRowXcolY(2, 3) -> StartPageMessages.WhatYouCanClaim.Table.naughtPercent,
        Selectors.tableRowXcolY(3, 1) -> BaseMessages.Month.july,
        Selectors.tableRowXcolY(3, 2) -> StartPageMessages.WhatYouCanClaim.Table.julGovt,
        Selectors.tableRowXcolY(3, 3) -> StartPageMessages.WhatYouCanClaim.Table.julyEmp,
        Selectors.tableRowXcolY(4, 1) -> BaseMessages.Month.august,
        Selectors.tableRowXcolY(4, 2) -> StartPageMessages.WhatYouCanClaim.Table.augSepGovt,
        Selectors.tableRowXcolY(4, 3) -> StartPageMessages.WhatYouCanClaim.Table.augSepEmp,
        Selectors.tableRowXcolY(5, 1) -> BaseMessages.Month.september,
        Selectors.tableRowXcolY(5, 2) -> StartPageMessages.WhatYouCanClaim.Table.augSepGovt,
        Selectors.tableRowXcolY(5, 3) -> StartPageMessages.WhatYouCanClaim.Table.augSepEmp
      ))
  }
}
