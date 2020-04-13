/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms.behaviours

import play.api.data.{Form, FormError}

trait DoubleFieldBehaviours extends FieldBehaviours {

  def doubleField(form: Form[_], fieldName: String, error: FormError): Unit = {

    "bind all double values" in {
      forAll(positveDoubles -> "doubles") { double: Double =>
        val result = form.bind(Map(fieldName -> double.toString)).apply(fieldName)
        result.errors shouldEqual Seq.empty
      }
    }

    "not bind non-numeric numbers" in {

      forAll(nonNumerics -> "nonNumeric") { nonNumeric =>
        val result = form.bind(Map(fieldName -> nonNumeric)).apply(fieldName)
        result.errors shouldEqual Seq(error)
      }
    }
  }
}
