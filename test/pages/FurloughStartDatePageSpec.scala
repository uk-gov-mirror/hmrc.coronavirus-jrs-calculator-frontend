/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import java.time.LocalDate

import org.scalacheck.Arbitrary
import pages.behaviours.PageBehaviours

class FurloughStartDatePageSpec extends PageBehaviours {

  "FurloughStartDatePage" must {

    implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
      datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
    }

    beRetrievable[LocalDate](FurloughStartDatePage)(arbitraryLocalDate, implicitly)

    beSettable[LocalDate](FurloughStartDatePage)(arbitraryLocalDate, implicitly)

    beRemovable[LocalDate](FurloughStartDatePage)(arbitraryLocalDate, implicitly)
  }
}
