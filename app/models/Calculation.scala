/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
