/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import java.time.LocalDate

import pages.behaviours.PageBehaviours

class PayDatePageSpec extends PageBehaviours {

  "PayDatePage" must {

    beRetrievable[LocalDate](PayDatePage)

    beSettable[LocalDate](PayDatePage)

    beRemovable[LocalDate](PayDatePage)
  }
}
