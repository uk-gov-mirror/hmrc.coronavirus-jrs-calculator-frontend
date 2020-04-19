/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.VariableLengthPartialPay
import pages.behaviours.PageBehaviours

class PartialPayBeforeFurloughPageSpec extends PageBehaviours {

  "PartialPayBeforeFurloughPage" must {

    beRetrievable[VariableLengthPartialPay](PartialPayBeforeFurloughPage)

    beSettable[VariableLengthPartialPay](PartialPayBeforeFurloughPage)

    beRemovable[VariableLengthPartialPay](PartialPayBeforeFurloughPage)
  }
}
