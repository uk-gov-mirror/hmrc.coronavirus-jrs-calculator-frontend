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

import config.SchemeConfiguration
import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.i18n.Messages

import java.time.LocalDate
import javax.inject.Inject

class FirstFurloughDateFormProvider @Inject() extends Mappings with SchemeConfiguration {

  def apply(furloughStartDate: LocalDate)(implicit messages: Messages): Form[LocalDate] =
    Form(
      "firstFurloughDate" -> localDate(invalidKey = "firstFurloughStartDate.error.invalid")
        .verifying(validFirstFurloughDate(furloughStartDate)))

  private def validFirstFurloughDate(furloughStartDate: LocalDate)(implicit messages: Messages): Constraint[LocalDate] = Constraint {
    firstFurloughDate =>
      if (!furloughStartDate.isAfter(firstFurloughDate)) {
        Invalid("firstFurloughStartDate.error.afterStartDate")
      } else if (firstFurloughDate.isBefore(extensionStartDate)) {
        Invalid("firstFurloughStartDate.error.beforeExtensionDate")
      } else {
        Valid
      }
  }
}
