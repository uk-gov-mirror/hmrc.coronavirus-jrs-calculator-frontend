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

package assets.messages

import play.api.i18n.Messages
import views.ViewUtils._

import java.time.LocalDate

object RegularPayAmountMessages {

  def h1(cutoffDate: LocalDate)(implicit messages: Messages) =
    s"What was the employee paid in the last pay period ending on or before ${dateToString(cutoffDate)}?"

  def p1(cutoffDate: LocalDate)(implicit messages: Messages) =
    s"This is the gross amount paid in the last pay period ending on or before ${dateToString(cutoffDate)}, before deductions."

  val p2 = "Do not include discretionary payments, non-cash payments or non-monetary benefits."

  val indent =
    "If this employee started their employment with you during the pay period, enter the amount they would have received if it was a full pay period"
}
