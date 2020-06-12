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

package forms.behaviours

import play.api.data.{Form, FormError}

trait DoubleFieldBehaviours extends FieldBehaviours {

  def doubleField(form: Form[_], fieldName: String, error: FormError, maxValue: Option[Double] = None): Unit = {

    "bind all double values" in {
      forAll(maxValue.fold(positiveDoubles)(positiveDoublesWithMax) -> "doubles") { double: Double =>
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
