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

class PaymentFrequencySpec extends WordSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues {

  "PaymentFrequency" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(PaymentFrequency.values.toSeq)

      forAll(gen) { paymentFrequency =>
        JsString(paymentFrequency.toString).validate[PaymentFrequency].asOpt.value mustEqual paymentFrequency
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!PaymentFrequency.values.map(_.toString).contains(_))

      forAll(gen) { invalidValue =>
        JsString(invalidValue).validate[PaymentFrequency] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(PaymentFrequency.values.toSeq)

      forAll(gen) { paymentFrequency =>
        Json.toJson(paymentFrequency) mustEqual JsString(paymentFrequency.toString)
      }
    }
  }
}
