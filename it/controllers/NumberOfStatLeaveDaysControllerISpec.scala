package controllers

import java.time._

import assets.{BaseITConstants, PageTitles}
import models.PayMethod.Variable
import models.PaymentFrequency.Monthly
import models._
import play.api.http.Status._
import play.api.libs.json.Json
import utils.LocalDateHelpers._
import utils.{CreateRequestHelper, CustomMatchers, ITCoreTestData, IntegrationSpecBase}
import views.ViewUtils.dateToString

class NumberOfStatLeaveDaysControllerISpec extends IntegrationSpecBase with CreateRequestHelper with CustomMatchers with BaseITConstants with ITCoreTestData {

  "GET /number-of-days-on-statutory-leave" when {

    "Employee Type 3" should {

      "render the first furlough date page with correct title" in {

        val furloughStartDate: String = "2021, 03, 01"
        val boundaryStart: String = dateToString(apr6th2019)
        val boundaryEnd: String = dateToString(apr5th2020)

        val userAnswers: UserAnswers =
          emptyUserAnswers
            .withClaimPeriodStart("2020, 11, 1")
            .withClaimPeriodEnd("2020, 11, 30")
            .withFurloughStartDate(furloughStartDate)
            .withFurloughStatus()
            .withPaymentFrequency(Monthly)
            .withPayMethod(Variable)
            .withFurloughInLastTaxYear(false)
            .withVariableLengthEmployed(EmployeeStarted.OnOrBefore1Feb2019)


        setAnswers(userAnswers)

        val res = getRequestHeaders("/number-of-days-on-statutory-leave")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

        whenReady(res) { result =>
          result should have(
            httpStatus(OK),
            titleOf(PageTitles.numberOfStatLeaveDays(Some(boundaryStart), boundaryEnd))
          )
        }
      }
    }

    "Employee Type 4" should {

      "render the first furlough date page with correct wordy title" in {

        val employeeStartDate = "2020, 01, 31"
        val furloughStartDate: String = "2021, 03, 01"
        val firstFurloughDate: String = "2020, 04, 05"
        val boundaryEnd: String = dateToString(LocalDate.of(2020, 4, 4))

        val userAnswers: UserAnswers =
          emptyUserAnswers
            .withClaimPeriodStart("2020, 11, 1")
            .withClaimPeriodEnd("2020, 11, 30")
            .withFurloughStartDate(furloughStartDate)
            .withFurloughStatus()
            .withPaymentFrequency(Monthly)
            .withPayMethod(Variable)
            .withFurloughInLastTaxYear(false)
            .withVariableLengthEmployed(EmployeeStarted.After1Feb2019)
            .withEmployeeStartDate(employeeStartDate)
            .withFirstFurloughDate(firstFurloughDate)

        setAnswers(userAnswers)

        val res = getRequestHeaders("/number-of-days-on-statutory-leave")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

        whenReady(res) { result =>
          result should have(
            httpStatus(OK),
            titleOf(PageTitles.numberOfStatLeaveDays(None, boundaryEnd))
          )
        }
      }
    }

    "Employee Type 5a" should {

      "render the first furlough date page with correct wordy title" in {

        val employeeStartDate = "2020, 04, 10"
        val furloughStartDate: String = "2020, 11, 01"
        val firstFurloughDate: String = "2020, 01, 01"
        val boundaryEnd = dateToString(LocalDate.of(2019, 12, 31))

        val userAnswers: UserAnswers =
          emptyUserAnswers
            .withClaimPeriodStart("2020, 11, 1")
            .withClaimPeriodEnd("2020, 11, 30")
            .withFurloughStartDate(furloughStartDate)
            .withFurloughStatus()
            .withPaymentFrequency(Monthly)
            .withPayMethod(Variable)
            .withFurloughInLastTaxYear(false)
            .withVariableLengthEmployed(EmployeeStarted.After1Feb2019)
            .withEmployeeStartDate(employeeStartDate)
            .withFirstFurloughDate(firstFurloughDate)
            .withOnPayrollBefore30thOct2020(true)

        setAnswers(userAnswers)

        val res = getRequestHeaders("/number-of-days-on-statutory-leave")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

        whenReady(res) { result =>
          result should have(
            httpStatus(OK),
            titleOf(PageTitles.numberOfStatLeaveDays(None, boundaryEnd))
          )
        }
      }
    }

    "Employee Type 5b" should {

      "render the first furlough date page with correct wordy title" in {

        val employeeStartDate = "2020, 04, 10"
        val furloughStartDate: String = "2021, 05, 01"
        val firstFurloughDate: String = "2020, 05, 21"
        val boundaryEnd = dateToString(LocalDate.of(2020, 5, 20))

        val userAnswers: UserAnswers =
          emptyUserAnswers
            .withClaimPeriodStart("2021, 5, 1")
            .withClaimPeriodEnd("2021, 5, 31")
            .withFurloughStartDate(furloughStartDate)
            .withFurloughStatus()
            .withPaymentFrequency(Monthly)
            .withPayMethod(Variable)
            .withFurloughInLastTaxYear(false)
            .withVariableLengthEmployed(EmployeeStarted.After1Feb2019)
            .withEmployeeStartDate(employeeStartDate)
            .withFirstFurloughDate(firstFurloughDate)
            .withOnPayrollBefore30thOct2020(false)

        setAnswers(userAnswers)

        val res = getRequestHeaders("/number-of-days-on-statutory-leave")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

        whenReady(res) { result =>
          result should have(
            httpStatus(OK),
            titleOf(PageTitles.numberOfStatLeaveDays(None, boundaryEnd))
          )
        }
      }
    }
  }

  "POST /number-of-days-on-statutory-leave" when {

    "Employee Type 3" should {

      "valid values supplied" must {

        "redirect to onward route" in {

          val furloughStartDate: String = "2021, 03, 01"

          val userAnswers: UserAnswers =
            emptyUserAnswers
              .withClaimPeriodStart("2021, 3, 1")
              .withClaimPeriodEnd("2021, 3, 30")
              .withFurloughStartDate(furloughStartDate)
              .withFurloughStatus()
              .withPaymentFrequency(Monthly)
              .withPayMethod(Variable)
              .withFurloughInLastTaxYear(false)
              .withVariableLengthEmployed(EmployeeStarted.OnOrBefore1Feb2019)

          setAnswers(userAnswers)

          val res = postRequestHeader(
            path = "/number-of-days-on-statutory-leave",
            formJson = Json.obj("value" -> "10")
          )("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

          whenReady(res) { result =>
            result should have(
              httpStatus(SEE_OTHER),
              redirectLocation(controllers.routes.StatutoryLeavePayController.onPageLoad().url)
            )
          }
        }
      }
    }

    "Employee Type 4" should {

      "valid values supplied" must {

        "redirect to onward route" in {

          val employeeStartDate = "2020, 01, 31"
          val furloughStartDate: String = "2021, 03, 01"
          val firstFurloughDate: String = "2020, 04, 05"

          val userAnswers: UserAnswers =
            emptyUserAnswers
              .withClaimPeriodStart("2021, 3, 1")
              .withClaimPeriodEnd("2021, 3, 30")
              .withFurloughStartDate(furloughStartDate)
              .withFurloughStatus()
              .withPaymentFrequency(Monthly)
              .withPayMethod(Variable)
              .withFurloughInLastTaxYear(false)
              .withVariableLengthEmployed(EmployeeStarted.After1Feb2019)
              .withEmployeeStartDate(employeeStartDate)
              .withFirstFurloughDate(firstFurloughDate)
              .withFirstFurloughDate(firstFurloughDate)

          setAnswers(userAnswers)

          val res = postRequestHeader(
            path = "/number-of-days-on-statutory-leave",
            formJson = Json.obj("value" -> "10")
          )("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

          whenReady(res) { result =>
            result should have(
              httpStatus(SEE_OTHER),
              redirectLocation(controllers.routes.RootPageController.onPageLoad().url) //TODO: Update when routing is in
            )
          }
        }
      }
    }

    "Employee Type 5a" should {

      "valid values supplied" must {

        "redirect to onward route" in {

          val employeeStartDate = "2020, 04, 10"
          val furloughStartDate: String = "2020, 11, 01"

          val userAnswers: UserAnswers =
            emptyUserAnswers
              .withClaimPeriodStart("2020, 11, 1")
              .withClaimPeriodEnd("2020, 11, 30")
              .withFurloughStartDate(furloughStartDate)
              .withFurloughStatus()
              .withPaymentFrequency(Monthly)
              .withPayMethod(Variable)
              .withFurloughInLastTaxYear(false)
              .withVariableLengthEmployed(EmployeeStarted.After1Feb2019)
              .withEmployeeStartDate(employeeStartDate)
              .withOnPayrollBefore30thOct2020(true)

          setAnswers(userAnswers)

          val res = postRequestHeader(
            path = "/number-of-days-on-statutory-leave",
            formJson = Json.obj("value" -> "1")
          )("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

          whenReady(res) { result =>
            result should have(
              httpStatus(SEE_OTHER),
              redirectLocation(controllers.routes.RootPageController.onPageLoad().url) //TODO: Update when routing is in
            )
          }
        }
      }
    }

    "Employee Type 5b" should {

      "valid values supplied" must {

        "redirect to onward route" in {

          val employeeStartDate = "2020, 04, 10"
          val furloughStartDate: String = "2021, 05, 01"
          val firstFurloughDate: String = "2020, 05, 21"

          val userAnswers: UserAnswers =
            emptyUserAnswers
              .withClaimPeriodStart("2021, 5, 1")
              .withClaimPeriodEnd("2021, 5, 31")
              .withFurloughStartDate(furloughStartDate)
              .withFurloughStatus()
              .withPaymentFrequency(Monthly)
              .withPayMethod(Variable)
              .withFurloughInLastTaxYear(false)
              .withVariableLengthEmployed(EmployeeStarted.After1Feb2019)
              .withEmployeeStartDate(employeeStartDate)
              .withFirstFurloughDate(firstFurloughDate)
              .withOnPayrollBefore30thOct2020(false)

          setAnswers(userAnswers)

          val res = postRequestHeader(
            path = "/number-of-days-on-statutory-leave",
            formJson = Json.obj("value" -> "10")
          )("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

          whenReady(res) { result =>
            result should have(
              httpStatus(SEE_OTHER),
              redirectLocation(controllers.routes.StatutoryLeavePayController.onPageLoad().url)
            )
          }
        }
      }
    }

    "redirect to another page" when {

      "the user has not answered the questions" in {

        val userAnswers: UserAnswers = emptyUserAnswers

        setAnswers(userAnswers)

        val res = getRequest("/number-of-days-on-statutory-leave")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

        //TODO Should redirect to reset or start again page
        whenReady(res) { result =>
          result should have(
            httpStatus(INTERNAL_SERVER_ERROR)
          )
        }
      }
    }
  }

}

