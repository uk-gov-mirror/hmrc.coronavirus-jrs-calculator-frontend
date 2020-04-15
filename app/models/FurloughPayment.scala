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

case class PayPeriodBreakdown(amount: Double, payPeriodWithPayDay: PayPeriodWithPayDay)

object PayPeriodBreakdown {
  implicit val defaultFormat: Format[PayPeriodBreakdown] = Json.format
}

sealed trait Calculation
case object Calculation extends Enumerable.Implicits {
  case object FurloughCalculationResult extends WithName("furlough") with Calculation
  case object NicCalculationResult extends WithName("nic") with Calculation
  case object PensionCalculationResult extends WithName("pension") with Calculation

  val values: Seq[Calculation] = Seq(FurloughCalculationResult, NicCalculationResult, PensionCalculationResult)

  implicit val enumerable: Enumerable[Calculation] = Enumerable(values.map(v => v.toString -> v): _*)
}

case class CalculationResult(calculation: Calculation, total: Double, payPeriodBreakdowns: Seq[PayPeriodBreakdown])

object CalculationResult {
  implicit val defaultFormat: Format[CalculationResult] = Json.format[CalculationResult]
}
