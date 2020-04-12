/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import java.time.LocalDate

import play.api.libs.json.{Format, Json}

sealed trait TaxYear
case object TaxYearEnding2020 extends TaxYear
case object TaxYearEnding2021 extends TaxYear

case class PayPeriod(start: LocalDate, end: LocalDate)

object PayPeriod {
  implicit val defaultFormat: Format[PayPeriod] = Json.format
}

case class FurloughPayment(amount: Double, payPeriod: PayPeriod)

object FurloughPayment {
  implicit val defaultFormat: Format[FurloughPayment] = Json.format
}

case class PayPeriodBreakdown(amount: Double, payPeriod: PayPeriod)

object PayPeriodBreakdown {
  implicit val defaultFormat: Format[PayPeriodBreakdown] = Json.format
}

case class NicCalculationResult(total: Double, payPeriodBreakdowns: Seq[PayPeriodBreakdown])

object NicCalculationResult {
  implicit val defaultFormat: Format[NicCalculationResult] = Json.format[NicCalculationResult]
}
