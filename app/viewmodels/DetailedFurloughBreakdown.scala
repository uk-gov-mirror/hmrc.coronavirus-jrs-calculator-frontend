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

import models.{Amount, FullPeriod, FurloughCap, PartialPeriod, PaymentWithPeriod}
import services.Calculators.AmountRounding

case class DetailedFurloughBreakdown(employeesWages: Amount, furloughCap: FurloughCap, furloughGrant: Amount, payment: PaymentWithPeriod) {

  def isCapped: Boolean = (employeesWages.value * 0.8) > furloughCap.value

  def calculatedFurlough = Amount(employeesWages.value * 0.8).halfUp.value.formatted("%.2f")

  def formattedWages: String = employeesWages.value.formatted("%.2f")

  def formattedGrant: String = furloughGrant.value.formatted("%.2f")
}
