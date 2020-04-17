/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json._

case class Amount(value: BigDecimal)

object Amount {
  implicit val format: Format[Amount] = Json.format[Amount]
}

//TODO use Amount Vs BigDecimal
case class Salary(amount: BigDecimal)

object Salary {
  implicit val format = Json.format[Salary]
}
