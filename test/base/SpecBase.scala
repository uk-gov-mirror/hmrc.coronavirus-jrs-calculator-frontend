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

package base

import models.UserAnswers
import org.scalatest.{MustMatchers, OptionValues, TryValues, WordSpec}
import play.api.libs.json.Writes
import queries.Settable

trait SpecBase extends WordSpec with MustMatchers with TryValues with OptionValues {

  implicit class UserAnswersHelper(val userAnswers: UserAnswers) {
    def setValue[A](page: Settable[A], value: A, idx: Option[Int] = None)(implicit writes: Writes[A]) =
      userAnswers.set(page, value, idx).success.value
  }

}
