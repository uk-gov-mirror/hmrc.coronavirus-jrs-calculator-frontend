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

package config

trait UrlConfiguration {

  val calculationGuidance: String =
    "https://www.gov.uk/guidance/work-out-80-of-your-employees-wages-to-claim-through-the-coronavirus-job-retention-scheme"

  val ninoCatLetter: String = "https://www.gov.uk/national-insurance-rates-letters/category-letters"

  val workOutHowMuch: String =
    "https://www.gov.uk/guidance/calculate-how-much-you-can-claim-using-the-coronavirus-job-retention-scheme#work-out-how-much-you-can-claim-for-employer-national-insurance-contributions-nics"

  val webchatHelpUrl: String =
    "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/get-help-with-the-coronavirus-job-retention-scheme"

  val jobRetentionScheme: String = "https://www.gov.uk/guidance/claim-for-wages-through-the-coronavirus-job-retention-scheme"

}
