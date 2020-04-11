/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package generators

import java.time.{Instant, LocalDate, ZoneOffset}

import models._
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  val claimPeriodModelGen = for {
    startDate <- periodDatesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
    endDate <- periodDatesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
  } yield ClaimPeriodModel(startDate, endDate)

  implicit lazy val arbitraryClaimPeriodModel: Arbitrary[ClaimPeriodModel] = Arbitrary(claimPeriodModelGen)

  private def periodDatesBetween(min: LocalDate, max: LocalDate): Gen[LocalDate] = {

    def toMillis(date: LocalDate): Long =
      date.atStartOfDay.atZone(ZoneOffset.UTC).toInstant.toEpochMilli

    Gen.choose(toMillis(min), toMillis(max)).map {
      millis =>
        Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC).toLocalDate
    }
  }
}
