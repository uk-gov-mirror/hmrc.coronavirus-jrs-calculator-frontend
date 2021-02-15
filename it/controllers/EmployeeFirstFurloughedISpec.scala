package controllers

import assets.BaseITConstants
import assets.PageTitles.{claimPeriodStartDate, employeeFirstFurloughed}
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.libs.json.Json
import utils.{CreateRequestHelper, CustomMatchers, IntegrationSpecBase}

class EmployeeFirstFurloughedISpec extends IntegrationSpecBase with CreateRequestHelper with CustomMatchers with BaseITConstants {

  "GET /first-furlough-date" when {

    "redirect to the start page" in {

      val res = getRequest("/first-furlough-date")()

      whenReady(res) { result =>
        result should have(
          httpStatus(OK),
          titleOf(employeeFirstFurloughed)
        )
      }
    }
  }
  //TODO Will need to implement once page has been wired up to the navigator
//  "POST /employeeFirstFurloughed" when {
//
//    "enters a valid answer" when {
//
//      "redirect to EmployeeFirstFurloughed" in {
//
//
//        val res = postRequest("/first-furlough-date",
//          Json.obj(
//            "value.day" -> claimStartDate.getDayOfMonth,
//            "value.month" -> claimStartDate.getMonthValue,
//            "value.year" -> claimStartDate.getYear
//          ))()
//
//
//        whenReady(res) { result =>
//          result should have(
//            httpStatus(SEE_OTHER),
//            redirectLocation(controllers.routes.EmployeeFirstFurloughedController.onPageLoad().url)
//          )
//        }
//      }
//    }
//  }
}
