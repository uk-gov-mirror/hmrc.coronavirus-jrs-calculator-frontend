/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import java.time.LocalDate

import play.api.libs.json.{Format, Json}

case class Period(start: LocalDate, end: LocalDate)

object Period {
  implicit val defaultFormat: Format[Period] = Json.format
}

case class PartialPeriod(original: Period, partial: Period)

object PartialPeriod {
  implicit val defaultFormat: Format[PartialPeriod] = Json.format
}

case class PeriodWithPaymentDate(period: Period, paymentDate: PaymentDate)

object PeriodWithPaymentDate {
  implicit val defaultFormat: Format[PeriodWithPaymentDate] = Json.format
}

case class PartialPeriodWithPaymentDate(period: PartialPeriod, paymentDate: PaymentDate)

object PartialPeriodWithPaymentDate {
  implicit val defaultFormat: Format[PartialPeriodWithPaymentDate] = Json.format
}
