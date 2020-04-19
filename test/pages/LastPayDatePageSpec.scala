/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import java.time.LocalDate

import org.scalacheck.Arbitrary
import pages.behaviours.PageBehaviours

class LastPayDatePageSpec extends PageBehaviours {

  "LastPayDatePage" must {

    implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
      datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
    }

    beRetrievable[LocalDate](LastPayDatePage)(arbitraryLocalDate, implicitly)

    beSettable[LocalDate](LastPayDatePage)(arbitraryLocalDate, implicitly)

    beRemovable[LocalDate](LastPayDatePage)(arbitraryLocalDate, implicitly)
  }
}
