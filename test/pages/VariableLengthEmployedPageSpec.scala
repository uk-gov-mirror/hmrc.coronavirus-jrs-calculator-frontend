/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.EmployeeStarted
import pages.behaviours.PageBehaviours

class EmployeeStartedSpec extends PageBehaviours {

  "EmployedStartedPage" must {

    beRetrievable[EmployeeStarted](EmployedStartedPage)

    beSettable[EmployeeStarted](EmployedStartedPage)

    beRemovable[EmployeeStarted](EmployedStartedPage)
  }
}
