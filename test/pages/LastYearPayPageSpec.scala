/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.CylbPayment
import pages.behaviours.PageBehaviours

class LastYearPayPageSpec extends PageBehaviours {

  "LastYearPayPage" must {

    beRetrievable[CylbPayment](LastYearPayPage)

    beSettable[CylbPayment](LastYearPayPage)

    beRemovable[CylbPayment](LastYearPayPage)
  }
}
