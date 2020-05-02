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
import models.{MandatoryData, UserAnswers}
import play.api.libs.json.Json
import utils.CoreTestData

class DataExtractorSpec extends SpecBase with CoreTestData with CoreTestDataBuilder {

  "Extract mandatory data in order to do the calculation" in new DataExtractor {
    val userAnswers = Json.parse(userAnswersJson()).as[UserAnswers]

    extract(userAnswers) must matchPattern {
      case Some(MandatoryData(_, _, _, _, _, _, _, _, _)) =>
    }
  }

  "Extract prior furlough period from user answers" in new DataExtractor {
    val userAnswers = Json.parse(userAnswersJson(employeeStartDate = "2020-12-01")).as[UserAnswers]
    val expected = period("2020, 12, 1", "2020, 2, 29")

    extractPriorFurloughPeriod(userAnswers) mustBe Some(expected)
  }

}
