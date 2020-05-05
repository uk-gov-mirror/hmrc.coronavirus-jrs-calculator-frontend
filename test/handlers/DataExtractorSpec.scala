/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import base.{CoreTestDataBuilder, SpecBase}
import models.UserAnswers
import play.api.libs.json.Json
import utils.CoreTestData

class DataExtractorSpec extends SpecBase with CoreTestData with CoreTestDataBuilder {

  "Extract prior furlough period from user answers" when {

    "employee start date is present" in new DataExtractor {
      val userAnswers = Json.parse(userAnswersJson(employeeStartDate = "2020-12-01")).as[UserAnswers]
      val expected = period("2020, 12, 1", "2020, 2, 29")

      extractPriorFurloughPeriod(userAnswers) mustBe Some(expected)
    }

    "employee start date is not present" in new DataExtractor {
      val userAnswers = Json.parse(userAnswersJson()).as[UserAnswers]
      val expected = period("2019, 4, 6", "2020, 2, 29")

      extractPriorFurloughPeriod(userAnswers) mustBe Some(expected)
    }

  }

}
