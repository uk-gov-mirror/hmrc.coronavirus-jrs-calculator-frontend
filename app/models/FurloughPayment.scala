/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import java.time.LocalDate

import play.api.libs.json.{Format, Json}
import utils.ValueClassFormat

sealed trait TaxYear
case object TaxYearEnding2020 extends TaxYear
case object TaxYearEnding2021 extends TaxYear

case class PayPeriod(start: LocalDate, end: LocalDate)

object PayPeriod {
  implicit val defaultFormat: Format[PayPeriod] = Json.format
}

case class PaymentDate(value: LocalDate)

object PaymentDate {
  implicit val defaultFormat: Format[PaymentDate] =
    ValueClassFormat.format(dateString => PaymentDate.apply(LocalDate.parse(dateString)))(_.value)
}

case class FurloughPayment(amount: Double, paymentDate: PaymentDate)

object FurloughPayment {
  implicit val defaultFormat: Format[FurloughPayment] = Json.format
}

case class PaymentDateBreakdown(amount: Double, paymentDate: PaymentDate)

object PaymentDateBreakdown {
  implicit val defaultFormat: Format[PaymentDateBreakdown] = Json.format
}

case class NicCalculationResult(total: Double, paymentDateBreakdowns: Seq[PaymentDateBreakdown])

object NicCalculationResult {
  implicit val defaultFormat: Format[NicCalculationResult] = Json.format[NicCalculationResult]
}
