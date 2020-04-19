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

class PartialPayBeforeFurloughPageSpec extends PageBehaviours {

  "PartialPayBeforeFurloughPage" must {

<<<<<<< HEAD
    beRetrievable[FurloughPartialPay](PartialPayBeforeFurloughPage)

    beSettable[FurloughPartialPay](PartialPayBeforeFurloughPage)

    beRemovable[FurloughPartialPay](PartialPayBeforeFurloughPage)
=======
    beRetrievable[VariableLengthPartialPay](PartialPayBeforeFurloughPage)

    beSettable[VariableLengthPartialPay](PartialPayBeforeFurloughPage)

    beRemovable[VariableLengthPartialPay](PartialPayBeforeFurloughPage)
>>>>>>> 99695f13f65c4f3be36cb188c073ce349bf0618b
  }
}
