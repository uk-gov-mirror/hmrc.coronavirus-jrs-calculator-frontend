/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.VariableLengthEmployed
import pages.behaviours.PageBehaviours

class VariableLengthEmployedSpec extends PageBehaviours {

  "VariableLengthEmployedPage" must {

    beRetrievable[VariableLengthEmployed](VariableLengthEmployedPage)

    beSettable[VariableLengthEmployed](VariableLengthEmployedPage)

    beRemovable[VariableLengthEmployed](VariableLengthEmployedPage)
  }
}
