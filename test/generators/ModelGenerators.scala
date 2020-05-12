/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package generators

import java.time.{Instant, LocalDate, ZoneOffset}

import models._
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  implicit lazy val arbitraryAdditionalPayment: Arbitrary[AdditionalPayment] =
    Arbitrary {
      for {
        date  <- Arbitrary.arbitrary[LocalDate]
        value <- Arbitrary.arbitrary[BigDecimal]
      } yield AdditionalPayment(date, Amount(value))
    }

  implicit lazy val arbitraryTopUpQuestion: Arbitrary[TopUpStatus] =
    Arbitrary {
      Gen.oneOf(TopUpStatus.values.toSeq)
    }

  implicit lazy val arbitraryTopUpPayment: Arbitrary[TopUpPayment] =
    Arbitrary {
      for {
        date  <- Arbitrary.arbitrary[LocalDate]
        value <- Arbitrary.arbitrary[BigDecimal]
      } yield TopUpPayment(date, Amount(value))
    }

  implicit lazy val arbitraryTopUpPeriod: Arbitrary[TopUpPeriod] =
    Arbitrary {
      for {
        date  <- Arbitrary.arbitrary[LocalDate]
        value <- Arbitrary.arbitrary[BigDecimal]
      } yield TopUpPeriod(date, Amount(value))
    }

  implicit lazy val arbitraryEmployeeStarted: Arbitrary[EmployeeStarted] =
    Arbitrary {
      Gen.oneOf(EmployeeStarted.values.toSeq)
    }

  implicit lazy val arbitraryfurloughOngoing: Arbitrary[FurloughStatus] =
    Arbitrary {
      Gen.oneOf(FurloughStatus.values.toSeq)
    }

  implicit lazy val arbitraryAdditionalPaymentStatus: Arbitrary[AdditionalPaymentStatus] =
    Arbitrary {
      Gen.oneOf(AdditionalPaymentStatus.values)
    }

  implicit lazy val arbitraryNicCategory: Arbitrary[NicCategory] =
    Arbitrary {
      Gen.oneOf(NicCategory.values.toSeq)
    }

  implicit lazy val arbitraryPensionStatus: Arbitrary[PensionStatus] =
    Arbitrary {
      Gen.oneOf(PensionStatus.values.toSeq)
    }

  implicit lazy val arbitraryRegularPayAmount: Arbitrary[Salary] =
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

  implicit lazy val arbitraryAnnualPayAmountMethod: Arbitrary[AnnualPayAmount] =
    Arbitrary {
      for {
        value <- Arbitrary.arbitrary[BigDecimal]
      } yield AnnualPayAmount(value)
    }

  implicit lazy val arbitraryVariableLengthPartialPayMethod: Arbitrary[FurloughPartialPay] =
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
