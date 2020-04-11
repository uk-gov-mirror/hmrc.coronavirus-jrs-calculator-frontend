/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import java.time.LocalDate

sealed trait TaxYear
case object TaxYearEnding2020 extends TaxYear
case object TaxYearEnding2021 extends TaxYear

case class PayPeriod(start: LocalDate, end: LocalDate)

case class FurloughPayment(amount: Double, payPeriod: PayPeriod)
