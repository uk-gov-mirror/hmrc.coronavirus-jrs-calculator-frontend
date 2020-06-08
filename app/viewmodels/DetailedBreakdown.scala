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

package viewmodels

import models.{NicBreakdown, PensionBreakdown, Periods, PhaseTwoFurloughBreakdown, PhaseTwoNicBreakdown, PhaseTwoPensionBreakdown}
import views.ViewUtils._

case class DetailedBreakdown(period: Periods, furlough: DetailedFurloughBreakdown, nic: NicBreakdown, pension: PensionBreakdown) {
  def payPeriodStart: String = dateToStringWithoutYear(period.period.start)
  def payPeriodEnd: String = dateToString(period.period.end)
}

case class PhaseTwoDetailedBreakdown(
  period: Periods,
  furlough: PhaseTwoFurloughBreakdown,
  nic: PhaseTwoNicBreakdown,
  pension: PhaseTwoPensionBreakdown) {
  def payPeriodStart: String = dateToStringWithoutYear(period.period.start)
  def payPeriodEnd: String = dateToString(period.period.end)
}
