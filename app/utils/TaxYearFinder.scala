/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package utils

import java.time.LocalDate

import models.{PayPeriod, TaxYear, TaxYearEnding2020, TaxYearEnding2021}

trait TaxYearFinder {

  def taxYearAt(payPeriod: PayPeriod): TaxYear =
    if (payPeriod.end.isBefore(LocalDate.of(2020, 4, 5))) TaxYearEnding2020 else TaxYearEnding2021

}
