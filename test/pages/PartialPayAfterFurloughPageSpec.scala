/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.FurloughPartialPay
import pages.behaviours.PageBehaviours

class PartialPayAfterFurloughPageSpec extends PageBehaviours {

  "PartialPayAfterFurloughPage" must {

    beRetrievable[FurloughPartialPay](PartialPayAfterFurloughPage)

    beSettable[FurloughPartialPay](PartialPayAfterFurloughPage)

    beRemovable[FurloughPartialPay](PartialPayAfterFurloughPage)
  }
}
