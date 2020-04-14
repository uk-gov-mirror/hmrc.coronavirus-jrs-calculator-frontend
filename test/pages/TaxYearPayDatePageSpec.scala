/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import java.time.LocalDate

import org.scalacheck.Arbitrary
import pages.behaviours.PageBehaviours

class TaxYearPayDatePageSpec extends PageBehaviours {

  "TaxYearPayDatePage" must {

    beRetrievable[LocalDate](TaxYearPayDatePage)

    beSettable[LocalDate](TaxYearPayDatePage)

    beRemovable[LocalDate](TaxYearPayDatePage)
  }
}
