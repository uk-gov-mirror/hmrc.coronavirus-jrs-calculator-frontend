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

  implicit lazy val arbitraryTestOnlyNICGrantCalculatorUserAnswersEntry: Arbitrary[(TestOnlyNICGrantCalculatorPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TestOnlyNICGrantCalculatorPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

}
