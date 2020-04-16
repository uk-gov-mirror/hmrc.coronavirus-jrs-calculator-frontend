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

class VariableLengthEmployedSpec extends WordSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues {

  "VariableLengthEmployed" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(VariableLengthEmployed.values.toSeq)

      forAll(gen) { variableLengthEmployed =>
        JsString(variableLengthEmployed.toString).validate[VariableLengthEmployed].asOpt.value mustEqual variableLengthEmployed
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!VariableLengthEmployed.values.map(_.toString).contains(_))

      forAll(gen) { invalidValue =>
        JsString(invalidValue).validate[VariableLengthEmployed] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(VariableLengthEmployed.values.toSeq)

      forAll(gen) { variableLengthEmployed =>
        Json.toJson(variableLengthEmployed) mustEqual JsString(variableLengthEmployed.toString)
      }
    }
  }
}
