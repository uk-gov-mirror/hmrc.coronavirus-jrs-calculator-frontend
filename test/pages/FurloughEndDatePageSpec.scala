/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import java.time.LocalDate

import org.scalacheck.Arbitrary
import pages.behaviours.PageBehaviours

class FurloughEndDatePageSpec extends PageBehaviours {

  "FurloughEndDatePage" must {

    implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
      datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
    }

    beRetrievable[LocalDate](FurloughEndDatePage)(arbitraryLocalDate, implicitly)

    beSettable[LocalDate](FurloughEndDatePage)(arbitraryLocalDate, implicitly)

    beRemovable[LocalDate](FurloughEndDatePage)(arbitraryLocalDate, implicitly)
  }
}
