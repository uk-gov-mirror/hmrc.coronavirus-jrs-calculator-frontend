/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms.behaviours

import play.api.data.{Form, FormError}

trait BigDecimalFieldBehaviours extends FieldBehaviours {

  def bigDecimalField(form: Form[_], fieldName: String, error: FormError): Unit = {

    "bind all big decimal values with decimal places <= 2" in {
      forAll(positiveBigDecimalsWith2dp -> "bigDecimals") { bigDecimal: BigDecimal =>
        val result = form.bind(Map(fieldName -> bigDecimal.toString)).apply(fieldName)
        result.errors shouldEqual Seq.empty
      }
    }

    "not bind all big decimal values with decimal places > 2" in {
      forAll(positiveBigDecimalsWithMoreThan2dp -> "bigDecimals") { bigDecimal: BigDecimal =>
        val result = form.bind(Map(fieldName -> bigDecimal.toString)).apply(fieldName)
        result.errors shouldEqual Seq(FormError("value", "amount.error.max.2.decimals"))
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
