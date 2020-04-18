/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json.{Format, Json}

sealed trait Calculation
case object Calculation extends Enumerable.Implicits {
  case object FurloughCalculationResult extends WithName("furlough") with Calculation
  case object NicCalculationResult extends WithName("nic") with Calculation
  case object PensionCalculationResult extends WithName("pension") with Calculation

  val values: Seq[Calculation] = Seq(FurloughCalculationResult, NicCalculationResult, PensionCalculationResult)

  implicit val enumerable: Enumerable[Calculation] = Enumerable(values.map(v => v.toString -> v): _*)
}

case class CalculationResult(calculation: Calculation, total: BigDecimal, payPeriodBreakdowns: Seq[PeriodBreakdown])

object CalculationResult {
  implicit val defaultFormat: Format[CalculationResult] = Json.format[CalculationResult]
}
