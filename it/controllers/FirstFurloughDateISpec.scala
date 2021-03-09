package controllers

import assets.BaseITConstants
import assets.PageTitles.firstFurloughDate
import models.UserAnswers
import play.api.http.Status._
import play.api.libs.json.Json
import utils.{CreateRequestHelper, CustomMatchers, ITCoreTestData, IntegrationSpecBase}

class FirstFurloughDateISpec extends IntegrationSpecBase with CreateRequestHelper with CustomMatchers with BaseITConstants with ITCoreTestData {

  "GET /first-furlough-date" when {

    "render the first furlough date page" in {

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

  "POST /first-furlough-date" when {

    "valid values supplied" must {

      "redirect to onward route" in {

        val userAnswers: UserAnswers = variablePayNewStarterEmployeeJourney

        setAnswers(userAnswers)

        val res = postRequestHeader(
          path = "/first-furlough-date",
          formJson = Json.obj(
            "firstFurloughDate.day" -> "11",
            "firstFurloughDate.month" -> "11",
            "firstFurloughDate.year" -> "2020"
          )
        )("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

        whenReady(res) { result =>
          result should have(
            httpStatus(SEE_OTHER),
            redirectLocation(controllers.routes.PayDateController.onPageLoad(1).url)
          )
        }
      }
    }
  }
}

