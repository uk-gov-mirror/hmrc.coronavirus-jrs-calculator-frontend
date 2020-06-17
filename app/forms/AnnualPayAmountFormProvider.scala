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

import forms.mappings.Mappings
import javax.inject.Inject
import models.AnnualPayAmount
import play.api.data.Form
import play.api.data.Forms.mapping

class AnnualPayAmountFormProvider @Inject() extends Mappings {

  def apply(): Form[AnnualPayAmount] =
    Form(
      mapping(
        "value" -> bigDecimal(
          requiredKey = "annualPayAmount.error.required",
          nonNumericKey = "annualPayAmount.error.invalid"
        ).verifying(greaterThan(BigDecimal(0.0), "amount.error.must.be.positive"))
          .verifying(maxTwoDecimals())
      )(AnnualPayAmount.apply)(AnnualPayAmount.unapply)
    )
}
