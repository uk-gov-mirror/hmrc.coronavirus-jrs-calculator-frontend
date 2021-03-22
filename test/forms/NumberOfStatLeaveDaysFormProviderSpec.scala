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

import java.time._

import base.SpecBaseControllerSpecs
import forms.behaviours.IntFieldBehaviours
import play.api.data.FormError

class NumberOfStatLeaveDaysFormProviderSpec extends SpecBaseControllerSpecs {

  val intFieldBehaviours = new IntFieldBehaviours

  import intFieldBehaviours._

  val boundaryStart = LocalDate.of(2019, 4, 6)
  val boundaryEnd   = LocalDate.of(2020, 4, 5)

  val form = new NumberOfStatLeaveDaysFormProvider()(boundaryStart, boundaryEnd)

  ".value" must {

    val fieldName = "value"

    val minimum = 0
    val maximum = Duration.between(boundaryStart.atStartOfDay(), boundaryEnd.atStartOfDay()).toDays.toInt

    val validDataGenerator = intsInRangeWithCommas(minimum, maximum)

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validDataGenerator
    )

    behave like intField(
      form,
      fieldName,
      nonNumericError = FormError(fieldName, "numberOfStatLeaveDays.error.nonNumeric"),
      wholeNumberError = FormError(fieldName, "numberOfStatLeaveDays.error.wholeNumber")
    )

    behave like intFieldWithRange(
      form,
      fieldName,
      minimum = minimum,
      maximum = maximum,
      expectedError = FormError(fieldName, "numberOfStatLeaveDays.error.outOfRange", Seq(minimum, maximum))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, "numberOfStatLeaveDays.error.required")
    )
  }
}
