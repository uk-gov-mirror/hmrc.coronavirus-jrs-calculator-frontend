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

object BeenOnStatutoryLeaveMessages {

  def h1(boundaryStart: String, boundaryEnd: String) =
    s"Has this employee been on statutory leave for part of the period between $boundaryStart and $boundaryEnd?"
  val p                    = "For this calculation, statutory leave only includes:"
  val bullet1              = "statutory sick pay related leave"
  val bullet2              = "family related statutory leave, for example paternity leave"
  val bullet3              = "reduced rate paid leave following a period of statutory sick pay related leave"
  val bullet4              = "reduced rate paid leave following a period of family related statutory leave"
  val dayEmploymentStarted = "the day their employment started"
  val insetText            = "If the employee was on statutory leave for the full period between these dates, you must answer ‘no’."

}
