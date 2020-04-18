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

class FurloughCalculationsSpec extends WordSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues {

  "FurloughCalculations" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(FurloughCalculations.values.toSeq)

      forAll(gen) { furloughCalculations =>
        JsString(furloughCalculations.toString).validate[FurloughCalculations].asOpt.value mustEqual furloughCalculations
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!FurloughCalculations.values.map(_.toString).contains(_))

      forAll(gen) { invalidValue =>
        JsString(invalidValue).validate[FurloughCalculations] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(FurloughCalculations.values.toSeq)

      forAll(gen) { furloughCalculations =>
        Json.toJson(furloughCalculations) mustEqual JsString(furloughCalculations.toString)
      }
    }
  }
}
