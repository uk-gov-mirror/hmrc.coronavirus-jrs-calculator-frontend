/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.VariableLengthPartialPay
import pages.behaviours.PageBehaviours

class PartialPayAfterFurloughPageSpec extends PageBehaviours {

  "PartialPayAfterFurloughPage" must {

    beRetrievable[VariableLengthPartialPay](PartialPayAfterFurloughPage)

    beSettable[VariableLengthPartialPay](PartialPayAfterFurloughPage)

    beRemovable[VariableLengthPartialPay](PartialPayAfterFurloughPage)
  }
}
