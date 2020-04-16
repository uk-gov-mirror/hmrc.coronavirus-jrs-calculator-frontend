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

class FurloughDatesSpec extends WordSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues {

  "FurloughDates" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(FurloughDates.values.toSeq)

      forAll(gen) { furloughDates =>
        JsString(furloughDates.toString).validate[FurloughDates].asOpt.value mustEqual furloughDates
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!FurloughDates.values.map(_.toString).contains(_))

      forAll(gen) { invalidValue =>
        JsString(invalidValue).validate[FurloughDates] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(FurloughDates.values.toSeq)

      forAll(gen) { furloughDates =>
        Json.toJson(furloughDates) mustEqual JsString(furloughDates.toString)
      }
    }
  }
}
