/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.PaymentFrequency
import pages.behaviours.PageBehaviours

class PaymentFrequencySpec extends PageBehaviours {

  "PaymentFrequencyPage" must {

    beRetrievable[PaymentFrequency](PaymentFrequencyPage)

    beSettable[PaymentFrequency](PaymentFrequencyPage)

    beRemovable[PaymentFrequency](PaymentFrequencyPage)
  }
}
