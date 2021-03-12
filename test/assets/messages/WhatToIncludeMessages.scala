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

object WhatToIncludeMessages {

  val summary                   = "What should be included in this amount?"
  val include                   = "Include:"
  val includeCylb               = "gross amount the employee earned in this pay period, even if paid in a different pay period"
  val includeL1                 = "gross amount payable to the employee"
  def inclL1(cylb: Boolean)     = if (cylb) includeCylb else includeL1
  val includeL2                 = "non-discretionary overtime"
  val includeL3                 = "non-discretionary fees"
  val includeL4                 = "non-discretionary commission payments"
  val includeL5                 = "piece rate payments"
  val doNotInclude              = "Do not include:"
  val doNotIncludeCylb          = "payments earned in a different pay period, even if they appear on the payslip for this pay period"
  val doNotIncludeL1            = "discretionary payments you’re not contractually obliged to pay, including:"
  def dontInclL1(cylb: Boolean) = if (cylb) doNotIncludeCylb else doNotIncludeL1
  val doNotIncludeL1L1          = "tips"
  val doNotIncludeL1L2          = "discretionary bonuses"
  val doNotIncludeL1L3          = "discretionary commission payments"
  val doNotIncludeL2            = "non-cash payments"
  val doNotIncludeL3 =
    "non-monetary benefits like benefits in kind (such as a company car) and salary sacrifice schemes that reduce taxable pay"

}
