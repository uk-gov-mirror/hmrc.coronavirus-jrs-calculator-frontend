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

class FurloughTopUpStatusSpec extends WordSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues {

  "FurloughTopUpStatus" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(FurloughTopUpStatus.values.toSeq)

      forAll(gen) { furloughTopUp =>
        JsString(furloughTopUp.toString).validate[FurloughTopUpStatus].asOpt.value mustEqual furloughTopUp
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!FurloughTopUpStatus.values.map(_.toString).contains(_))

      forAll(gen) { invalidValue =>
        JsString(invalidValue).validate[FurloughTopUpStatus] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(FurloughTopUpStatus.values.toSeq)

      forAll(gen) { furloughTopUp =>
        Json.toJson(furloughTopUp) mustEqual JsString(furloughTopUp.toString)
      }
    }
  }
}
