/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import java.time.LocalDate

import play.api.libs.json.{Format, Json}

final case class Period(start: LocalDate, end: LocalDate)

object Period {
  implicit val defaultFormat: Format[Period] = Json.format
}

sealed trait Periods {
  val period: Period
}
final case class FullPeriod(period: Period) extends Periods
final case class PartialPeriod(original: Period, partial: Period) extends Periods {
  override val period = original
}

object Periods {
  implicit val defaultFormat: Format[Periods] = Json.format
}

object FullPeriod {
  implicit val defaultFormat: Format[FullPeriod] = Json.format
}

object PartialPeriod {
  implicit val defaultFormat: Format[PartialPeriod] = Json.format
}

case class PeriodWithPaymentDate(period: Periods, paymentDate: PaymentDate)

object PeriodWithPaymentDate {
  implicit val defaultFormat: Format[PeriodWithPaymentDate] = Json.format
}
