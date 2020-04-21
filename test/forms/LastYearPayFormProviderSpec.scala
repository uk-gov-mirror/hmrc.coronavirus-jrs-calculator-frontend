/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import forms.behaviours.BigDecimalFieldBehaviours
import play.api.data.FormError

class LastYearPayFormProviderSpec extends BigDecimalFieldBehaviours {

  val form = new LastYearPayFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "lastYearPay.error.required"
    val invalidKey = "lastYearPay.error.nonNumeric"

    behave like bigDecimalField(
      form,
      fieldName,
      error = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
