/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json.Json

case class FurloughPartialPay(value: BigDecimal)

object FurloughPartialPay {
  implicit val format = Json.format[FurloughPartialPay]
}
