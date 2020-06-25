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

package services

import base.SpecBase
import models.{BackFirstPage, BackToPreviousPage}
import utils.CoreTestData

class BackJourneyValidatorSpec extends SpecBase with CoreTestData {

  "enable back link on fast journey question pages if claim start&end, furlough start&end&status and pay periods exists" in new BackJourneyValidator {
    val answers = emptyUserAnswers
      .withFurloughStartDate("2020,1,1")
      .withFurloughEndDate("2020,2,1")
      .withClaimPeriodStart("2020,1,1")
      .withClaimPeriodEnd("2020,2,1")
      .withFurloughStatus()
      .withPayDate(List("2020,2,1"))

    validateBackJourney(answers) mustBe BackToPreviousPage
    validateBackJourney(
      emptyUserAnswers
        .withClaimPeriodStart("2020,1,1")
        .withClaimPeriodEnd("2020,2,1")) mustBe BackFirstPage
  }
}
