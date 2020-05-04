/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.FurloughTopUpStatus
import pages.behaviours.PageBehaviours

class FurloughTopUpStatusPageSpec extends PageBehaviours {

  "FurloughTopUpStatusPage" must {

    beRetrievable[FurloughTopUpStatus](FurloughTopUpStatusPage)

    beSettable[FurloughTopUpStatus](FurloughTopUpStatusPage)

    beRemovable[FurloughTopUpStatus](FurloughTopUpStatusPage)
  }
}
