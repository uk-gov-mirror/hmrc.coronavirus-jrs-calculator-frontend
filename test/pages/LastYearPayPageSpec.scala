/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.CylbPaymentWith2020Periods
import pages.behaviours.PageBehaviours

class LastYearPayPageSpec extends PageBehaviours {

  "LastYearPayPage" must {

    beRetrievable[CylbPaymentWith2020Periods](LastYearPayPage)

    beSettable[CylbPaymentWith2020Periods](LastYearPayPage)

    beRemovable[CylbPaymentWith2020Periods](LastYearPayPage)
  }
}
