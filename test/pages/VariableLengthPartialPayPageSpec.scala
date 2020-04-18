/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.VariableLengthPartialPay
import pages.behaviours.PageBehaviours

class VariableLengthPartialPayPageSpec extends PageBehaviours {

  "VariableLengthPartialPayPage" must {

    beRetrievable[VariableLengthPartialPay](VariableLengthPartialPayPage)

    beSettable[VariableLengthPartialPay](VariableLengthPartialPayPage)

    beRemovable[VariableLengthPartialPay](VariableLengthPartialPayPage)
  }
}
