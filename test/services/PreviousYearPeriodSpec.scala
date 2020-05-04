/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import base.{CoreTestDataBuilder, SpecBase}
import models.{CylbDuration, CylbOperators}
import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}

class PreviousYearPeriodSpec extends SpecBase with CoreTestDataBuilder {

  "Return operators for cylb" in new PreviousYearPeriod {

    operators(Weekly, fullPeriod("2020,3,1", "2020,3,7")) mustBe CylbOperators(7, 2, 5)
    operators(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,3" -> "2020,3,7")) mustBe CylbOperators(7, 0, 5)
    operators(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,4" -> "2020,3,7")) mustBe CylbOperators(7, 0, 4)
    operators(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,2" -> "2020,3,7")) mustBe CylbOperators(7, 1, 5)

    //Furlough ends
    operators(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,1" -> "2020,3,6")) mustBe CylbOperators(7, 2, 4)
    operators(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,1" -> "2020,3,2")) mustBe CylbOperators(7, 2, 0)

    operators(FortNightly, fullPeriod("2020,3,1", "2020,3,14")) mustBe CylbOperators(14, 2, 12)
    operators(FortNightly, partialPeriod("2020,3,1" -> "2020,3,14", "2020,3,3" -> "2020,3,14")) mustBe CylbOperators(14, 0, 12)
    operators(FortNightly, partialPeriod("2020,3,1" -> "2020,3,14", "2020,3,5" -> "2020,3,14")) mustBe CylbOperators(14, 0, 10)
    operators(FortNightly, partialPeriod("2020,3,1" -> "2020,3,14", "2020,3,2" -> "2020,3,14")) mustBe CylbOperators(14, 1, 12)

    operators(FourWeekly, fullPeriod("2020,3,1", "2020,3,28")) mustBe CylbOperators(28, 2, 26)
    operators(FourWeekly, partialPeriod("2020,3,1" -> "2020,3,28", "2020,3,3"  -> "2020,3,28")) mustBe CylbOperators(28, 0, 26)
    operators(FourWeekly, partialPeriod("2020,3,1" -> "2020,3,28", "2020,3,9"  -> "2020,3,28")) mustBe CylbOperators(28, 0, 20)
    operators(FourWeekly, partialPeriod("2020,3,1" -> "2020,3,28", "2020,3, 2" -> "2020,3,28")) mustBe CylbOperators(28, 1, 26)

    operators(Monthly, fullPeriod("2020,3,1", "2020,3,31")) mustBe CylbOperators(31, 0, 31)
    operators(Monthly, fullPeriod("2020,4,1", "2020,4,30")) mustBe CylbOperators(30, 0, 30)
    operators(Monthly, partialPeriod("2020,3,1" -> "2020,3,31", "2020,3,3" -> "2020,3,31")) mustBe CylbOperators(31, 0, 29)
  }

  def extract(duration: CylbDuration): (Int, Int, Int) =
    (duration.fullPeriodLength, duration.equivalentPeriodDays, duration.previousPeriodDays)

  "Weekly tests" in {
    extract(CylbDuration(Weekly, fullPeriod("2020,3,1", "2020,3,7"))) mustBe Tuple3(7, 5, 2)

    extract(CylbDuration(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,3" -> "2020,3,7"))) mustBe
      Tuple3(7, 5, 0)

    extract(CylbDuration(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,4" -> "2020,3,7"))) mustBe
      Tuple3(7, 4, 0)

    extract(CylbDuration(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,2" -> "2020,3,7"))) mustBe
      Tuple3(7, 5, 1)

    extract(CylbDuration(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,1" -> "2020,3,6"))) mustBe
      Tuple3(7, 4, 2)

    extract(CylbDuration(Weekly, partialPeriod("2020,3,1" -> "2020,3,7", "2020,3,1" -> "2020,3,2"))) mustBe
      Tuple3(7, 0, 2)
  }

  "Fortnightly tests" in {
    extract(CylbDuration(FortNightly, fullPeriod("2020,3,1", "2020,3,14"))) mustBe
      Tuple3(14, 12, 2)

    extract(CylbDuration(FortNightly, partialPeriod("2020,3,1" -> "2020,3,14", "2020,3,3" -> "2020,3,14"))) mustBe
      Tuple3(14, 12, 0)

    extract(CylbDuration(FortNightly, partialPeriod("2020,3,1" -> "2020,3,14", "2020,3,5" -> "2020,3,14"))) mustBe
      Tuple3(14, 10, 0)

    extract(CylbDuration(FortNightly, partialPeriod("2020,3,1" -> "2020,3,14", "2020,3,2" -> "2020,3,14"))) mustBe
      Tuple3(14, 12, 1)
  }

  "Fourweekly tests" in {
    extract(CylbDuration(FourWeekly, fullPeriod("2020,3,1", "2020,3,28"))) mustBe
      Tuple3(28, 26, 2)

    extract(CylbDuration(FourWeekly, partialPeriod("2020,3,1" -> "2020,3,28", "2020,3,3" -> "2020,3,28"))) mustBe
      Tuple3(28, 26, 0)

    extract(CylbDuration(FourWeekly, partialPeriod("2020,3,1" -> "2020,3,28", "2020,3,9" -> "2020,3,28"))) mustBe
      Tuple3(28, 20, 0)

    extract(CylbDuration(FourWeekly, partialPeriod("2020,3,1" -> "2020,3,28", "2020,3, 2" -> "2020,3,28"))) mustBe
      Tuple3(28, 26, 1)
  }

  "Monthly tests" in {
    extract(CylbDuration(Monthly, fullPeriod("2020,3,1", "2020,3,31"))) mustBe
      Tuple3(31, 31, 0)
    extract(CylbDuration(Monthly, fullPeriod("2020,4,1", "2020,4,30"))) mustBe
      Tuple3(30, 30, 0)
    extract(CylbDuration(Monthly, partialPeriod("2020,3,1" -> "2020,3,31", "2020,3,3" -> "2020,3,31"))) mustBe
      Tuple3(31, 29, 0)
  }

}
