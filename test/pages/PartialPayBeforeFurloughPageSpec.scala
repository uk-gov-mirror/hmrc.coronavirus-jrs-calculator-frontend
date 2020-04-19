/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.FurloughPartialPay
import pages.behaviours.PageBehaviours

class PartialPayBeforeFurloughPageSpec extends PageBehaviours {

  "PartialPayBeforeFurloughPage" must {

    beRetrievable[FurloughPartialPay](PartialPayBeforeFurloughPage)

    beSettable[FurloughPartialPay](PartialPayBeforeFurloughPage)

    beRemovable[FurloughPartialPay](PartialPayBeforeFurloughPage)

  }
}
