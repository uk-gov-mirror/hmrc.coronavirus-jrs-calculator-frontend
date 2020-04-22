/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import java.time.LocalDate

import models.{FurloughQuestion, UserAnswers}
import pages.behaviours.PageBehaviours

class FurloughQuestionSpec extends PageBehaviours {

  "FurloughQuestionPage" must {

    beRetrievable[FurloughQuestion](FurloughQuestionPage)

    beSettable[FurloughQuestion](FurloughQuestionPage)

    beRemovable[FurloughQuestion](FurloughQuestionPage)

    "remove furlough end date when answered 'No'" in {
      val initialAnswers = UserAnswers("id")
        .set(FurloughQuestionPage, FurloughQuestion.Yes)
        .success
        .get
        .set(FurloughEndDatePage, LocalDate.of(2020, 3, 1))
        .success
        .get

      val updatedAnswers = initialAnswers.set(FurloughQuestionPage, FurloughQuestion.No).success.value

      updatedAnswers.get(FurloughEndDatePage) must not be defined
    }

  }
}
