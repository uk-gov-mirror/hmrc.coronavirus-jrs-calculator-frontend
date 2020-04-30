/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import base.SpecBase
import models.Amount
import Calculators._

class CalculatorsSpec extends SpecBase {

  "Calculates 80% of a given amount" in new Calculators {
    eightyPercent(Amount(1000.0)) mustBe Amount(800.0)
  }

  "Daily calculation rounded half up" in new Calculators {
    dailyCalculation(Amount(3000.155), 30, 10) mustBe Amount(1000.05)
  }

  "return 80% of an amount if lesser than cap allowance unrounded or cap otherwise" in new Calculators {
    claimableAmount(Amount(100.15), 99) mustBe Amount(80.120)
    claimableAmount(Amount(100.00), 79) mustBe Amount(79)
  }

  "Round an amount HALF_UP" in new Calculators {
    Amount(1000.5111).halfUp mustBe Amount(1000.51)
    Amount(1000.4999).halfUp mustBe Amount(1000.50)
  }
}
