/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json.Json

case class VariableGrossPay(amount: BigDecimal)

object VariableGrossPay {
  implicit val format = Json.format[VariableGrossPay]
}
