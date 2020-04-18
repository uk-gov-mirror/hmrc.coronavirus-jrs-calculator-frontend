/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package generators

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryVariableGrossPayUserAnswersEntry: Arbitrary[(VariableGrossPayPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[VariableGrossPayPage.type]
        value <- arbitrary[VariableGrossPay].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryEmployeeStartDateUserAnswersEntry: Arbitrary[(EmployeeStartDatePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[EmployeeStartDatePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryVariableLengthEmployedUserAnswersEntry: Arbitrary[(VariableLengthEmployedPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[VariableLengthEmployedPage.type]
        value <- arbitrary[VariableLengthEmployed].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryFurloughStartDateUserAnswersEntry: Arbitrary[(FurloughStartDatePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FurloughStartDatePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryFurloughEndDateUserAnswersEntry: Arbitrary[(FurloughEndDatePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FurloughEndDatePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryFurloughDatesUserAnswersEntry: Arbitrary[(FurloughDatesPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FurloughDatesPage.type]
        value <- arbitrary[FurloughDates].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTaxYearPayDateUserAnswersEntry: Arbitrary[(TaxYearPayDatePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TaxYearPayDatePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryFurloughQuestionUserAnswersEntry: Arbitrary[(FurloughQuestionPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FurloughQuestionPage.type]
        value <- arbitrary[FurloughQuestion].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryClaimPeriodEndUserAnswersEntry: Arbitrary[(ClaimPeriodEndPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ClaimPeriodEndPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryClaimPeriodStartUserAnswersEntry: Arbitrary[(ClaimPeriodStartPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ClaimPeriodStartPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPensionAutoEnrolmentUserAnswersEntry: Arbitrary[(PensionAutoEnrolmentPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PensionAutoEnrolmentPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryNicCategoryUserAnswersEntry: Arbitrary[(NicCategoryPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[NicCategoryPage.type]
        value <- arbitrary[NicCategory].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySalaryQuestionUserAnswersEntry: Arbitrary[(SalaryQuestionPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SalaryQuestionPage.type]
        value <- arbitrary[Salary].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPaymentFrequencyUserAnswersEntry: Arbitrary[(PaymentFrequencyPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PaymentFrequencyPage.type]
        value <- arbitrary[PaymentFrequency].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPayQuestionUserAnswersEntry: Arbitrary[(PayQuestionPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PayQuestionPage.type]
        value <- arbitrary[PayQuestion].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPayDateUserAnswersEntry: Arbitrary[(PayDatePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PayDatePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

}
