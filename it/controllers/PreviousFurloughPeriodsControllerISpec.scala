package controllers

import assets.BaseITConstants
import assets.PageTitles.{firstFurloughDate, previousFurloughPeriods}
import config.featureSwitch.{ExtensionTwoNewStarterFlow, FeatureSwitching}
import models.UserAnswers
import play.api.http.Status._
import play.api.libs.json.Json
import utils.LocalDateHelpers._
import utils.{CreateRequestHelper, CustomMatchers, ITCoreTestData, IntegrationSpecBase}
import views.ViewUtils.dateToString

class PreviousFurloughPeriodsControllerISpec extends IntegrationSpecBase with CreateRequestHelper with CustomMatchers with BaseITConstants with ITCoreTestData
 with FeatureSwitching {

  override def beforeAll(): Unit = {
    super.beforeAll()
    enable(ExtensionTwoNewStarterFlow)
  }

  override def afterAll(): Unit = {
    super.afterAll()
    disable(ExtensionTwoNewStarterFlow)
  }

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

  "POST /furloughed-more-than-once" when {
    "redirect to the pay date page when the user selects 'No'" in {
      val userAnswers: UserAnswers = hasTheEmployerHadPreviousFurloughPeriods

      setAnswers(userAnswers)

      val res = postRequestHeader("/furloughed-more-than-once", Json.obj("value" -> "false"))("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

      whenReady(res) { result =>
        result should have(
          httpStatus(SEE_OTHER),
          redirectLocation(controllers.routes.PayDateController.onPageLoad(1).url)
        )
      }
    }

    "redirect to the first furlough date page when the user selects 'Yes'" in {
      val userAnswers: UserAnswers = hasTheEmployerHadPreviousFurloughPeriods

      setAnswers(userAnswers)

      val res = postRequestHeader("/furloughed-more-than-once", Json.obj("value" -> "true"))("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

      whenReady(res) { result =>
        result should have(
          httpStatus(SEE_OTHER),
          redirectLocation(controllers.routes.FirstFurloughDateController.onPageLoad().url)
        )
      }
    }

    "show an error when the user does not select an answer - passing in the correct date for 5A employee (1st November 2020)" in {
      val userAnswers: UserAnswers = hasTheEmployerHadPreviousFurloughPeriods

      setAnswers(userAnswers)

      val res = postRequestHeader("/furloughed-more-than-once", Json.toJson("value", ""))("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

      whenReady(res) { result =>
        result should have(
          httpStatus(BAD_REQUEST),
          contentExists(s"Select yes if this employee has been furloughed more than once since ${dateToString(nov1st2020)}")
        )
      }
    }

    "show an error when the user does not select an answer - passing in the correct date for 5B employee (1st May 2021)" in {
      val userAnswers: UserAnswers = hasTheEmployerHadPreviousFurloughPeriodsMay2021

      setAnswers(userAnswers)

      val res = postRequestHeader("/furloughed-more-than-once", Json.toJson("value", ""))("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

      whenReady(res) { result =>
        result should have(
          httpStatus(BAD_REQUEST),
          contentExists(s"Select yes if this employee has been furloughed more than once since ${dateToString(may1st2021)}")
        )
      }

    }
  }
}
