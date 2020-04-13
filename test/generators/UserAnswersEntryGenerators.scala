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

  implicit lazy val arbitraryClaimPeriodUserAnswersEntry: Arbitrary[(ClaimPeriodPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ClaimPeriodPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }
}
