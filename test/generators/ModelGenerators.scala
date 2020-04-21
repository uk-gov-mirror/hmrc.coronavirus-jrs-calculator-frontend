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

  implicit lazy val arbitraryFurloughCalculations: Arbitrary[FurloughCalculations] =
    Arbitrary {
      Gen.oneOf(FurloughCalculations.values.toSeq)
    }

  implicit lazy val arbitraryVariableLengthEmployed: Arbitrary[VariableLengthEmployed] =
    Arbitrary {
      Gen.oneOf(VariableLengthEmployed.values.toSeq)
    }

  implicit lazy val arbitraryFurloughDates: Arbitrary[FurloughDates] =
    Arbitrary {
      Gen.oneOf(FurloughDates.values.toSeq)
    }

  implicit lazy val arbitraryFurloughQuestion: Arbitrary[FurloughQuestion] =
    Arbitrary {
      Gen.oneOf(FurloughQuestion.values.toSeq)
    }

  implicit lazy val arbitraryNicCategory: Arbitrary[NicCategory] =
    Arbitrary {
      Gen.oneOf(NicCategory.values.toSeq)
    }

  implicit lazy val arbitraryPensionStatus: Arbitrary[PensionStatus] =
    Arbitrary {
      Gen.oneOf(PensionStatus.values.toSeq)
    }

  implicit lazy val arbitrarySalaryQuestion: Arbitrary[Salary] =
    Arbitrary {
      for {
        salary <- Arbitrary.arbitrary[Double]
      } yield Salary(salary)
    }

  implicit lazy val arbitraryVariableGrossPayQuestion: Arbitrary[VariableGrossPay] =
    Arbitrary {
      for {
        value <- Arbitrary.arbitrary[BigDecimal]
      } yield VariableGrossPay(value)
    }

  implicit lazy val arbitraryVariableLengthPartialPayQuestion: Arbitrary[FurloughPartialPay] =
    Arbitrary {
      for {
        value <- Arbitrary.arbitrary[BigDecimal]
      } yield FurloughPartialPay(value)
    }

  implicit lazy val arbitraryPaymentFrequency: Arbitrary[PaymentFrequency] =
    Arbitrary {
      Gen.oneOf(PaymentFrequency.values.toSeq)
    }

  implicit lazy val arbitraryPayQuestion: Arbitrary[PayQuestion] =
    Arbitrary {
      Gen.oneOf(PayQuestion.values.toSeq)
    }

  val claimPeriodDatesGen = for {
    date <- periodDatesBetween(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 5, 31))
  } yield date

  implicit lazy val arbitraryClaimPeriod: Arbitrary[LocalDate] = Arbitrary(claimPeriodDatesGen)

  def periodDatesBetween(min: LocalDate, max: LocalDate): Gen[LocalDate] = {

    def toMillis(date: LocalDate): Long =
      date.atStartOfDay.atZone(ZoneOffset.UTC).toInstant.toEpochMilli

    Gen.choose(toMillis(min), toMillis(max)).map { millis =>
      Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC).toLocalDate
    }
  }

}
