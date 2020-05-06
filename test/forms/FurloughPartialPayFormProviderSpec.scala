/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms

import forms.behaviours.BigDecimalFieldBehaviours
import play.api.data.FormError

class FurloughPartialPayFormProviderSpec extends BigDecimalFieldBehaviours {

  val form = new FurloughPartialPayFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "FurloughPartialPay.error.required"
    val invalidKey = "FurloughPartialPay.error.invalid"

    "accept 0 as a valid input" in {
      val value = "0.0"

      val data = Map("value" -> value)

      val result = form.bind(data)

      result.errors shouldBe empty
      result.value.value.value shouldEqual BigDecimal(value)
    }

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
