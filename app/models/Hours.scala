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

package models

import play.api.libs.json.Format
import utils.ValueClassFormat

case class Hours(value: Double)

object Hours {
  implicit val format: Format[Hours] = ValueClassFormat.format(value => Hours.apply(value.toDouble))(_.value)

  implicit class Defaulted(maybeAmount: Option[Hours]) {
    def defaulted: Hours = maybeAmount.fold(Hours(0.0))(v => v)
  }

  implicit class FromDouble(value: Double) {
    def toAmount: Hours = Hours(value)
  }
}
