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

package views.includes

trait BaseSelectors {

  val panelHeading = "main div div.govuk-panel.govuk-panel--confirmation h1"
  val panelTotalClaims = "#main-content div:nth-child(4)"
  val panelBody = "main div div.govuk-panel.govuk-panel--confirmation div.govuk-panel__body"
  val panelBodyTotalClaims = "#main-content div:nth-child(4) > strong"
  val h1: String = "h1"
  val h2ConfirmationPage = "#main-content > div > div > div > h2"
  val h3ConfirmationPage = "#main-content h2:nth-child(7)"
  val p: Int => String = i => s"main p:nth-of-type($i)"
  val legend: String = "fieldset legend"
  val link: Int => String = i => s"main a:nth-of-type($i)"
  val indent = "div.govuk-inset-text"
  val nthIndent: Int => String = i => s"div.govuk-inset-text:nth-of-type($i)"
  val hint = "main span.govuk-hint"
  val bullet: Int => String = i => s"main ul.govuk-list.govuk-list--bullet li:nth-of-type($i)"
  val label = "main label.govuk-label"
  val nthLabel: Int => String = i => s"form > div > div:nth-child($i) > label"
  val nthLabelHint: Int => String = i => s"form > div > div:nth-child($i) span.govuk-hint"
  val warning = "main .govuk-warning-text__text"
  val warningP: Int => String = i => s"main .govuk-warning-text__text p:nth-of-type($i)"
  val warningBullet: Int => String = i => s"main .govuk-warning-text__text ul.govuk-list.govuk-list--bullet li:nth-of-type($i)"
  val whoCanClaim = "#who-can-claim"
  val button = ".govuk-button"
  val secondaryButton = ".govuk-button--secondary"
  val subheadingSelector = "#subheading"
  val viewAddedSoFar = "#viewAddedSoFar"
  val h2: Int => String = i => s"#main-content h2:nth-of-type($i)"
  val dropdown = "#main-content summary"
  val dropdownP: Int => String = i => s"#main-content details > div > p:nth-child($i)"

}

object BaseSelectors extends BaseSelectors
