/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package utils

import java.time.LocalDate

import models.{PayPeriod, PaymentDate, TaxYear, TaxYearEnding2020, TaxYearEnding2021}

trait TaxYearFinder {

  def taxYearAt(paymentDate: PaymentDate): TaxYear =
    if (paymentDate.value.isBefore(LocalDate.of(2020, 4, 5))) TaxYearEnding2020 else TaxYearEnding2021

  def containsNewTaxYear(payPeriod: PayPeriod): Boolean = {
    val newTaxYearDate = LocalDate.of(payPeriod.start.getYear, 4, 6)

    newTaxYearDate.isAfter(payPeriod.start) && newTaxYearDate.isBefore(payPeriod.end)
  }

}
