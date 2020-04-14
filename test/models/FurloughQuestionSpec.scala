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

class FurloughQuestionSpec extends WordSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues {

  "FurloughQuestion" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(FurloughQuestion.values.toSeq)

      forAll(gen) { furloughQuestion =>
        JsString(furloughQuestion.toString).validate[FurloughQuestion].asOpt.value mustEqual furloughQuestion
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!FurloughQuestion.values.map(_.toString).contains(_))

      forAll(gen) { invalidValue =>
        JsString(invalidValue).validate[FurloughQuestion] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(FurloughQuestion.values.toSeq)

      forAll(gen) { furloughQuestion =>
        Json.toJson(furloughQuestion) mustEqual JsString(furloughQuestion.toString)
      }
    }
  }
}
