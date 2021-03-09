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

object PaymentFrequencyMessages {

  val heading = "How often do you pay this employee?"
  val indent  = "The employees current pay frequency is different from the pay frequency in the reference period"
  val p1 = {
    "You cannot use the calculator if this employee is on fixed pay and their pay frequency has changed " +
      "between the reference period and the pay period you are calculating for. For example, " +
      "if they have changed from monthly pay to weekly pay. You will need to manually work out what you can claim."
  }
  val p2 = "You can"
  val link = {
    "read about the reference period and how to work out what you can claim manually " +
      "using the calculation guidance (opens in a new tab)."
  }

}
