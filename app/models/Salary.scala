/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json._

case class Salary(amount: Double)

object Salary {
  implicit val format = Json.format[Salary]
}
