/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.ClaimPeriodModel
import org.scalacheck.Arbitrary
import pages.behaviours.PageBehaviours

class ClaimPeriodPageSpec extends PageBehaviours {

  "ClaimPeriodPage" must {

    beRetrievable[ClaimPeriodModel](ClaimPeriodPage)

    beSettable[ClaimPeriodModel](ClaimPeriodPage)

    beRemovable[ClaimPeriodModel](ClaimPeriodPage)
  }
}
