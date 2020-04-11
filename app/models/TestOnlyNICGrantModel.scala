/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import java.time.LocalDate

import play.api.libs.json.Json

case class TestOnlyNICGrantModel(
  startDate: LocalDate,
  endDate: LocalDate,
  furloughedAmount: Double,
  frequency: PaymentFrequency)

object TestOnlyNICGrantModel {
  implicit val format = Json.format[TestOnlyNICGrantModel]
}
