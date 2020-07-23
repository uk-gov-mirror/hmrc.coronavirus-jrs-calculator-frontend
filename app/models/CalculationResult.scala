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

case class FurloughCalculationResult(total: BigDecimal, periodBreakdowns: Seq[FurloughBreakdown])
case class PhaseTwoFurloughCalculationResult(total: BigDecimal, periodBreakdowns: Seq[PhaseTwoFurloughBreakdown]) {
  def seventy = periodBreakdowns.map(_.seventy).sum
  def sixty = periodBreakdowns.map(_.sixty).sum
  def seventyDiff = total - seventy
  def sixtyDiff = total - sixty
}
case class NicCalculationResult(total: BigDecimal, periodBreakdowns: Seq[NicBreakdown])
case class PhaseTwoNicCalculationResult(total: BigDecimal, periodBreakdowns: Seq[PhaseTwoNicBreakdown])
case class PensionCalculationResult(total: BigDecimal, periodBreakdowns: Seq[PensionBreakdown])
case class PhaseTwoPensionCalculationResult(total: BigDecimal, periodBreakdowns: Seq[PhaseTwoPensionBreakdown])
