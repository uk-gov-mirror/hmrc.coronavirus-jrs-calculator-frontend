/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.VariableGrossPay
import pages.behaviours.PageBehaviours

class VariableGrossPayPageSpec extends PageBehaviours {

  "VariableGrossPayPage" must {

    beRetrievable[VariableGrossPay](VariableGrossPayPage)

    beSettable[VariableGrossPay](VariableGrossPayPage)

    beRemovable[VariableGrossPay](VariableGrossPayPage)
  }
}
