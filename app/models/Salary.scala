/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json._
import utils.ValueClassFormat

case class Amount(value: BigDecimal)

object Amount {
  implicit val format: Format[Amount] = ValueClassFormat.format(value => Amount.apply(BigDecimal(value)))(_.value)

  implicit class Defaulted(maybeAmount: Option[Amount]) {
    def defaulted: Amount = maybeAmount.fold(Amount(0.0))(v => v)
  }

  implicit class FromDouble(value: Double) {
    def toAmount: Amount = Amount(value)
  }
}

//TODO use Amount Vs BigDecimal
case class Salary(amount: BigDecimal)

object Salary {
  implicit val format = Json.format[Salary]
}
