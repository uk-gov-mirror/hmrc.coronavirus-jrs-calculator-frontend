package controllers

import assets.BaseITConstants
import assets.PageTitles.firstFurloughDate
import models.UserAnswers
import play.api.http.Status._
import utils.{CreateRequestHelper, CustomMatchers, ITCoreTestData, IntegrationSpecBase}

class FirstFurloughDateISpec extends IntegrationSpecBase with CreateRequestHelper with CustomMatchers with BaseITConstants with ITCoreTestData {

  "GET /first-furlough-date" when {

    "redirect to the .onPageLoad" in {

      val userAnswers: UserAnswers = variablePayNewStarterEmployeeJourney

      setAnswers(userAnswers)

      val res = getRequestHeaders("/first-furlough-date")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

      whenReady(res) { result =>
        result should have(
          httpStatus(OK),
          titleOf(firstFurloughDate)
        )
      }
    }
  }
}

