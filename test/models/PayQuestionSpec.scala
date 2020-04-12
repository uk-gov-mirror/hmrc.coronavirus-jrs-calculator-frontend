/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.libs.json.{JsError, JsString, Json}

class PayQuestionSpec extends WordSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues {

  "PayQuestion" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(PayQuestion.values.toSeq)

      forAll(gen) { payQuestion =>
        JsString(payQuestion.toString).validate[PayQuestion].asOpt.value mustEqual payQuestion
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!PayQuestion.values.map(_.toString).contains(_))

      forAll(gen) { invalidValue =>
        JsString(invalidValue).validate[PayQuestion] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(PayQuestion.values.toSeq)

      forAll(gen) { payQuestion =>
        Json.toJson(payQuestion) mustEqual JsString(payQuestion.toString)
      }
    }
  }
}
