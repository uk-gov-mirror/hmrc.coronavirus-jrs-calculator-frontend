/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.FurloughCalculations
import pages.behaviours.PageBehaviours

class FurloughCalculationsSpec extends PageBehaviours {

  "FurloughCalculationsPage" must {

    beRetrievable[FurloughCalculations](FurloughCalculationsPage)

    beSettable[FurloughCalculations](FurloughCalculationsPage)

    beRemovable[FurloughCalculations](FurloughCalculationsPage)
  }
}
