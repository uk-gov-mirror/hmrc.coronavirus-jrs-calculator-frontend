/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import base.{CoreDataBuilder, SpecBase}
import models.CylbOperators
import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}

class PreviousYearPeriodSpec extends SpecBase with CoreDataBuilder {

  "Return operators for cylb" in new PreviousYearPeriod {

    operatorsEnhanced(Weekly, fullPeriod("2020,3,1", "2020,3,7")) mustBe CylbOperators(7, 2, 5)
    operatorsEnhanced(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,3" -> "2020,3,7")) mustBe CylbOperators(7, 0, 5)
    operatorsEnhanced(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,4" -> "2020,3,7")) mustBe CylbOperators(7, 0, 4)
    operatorsEnhanced(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,2" -> "2020,3,7")) mustBe CylbOperators(7, 1, 5)

    //Furlough ends
    operatorsEnhanced(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,1" -> "2020,3,6")) mustBe CylbOperators(7, 2, 4)
    operatorsEnhanced(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,1" -> "2020,3,2")) mustBe CylbOperators(7, 2, 0)

    operatorsEnhanced(FortNightly, fullPeriod("2020,3,1", "2020,3,14")) mustBe CylbOperators(14, 2, 12)
    operatorsEnhanced(FortNightly, partialPeriod("2020,3,1" -> "2020,3,14", "2020,3,3" -> "2020,3,14")) mustBe CylbOperators(14, 0, 12)
    operatorsEnhanced(FortNightly, partialPeriod("2020,3,1" -> "2020,3,14", "2020,3,5" -> "2020,3,14")) mustBe CylbOperators(14, 0, 10)
    operatorsEnhanced(FortNightly, partialPeriod("2020,3,1" -> "2020,3,14", "2020,3,2" -> "2020,3,14")) mustBe CylbOperators(14, 1, 12)

    operatorsEnhanced(FourWeekly, fullPeriod("2020,3,1", "2020,3,28")) mustBe CylbOperators(28, 2, 26)
    operatorsEnhanced(FourWeekly, partialPeriod("2020,3,1" -> "2020,3,28", "2020,3,3"  -> "2020,3,28")) mustBe CylbOperators(28, 0, 26)
    operatorsEnhanced(FourWeekly, partialPeriod("2020,3,1" -> "2020,3,28", "2020,3,9"  -> "2020,3,28")) mustBe CylbOperators(28, 0, 20)
    operatorsEnhanced(FourWeekly, partialPeriod("2020,3,1" -> "2020,3,28", "2020,3, 2" -> "2020,3,28")) mustBe CylbOperators(28, 1, 26)

    operatorsEnhanced(Monthly, fullPeriod("2020,3,1", "2020,3,31")) mustBe CylbOperators(31, 0, 31)
    operatorsEnhanced(Monthly, fullPeriod("2020,4,1", "2020,4,30")) mustBe CylbOperators(30, 0, 30)
    operatorsEnhanced(Monthly, partialPeriod("2020,3,1" -> "2020,3,31", "2020,3,3" -> "2020,3,31")) mustBe CylbOperators(31, 0, 29)
  }

}
