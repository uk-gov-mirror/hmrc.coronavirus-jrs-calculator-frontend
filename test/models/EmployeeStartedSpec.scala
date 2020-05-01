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

class EmployeeStartedSpec extends WordSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues {

  "EmployeeStarted" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(EmployeeStarted.values.toSeq)

      forAll(gen) { employeeStarted =>
        JsString(employeeStarted.toString).validate[EmployeeStarted].asOpt.value mustEqual employeeStarted
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!EmployeeStarted.values.map(_.toString).contains(_))

      forAll(gen) { invalidValue =>
        JsString(invalidValue).validate[EmployeeStarted] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(EmployeeStarted.values.toSeq)

      forAll(gen) { employeeStarted =>
        Json.toJson(employeeStarted) mustEqual JsString(employeeStarted.toString)
      }
    }
  }
}
