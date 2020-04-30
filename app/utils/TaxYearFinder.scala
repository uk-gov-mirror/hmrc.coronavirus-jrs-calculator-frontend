/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package utils

import java.time.LocalDate

import models.{PaymentDate, TaxYear, TaxYearEnding2020, TaxYearEnding2021}

trait TaxYearFinder {

  def taxYearAt(paymentDate: PaymentDate): TaxYear =
    if (paymentDate.value.isBefore(LocalDate.of(2020, 4, 6))) TaxYearEnding2020 else TaxYearEnding2021

}
