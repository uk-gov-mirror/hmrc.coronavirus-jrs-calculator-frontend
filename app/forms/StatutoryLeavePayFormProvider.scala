/*
 * Copyright 2021 HM Revenue & Customs
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

import forms.mappings.Mappings

import javax.inject.Inject
import models.Amount
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.i18n.Messages
import utils.CurrencyFormatter

class StatutoryLeavePayFormProvider @Inject() extends Mappings with CurrencyFormatter {

  def apply(referencePay: BigDecimal)(implicit messages: Messages): Form[Amount] =
    Form(
      mapping(
        "value" -> bigDecimal("statutoryLeavePay.error.required", "statutoryLeavePay.error.invalid")
          .verifying(maxTwoDecimals())
          .verifying(greaterThan(BigDecimal(0.0), "statutoryLeavePay.error.moreThan0"))
          .verifying(lessThan(referencePay, "statutoryLeavePay.error.lessThan", currencyFormatAsNonHTMLString(referencePay)))
      )(Amount.apply)(Amount.unapply)
    )
}
