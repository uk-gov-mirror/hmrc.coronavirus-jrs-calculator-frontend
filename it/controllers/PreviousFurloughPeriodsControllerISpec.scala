package controllers

import assets.BaseITConstants
import assets.PageTitles.{firstFurloughDate, previousFurloughPeriods}
import models.UserAnswers
import play.api.http.Status.OK
import utils.{CreateRequestHelper, CustomMatchers, ITCoreTestData, IntegrationSpecBase}

class PreviousFurloughPeriodsControllerISpec extends IntegrationSpecBase with CreateRequestHelper with CustomMatchers with BaseITConstants with ITCoreTestData {

  "GET /furloughed-more-than-once" when {

    "redirect to the .onPageLoad" in {

      val userAnswers: UserAnswers = hasTheEmployerHadPreviousFurloughPeriods

      setAnswers(userAnswers)

      val res = getRequestHeaders("/furloughed-more-than-once")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

      whenReady(res) { result =>
        result should have(
          httpStatus(OK),
          titleOf(previousFurloughPeriods)
        )
      }
    }
  }
}
