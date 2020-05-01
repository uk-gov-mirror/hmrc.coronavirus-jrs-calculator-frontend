/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.PayMethod
import pages.behaviours.PageBehaviours

class PayMethodSpec extends PageBehaviours {

  "PayMethodPage" must {

    beRetrievable[PayMethod](PayMethodPage)

    beSettable[PayMethod](PayMethodPage)

    beRemovable[PayMethod](PayMethodPage)
  }
}
