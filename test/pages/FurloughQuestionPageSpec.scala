/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import java.time.LocalDate

import models.{FurloughStatus, UserAnswers}
import pages.behaviours.PageBehaviours

class FurloughOngoingSpec extends PageBehaviours {

  "furloughOngoingPage" must {

    beRetrievable[FurloughStatus](FurloughStatusPage)

    beSettable[FurloughStatus](FurloughStatusPage)

    beRemovable[FurloughStatus](FurloughStatusPage)

    "remove furlough end date when answered 'No'" in {
      val initialAnswers = UserAnswers("id")
        .set(FurloughStatusPage, FurloughStatus.FurloughEnded)
        .success
        .get
        .set(FurloughEndDatePage, LocalDate.of(2020, 3, 1))
        .success
        .get

      val updatedAnswers = initialAnswers.set(FurloughStatusPage, FurloughStatus.FurloughOngoing).success.value

      updatedAnswers.get(FurloughEndDatePage) must not be defined
    }

  }
}
