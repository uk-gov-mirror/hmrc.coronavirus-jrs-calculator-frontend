package controllers

import assets.{BaseITConstants, PageTitles}
import models.{EmployeeStarted, UserAnswers}
import pages._
import play.api.http.Status._
import play.api.libs.json.Json
import utils.LocalDateHelpers._
import utils._
import views.ViewUtils.dateToString

import java.time.LocalDate

class HasEmployeeBeenOnStatutoryLeaveControllerISpec
    extends IntegrationSpecBase with CreateRequestHelper with CustomMatchers with BaseITConstants with ITCoreTestData {

  val dayEmploymentStarted = "the day their employment started"

  "GET /been-on-statutory-leave" should {

    "employee is type 3" when {

      "the day before employee is first furloughed is before 5th April 2020" must {

        val firstFurloughDate = LocalDate.parse("2020-04-05")
        val furloughStartDate = LocalDate.parse("2021-03-01")
        val boundaryStart     = dateToString(apr6th2019)
        val boundaryEnd       = dateToString(firstFurloughDate.minusDays(1))

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(FurloughStartDatePage, furloughStartDate)
          .success
          .value
          .set(EmployeeStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
          .success
          .value
          .set(FirstFurloughDatePage, firstFurloughDate)
          .success
          .value

        getTests(boundaryStart, boundaryEnd, userAnswers)
      }

      "day before employee is first furloughed is after 5th April 2020" must {

        val firstFurloughDate = LocalDate.parse("2020-04-06")
        val furloughStartDate = LocalDate.parse("2021-03-01")
        val boundaryStart     = dateToString(apr6th2019)
        val boundaryEnd       = dateToString(apr5th2020)

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(FurloughStartDatePage, furloughStartDate)
          .success
          .value
          .set(EmployeeStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
          .success
          .value
          .set(FirstFurloughDatePage, firstFurloughDate)
          .success
          .value

        getTests(boundaryStart, boundaryEnd, userAnswers)
      }
    }

    "employee is type 4" when {

      "the day before employee is first furloughed is before 5th April 2020" must {

        val employeeStartDate = feb1st2020.minusDays(1)
        val firstFurloughDate = LocalDate.parse("2020-04-05")
        val furloughStartDate = LocalDate.parse("2021-03-01")
        val boundaryStart     = dayEmploymentStarted
        val boundaryEnd       = dateToString(firstFurloughDate.minusDays(1))

        val userAnswers = emptyUserAnswers
          .set(FurloughStartDatePage, furloughStartDate)
          .success
          .value
          .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
          .success
          .value
          .set(EmployeeStartDatePage, employeeStartDate)
          .success
          .value
          .set(FirstFurloughDatePage, firstFurloughDate)
          .success
          .value

        getTests(boundaryStart, boundaryEnd, userAnswers)
      }

      "day before employee is first furloughed is after 5th April 2020" must {

        val employeeStartDate = feb1st2020.minusDays(1)
        val firstFurloughDate = LocalDate.parse("2020-04-06")
        val furloughStartDate = LocalDate.parse("2021-03-01")
        val boundaryStart     = dayEmploymentStarted
        val boundaryEnd       = dateToString(apr5th2020)

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(FurloughStartDatePage, furloughStartDate)
          .success
          .value
          .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
          .success
          .value
          .set(EmployeeStartDatePage, employeeStartDate)
          .success
          .value
          .set(FirstFurloughDatePage, firstFurloughDate)
          .success
          .value

        getTests(boundaryStart, boundaryEnd, userAnswers)
      }
    }

    "employee is type 5a" when {

      "employment started before 6 April 2020 and first furloughed 1 Jan 2021" must {

        val employeeStartDate = apr6th2020.minusDays(1)
        val firstFurloughDate = LocalDate.parse("2021-01-01")
        val furloughStartDate = LocalDate.parse("2021-03-01")
        val boundaryStart     = dateToString(apr6th2020)
        val boundaryEnd       = dateToString(firstFurloughDate.minusDays(1))

        val userAnswers = emptyUserAnswers
          .set(FurloughStartDatePage, furloughStartDate)
          .success
          .value
          .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
          .success
          .value
          .set(EmployeeStartDatePage, employeeStartDate)
          .success
          .value
          .set(OnPayrollBefore30thOct2020Page, true)
          .success
          .value
          .set(FirstFurloughDatePage, firstFurloughDate)
          .success
          .value

        getTests(boundaryStart, boundaryEnd, userAnswers)
      }

      "employment started after 6 April 2020 and first furloughed 1 Jan 2021" must {

        val employeeStartDate = apr6th2020.plusDays(1)
        val firstFurloughDate = LocalDate.parse("2021-01-01")
        val furloughStartDate = LocalDate.parse("2021-03-01")
        val boundaryStart     = dayEmploymentStarted
        val boundaryEnd       = dateToString(firstFurloughDate.minusDays(1))

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(FurloughStartDatePage, furloughStartDate)
          .success
          .value
          .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
          .success
          .value
          .set(EmployeeStartDatePage, employeeStartDate)
          .success
          .value
          .set(OnPayrollBefore30thOct2020Page, true)
          .success
          .value
          .set(FirstFurloughDatePage, firstFurloughDate)
          .success
          .value

        getTests(boundaryStart, boundaryEnd, userAnswers)
      }
    }

    "employee is type 5b" when {

      "employment started before 6 April 2020 and first furloughed 01 May 2021" must {

        val employeeStartDate = apr6th2020.minusDays(1)
        val firstFurloughDate = LocalDate.parse("2021-05-01")
        val furloughStartDate = LocalDate.parse("2021-05-21")
        val boundaryStart     = dateToString(apr6th2020)
        val boundaryEnd       = dateToString(firstFurloughDate.minusDays(1))

        val userAnswers = emptyUserAnswers
          .set(FurloughStartDatePage, furloughStartDate)
          .success
          .value
          .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
          .success
          .value
          .set(EmployeeStartDatePage, employeeStartDate)
          .success
          .value
          .set(OnPayrollBefore30thOct2020Page, false)
          .success
          .value
          .set(FirstFurloughDatePage, firstFurloughDate)
          .success
          .value

        getTests(boundaryStart, boundaryEnd, userAnswers)
      }

      "employment started after 6 April 2020 and first furloughed 01 May 2021" must {

        val employeeStartDate = apr6th2020.plusDays(1)
        val firstFurloughDate = LocalDate.parse("2021-05-01")
        val furloughStartDate = LocalDate.parse("2021-05-21")
        val boundaryStart     = dayEmploymentStarted
        val boundaryEnd       = dateToString(firstFurloughDate.minusDays(1))

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(FurloughStartDatePage, furloughStartDate)
          .success
          .value
          .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
          .success
          .value
          .set(EmployeeStartDatePage, employeeStartDate)
          .success
          .value
          .set(OnPayrollBefore30thOct2020Page, false)
          .success
          .value
          .set(FirstFurloughDatePage, firstFurloughDate)
          .success
          .value

        getTests(boundaryStart, boundaryEnd, userAnswers)
      }
    }
  }

  "POST /been-on-statutory-leave" when {

    "user answers true" should {

      "redirect to how many days on statutory leave page" in {

        val employeeStartDate = feb1st2020.minusDays(1)
        val firstFurloughDate = LocalDate.parse("2020-04-05")
        val furloughStartDate = LocalDate.parse("2021-03-01")

        val userAnswers = emptyUserAnswers
          .set(FurloughStartDatePage, furloughStartDate)
          .success
          .value
          .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
          .success
          .value
          .set(EmployeeStartDatePage, employeeStartDate)
          .success
          .value
          .set(FirstFurloughDatePage, firstFurloughDate)
          .success
          .value

        setAnswers(userAnswers)

        val res = postRequest("/been-on-statutory-leave",
          Json.obj("value" -> "true")
        )("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

        whenReady(res) { result =>
          result should have(
            httpStatus(SEE_OTHER),
            redirectLocation(controllers.routes.RootPageController.onPageLoad().url)
          )
        }
      }
    }

    "user answers false" should {

      "redirect to has this employee done any work page page" in {

        val employeeStartDate = feb1st2020.minusDays(1)
        val firstFurloughDate = LocalDate.parse("2020-04-05")
        val furloughStartDate = LocalDate.parse("2021-03-01")

        val userAnswers = emptyUserAnswers
          .set(FurloughStartDatePage, furloughStartDate)
          .success
          .value
          .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
          .success
          .value
          .set(EmployeeStartDatePage, employeeStartDate)
          .success
          .value
          .set(FirstFurloughDatePage, firstFurloughDate)
          .success
          .value

        setAnswers(userAnswers)

        val res = postRequest("/been-on-statutory-leave",
          Json.obj("value" -> "false")
        )("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

        whenReady(res) { result =>
          result should have(
            httpStatus(SEE_OTHER),
            redirectLocation(controllers.routes.RootPageController.onPageLoad().url)
          )
        }
      }
    }

    "user answer is invalid" should {

      "redirect to how many days on statutory leave page" in {

        val employeeStartDate = feb1st2020.minusDays(1)
        val firstFurloughDate = LocalDate.parse("2020-04-05")
        val furloughStartDate = LocalDate.parse("2021-03-01")

        val userAnswers = emptyUserAnswers
          .set(FurloughStartDatePage, furloughStartDate)
          .success
          .value
          .set(EmployeeStartedPage, EmployeeStarted.After1Feb2019)
          .success
          .value
          .set(EmployeeStartDatePage, employeeStartDate)
          .success
          .value
          .set(FirstFurloughDatePage, firstFurloughDate)
          .success
          .value

        setAnswers(userAnswers)

        val res = postRequest("/been-on-statutory-leave",
          Json.obj("value" -> "INVALID")
        )("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

        whenReady(res) { result =>
          result should have(
            httpStatus(BAD_REQUEST)
          )
        }
      }
    }
  }

  def getTests(boundaryStart: String, boundaryEnd: String, userAnswers: UserAnswers): Unit =
    "GET /been-on-statutory-leave" in {

      setAnswers(userAnswers)
      val res = getRequest("/been-on-statutory-leave")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

      whenReady(res) { result =>
        result should have(
          httpStatus(OK),
          titleOf(PageTitles.hasEmployeeBeenOnStatutoryLeave(boundaryStart, boundaryEnd))
        )
      }
    }
}
