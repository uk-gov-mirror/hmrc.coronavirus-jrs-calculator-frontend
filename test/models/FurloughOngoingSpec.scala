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

class FurloughOngoingSpec extends WordSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues {

  "furloughOngoing" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(FurloughOngoing.values.toSeq)

      forAll(gen) { furloughOngoing =>
        JsString(furloughOngoing.toString).validate[FurloughOngoing].asOpt.value mustEqual furloughOngoing
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!FurloughOngoing.values.map(_.toString).contains(_))

      forAll(gen) { invalidValue =>
        JsString(invalidValue).validate[FurloughOngoing] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(FurloughOngoing.values.toSeq)

      forAll(gen) { furloughOngoing =>
        Json.toJson(furloughOngoing) mustEqual JsString(furloughOngoing.toString)
      }
    }
  }
}
