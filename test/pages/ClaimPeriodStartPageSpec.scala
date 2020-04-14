/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import java.time.LocalDate

import pages.behaviours.PageBehaviours

class ClaimPeriodStartPageSpec extends PageBehaviours {

  "ClaimPeriodStartPage" must {

    beRetrievable[LocalDate](ClaimPeriodStartPage)

    beSettable[LocalDate](ClaimPeriodStartPage)

    beRemovable[LocalDate](ClaimPeriodStartPage)
  }
}
