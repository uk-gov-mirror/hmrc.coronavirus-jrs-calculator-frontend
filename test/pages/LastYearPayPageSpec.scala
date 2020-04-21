/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.Salary
import pages.behaviours.PageBehaviours

class LastYearPayPageSpec extends PageBehaviours {

  "LastYearPayPage" must {

    beRetrievable[Salary](LastYearPayPage)

    beSettable[Salary](LastYearPayPage)

    beRemovable[Salary](LastYearPayPage)
  }
}
