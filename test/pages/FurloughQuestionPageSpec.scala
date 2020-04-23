/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import java.time.LocalDate

import models.{FurloughOngoing, UserAnswers}
import pages.behaviours.PageBehaviours

class FurloughOngoingSpec extends PageBehaviours {

  "furloughOngoingPage" must {

    beRetrievable[FurloughOngoing](FurloughOngoingPage)

    beSettable[FurloughOngoing](FurloughOngoingPage)

    beRemovable[FurloughOngoing](FurloughOngoingPage)

    "remove furlough end date when answered 'No'" in {
      val initialAnswers = UserAnswers("id")
        .set(FurloughOngoingPage, FurloughOngoing.Yes)
        .success
        .get
        .set(FurloughEndDatePage, LocalDate.of(2020, 3, 1))
        .success
        .get

      val updatedAnswers = initialAnswers.set(FurloughOngoingPage, FurloughOngoing.No).success.value

      updatedAnswers.get(FurloughEndDatePage) must not be defined
    }

  }
}
