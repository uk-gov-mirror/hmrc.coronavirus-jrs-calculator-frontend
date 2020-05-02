/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import java.time.LocalDate

sealed trait FurloughDates {
  def start: LocalDate
}

final case class FurloughOngoing(start: LocalDate) extends FurloughDates
final case class FurloughEnded(start: LocalDate, end: LocalDate) extends FurloughDates

final case class FurloughWithinClaim(start: LocalDate, end: LocalDate)

object FurloughWithinClaim {
  def apply(period: Period): FurloughWithinClaim = FurloughWithinClaim(period.start, period.end)
}

object FurloughDates {
  def apply(start: LocalDate, end: Option[LocalDate] = None): FurloughDates =
    end.fold[FurloughDates](FurloughOngoing(start))(FurloughEnded(start, _))
}
