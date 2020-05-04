/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package generators

import java.time.{Instant, LocalDate, ZoneOffset}

import models._
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  implicit lazy val arbitraryFurloughTopUpStatus: Arbitrary[FurloughTopUpStatus] =
    Arbitrary {
      Gen.oneOf(FurloughTopUpStatus.values.toSeq)
    }

  implicit lazy val arbitraryEmployeeStarted: Arbitrary[EmployeeStarted] =
    Arbitrary {
      Gen.oneOf(EmployeeStarted.values.toSeq)
    }

  implicit lazy val arbitraryfurloughOngoing: Arbitrary[FurloughStatus] =
    Arbitrary {
      Gen.oneOf(FurloughStatus.values.toSeq)
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
        salary <- Arbitrary.arbitrary[BigDecimal]
      } yield Salary(salary)
    }

  implicit lazy val arbitraryCylbpayMethod: Arbitrary[CylbPayment] =
    Arbitrary {
      for {
        date  <- Arbitrary.arbitrary[LocalDate]
        value <- Arbitrary.arbitrary[BigDecimal]
      } yield CylbPayment(date, Amount(value))
    }

  implicit lazy val arbitraryVariableGrosspayMethod: Arbitrary[VariableGrossPay] =
    Arbitrary {
      for {
        value <- Arbitrary.arbitrary[BigDecimal]
      } yield VariableGrossPay(value)
    }

  implicit lazy val arbitraryVariableLengthPartialpayMethod: Arbitrary[FurloughPartialPay] =
    Arbitrary {
      for {
        value <- Arbitrary.arbitrary[BigDecimal]
      } yield FurloughPartialPay(value)
    }

  implicit lazy val arbitraryPaymentFrequency: Arbitrary[PaymentFrequency] =
    Arbitrary {
      Gen.oneOf(PaymentFrequency.values.toSeq)
    }

  implicit lazy val arbitrarypayMethod: Arbitrary[PayMethod] =
    Arbitrary {
      Gen.oneOf(PayMethod.values.toSeq)
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
