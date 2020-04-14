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

class NicCategorySpec extends WordSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues {

  "NicCategory" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(NicCategory.values.toSeq)

      forAll(gen) { nicCategory =>
        JsString(nicCategory.toString).validate[NicCategory].asOpt.value mustEqual nicCategory
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!NicCategory.values.map(_.toString).contains(_))

      forAll(gen) { invalidValue =>
        JsString(invalidValue).validate[NicCategory] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(NicCategory.values.toSeq)

      forAll(gen) { nicCategory =>
        Json.toJson(nicCategory) mustEqual JsString(nicCategory.toString)
      }
    }
  }
}
