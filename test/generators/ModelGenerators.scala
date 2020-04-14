/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package generators

import java.time.{Instant, LocalDate, ZoneOffset}

import models.PaymentFrequency.Weekly
import models._
import org.scalacheck.Arbitrary.arbDouble
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  implicit lazy val arbitraryNicCategory: Arbitrary[NicCategory] =
    Arbitrary {
      Gen.oneOf(NicCategory.values.toSeq)
    }

  implicit lazy val arbitrarySalaryQuestion: Arbitrary[Salary] =
    Arbitrary {
      for {
        salary <- Arbitrary.arbitrary[Double]
      } yield Salary(salary)
    }

  implicit lazy val arbitraryPaymentFrequency: Arbitrary[PaymentFrequency] =
    Arbitrary {
      Gen.oneOf(PaymentFrequency.values.toSeq)
    }

  implicit lazy val arbitraryPayQuestion: Arbitrary[PayQuestion] =
    Arbitrary {
      Gen.oneOf(PayQuestion.values.toSeq)
    }

  val claimPeriodModelGen = for {
    startDate <- periodDatesBetween(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 5, 31))
    endDate   <- periodDatesBetween(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 5, 31))
  } yield ClaimPeriodModel(startDate, endDate)

  implicit lazy val arbitraryClaimPeriodModel: Arbitrary[ClaimPeriodModel] = Arbitrary(claimPeriodModelGen)

  private def periodDatesBetween(min: LocalDate, max: LocalDate): Gen[LocalDate] = {

    def toMillis(date: LocalDate): Long =
      date.atStartOfDay.atZone(ZoneOffset.UTC).toInstant.toEpochMilli

    Gen.choose(toMillis(min), toMillis(max)).map { millis =>
      Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC).toLocalDate
    }
  }

  val testOnlyNICGrantModelGen = for {
    startDate <- periodDatesBetween(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 5, 31))
    endDate   <- periodDatesBetween(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 5, 31))
    payDate   <- periodDatesBetween(LocalDate.of(2020, 5, 1), LocalDate.of(2020, 5, 31))
  } yield TestOnlyNICGrantModel(startDate, endDate, 1000, Weekly, payDate)

  implicit lazy val arbitraryTestOnlyNICGrantModel: Arbitrary[TestOnlyNICGrantModel] = Arbitrary(testOnlyNICGrantModelGen)
}
