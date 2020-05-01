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

class FurloughStatusSpec extends WordSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues {

  "furloughStatus" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(FurloughStatus.values.toSeq)

      forAll(gen) { furloughOngoing =>
        JsString(furloughOngoing.toString).validate[FurloughStatus].asOpt.value mustEqual furloughOngoing
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!FurloughStatus.values.map(_.toString).contains(_))

      forAll(gen) { invalidValue =>
        JsString(invalidValue).validate[FurloughStatus] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(FurloughStatus.values.toSeq)

      forAll(gen) { furloughOngoing =>
        Json.toJson(furloughOngoing) mustEqual JsString(furloughOngoing.toString)
      }
    }
  }
}
