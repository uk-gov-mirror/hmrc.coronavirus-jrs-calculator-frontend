/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import java.time.LocalDate

import generators.ModelGenerators
import models.TestOnlyNICGrantModel
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class TestOnlyNICGrantCalculatorPageSpec extends PageBehaviours with ModelGenerators {

  "TestOnlyNICGrantCalculatorPage" must {

    beRetrievable[TestOnlyNICGrantModel](TestOnlyNICGrantCalculatorPage)

    beSettable[TestOnlyNICGrantModel](TestOnlyNICGrantCalculatorPage)

    beRemovable[TestOnlyNICGrantModel](TestOnlyNICGrantCalculatorPage)
  }
}
