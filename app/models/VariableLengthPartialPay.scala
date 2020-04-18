/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json.Json

case class VariableLengthPartialPay(value: BigDecimal)

object VariableLengthPartialPay {
  implicit val format = Json.format[VariableLengthPartialPay]
}
