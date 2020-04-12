/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package utils

import java.time.LocalDate

import base.SpecBase
import models.{PaymentDate, TaxYearEnding2020, TaxYearEnding2021}

class TaxYearFinderSpec extends SpecBase {

  "Returns TaxYear for a given PayPeriod" in new TaxYearFinder {
    taxYearAt(PaymentDate(LocalDate.of(2020, 3, 31))) mustBe TaxYearEnding2020
    taxYearAt(PaymentDate(LocalDate.of(2020, 4, 30))) mustBe TaxYearEnding2021
  }
}
