/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.FurloughDates
import pages.behaviours.PageBehaviours

class FurloughDatesSpec extends PageBehaviours {

  "FurloughDatesPage" must {

    beRetrievable[FurloughDates](FurloughDatesPage)

    beSettable[FurloughDates](FurloughDatesPage)

    beRemovable[FurloughDates](FurloughDatesPage)
  }
}
