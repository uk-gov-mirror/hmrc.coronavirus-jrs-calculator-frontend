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
import models.Salary
import play.api.data.Form
import play.api.data.Forms._

class SalaryQuestionFormProvider @Inject() extends Mappings {

  def apply(): Form[Salary] = Form(
    mapping(
      "value" -> bigDecimal(
        requiredKey = "salaryQuestion.salary.error.required",
        nonNumericKey = "salaryQuestion.salary.error.invalid"
      ).verifying(positiveValue())
        .verifying(maxTwoDecimals())
    )(Salary.apply)(Salary.unapply)
  )
}
