/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import java.time.LocalDate

import pages.behaviours.PageBehaviours

class EmployeeStartDatePageSpec extends PageBehaviours {

  "EmployeeStartDatePage" must {

    beRetrievable[LocalDate](EmployeeStartDatePage)

    beSettable[LocalDate](EmployeeStartDatePage)

    beRemovable[LocalDate](EmployeeStartDatePage)
  }
}
