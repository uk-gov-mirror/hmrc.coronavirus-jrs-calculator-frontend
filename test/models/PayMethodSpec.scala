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

class PayMethodSpec extends WordSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues {

  "PayMethod" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(PayMethod.values.toSeq)

      forAll(gen) { payMethod =>
        JsString(payMethod.toString).validate[PayMethod].asOpt.value mustEqual payMethod
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!PayMethod.values.map(_.toString).contains(_))

      forAll(gen) { invalidValue =>
        JsString(invalidValue).validate[PayMethod] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(PayMethod.values.toSeq)

      forAll(gen) { payMethod =>
        Json.toJson(payMethod) mustEqual JsString(payMethod.toString)
      }
    }
  }
}
