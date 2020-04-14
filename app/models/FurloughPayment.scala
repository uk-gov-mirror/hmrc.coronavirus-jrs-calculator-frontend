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

case class PaymentDate(value: LocalDate)

object PaymentDate {
  implicit val defaultFormat: Format[PaymentDate] =
    ValueClassFormat.format(dateString => PaymentDate.apply(LocalDate.parse(dateString)))(_.value)
}

case class PayPeriod(start: LocalDate, end: LocalDate)

object PayPeriod {
  implicit val defaultFormat: Format[PayPeriod] = Json.format
}

case class PayPeriodWithPayDay(payPeriod: PayPeriod, paymentDate: PaymentDate)

object PayPeriodWithPayDay {
  implicit val defaultFormat: Format[PayPeriodWithPayDay] = Json.format
}

case class RegularPayment(salary: Salary, payPeriod: PayPeriod)

case class FurloughPayment(amount: Double, payPeriod: PayPeriodWithPayDay)

object FurloughPayment {
  implicit val defaultFormat: Format[FurloughPayment] = Json.format
}

case class PayPeriodBreakdown(amount: Double, payPeriodWithPayDay: PayPeriodWithPayDay)

object PayPeriodBreakdown {
  implicit val defaultFormat: Format[PayPeriodBreakdown] = Json.format
}

case class CalculationResult(total: Double, payPeriodBreakdowns: Seq[PayPeriodBreakdown])

object CalculationResult {
  implicit val defaultFormat: Format[CalculationResult] = Json.format[CalculationResult]
}
