/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

<<<<<<< HEAD
import models.FurloughPartialPay
=======
import models.VariableLengthPartialPay
>>>>>>> 99695f13f65c4f3be36cb188c073ce349bf0618b
import pages.behaviours.PageBehaviours

class PartialPayAfterFurloughPageSpec extends PageBehaviours {

  "PartialPayAfterFurloughPage" must {

<<<<<<< HEAD
    beRetrievable[FurloughPartialPay](PartialPayAfterFurloughPage)

    beSettable[FurloughPartialPay](PartialPayAfterFurloughPage)

    beRemovable[FurloughPartialPay](PartialPayAfterFurloughPage)
=======
    beRetrievable[VariableLengthPartialPay](PartialPayAfterFurloughPage)

    beSettable[VariableLengthPartialPay](PartialPayAfterFurloughPage)

    beRemovable[VariableLengthPartialPay](PartialPayAfterFurloughPage)
>>>>>>> 99695f13f65c4f3be36cb188c073ce349bf0618b
  }
}
