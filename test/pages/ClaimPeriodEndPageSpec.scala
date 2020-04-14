/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import java.time.LocalDate

import pages.behaviours.PageBehaviours

class ClaimPeriodEndPageSpec extends PageBehaviours {

  "ClaimPeriodEndPage" must {

    beRetrievable[LocalDate](ClaimPeriodEndPage)

    beSettable[LocalDate](ClaimPeriodEndPage)

    beRemovable[LocalDate](ClaimPeriodEndPage)
  }
}
