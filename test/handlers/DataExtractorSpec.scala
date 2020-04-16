/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import base.SpecBase
import models.UserAnswers
import play.api.libs.json.Json
import utils.CoreTestData

class DataExtractorSpec extends SpecBase with CoreTestData {

  "Extract mandatory data in order to do the calculation" in new DataExtractor {
    val userAnswers = Json.parse(userAnswersJson).as[UserAnswers]

    extract(userAnswers) must matchPattern {
      case Some(MandatoryData(_, _, _, _, _, _, _)) =>
    }
  }
}
