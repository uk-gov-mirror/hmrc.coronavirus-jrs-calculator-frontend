/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import java.time.LocalDate

import play.api.libs.json.Json

case class ClaimPeriodModel(startDate: LocalDate, endDate: LocalDate)

object ClaimPeriodModel {
  implicit val format = Json.format[ClaimPeriodModel]
}
