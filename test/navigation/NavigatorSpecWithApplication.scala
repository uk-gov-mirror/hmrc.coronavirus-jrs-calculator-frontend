/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package navigation

import java.time.LocalDate

import base.SpecBaseWithApplication
import controllers.routes
import models.PayMethod.{Regular, Variable}
import models._
import pages._
import play.api.libs.json.Json

class NavigatorSpecWithApplication extends SpecBaseWithApplication {

  val navigator = new Navigator(frontendAppConfig)

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from a page that doesn't exist in the route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, UserAnswers("id")) mustBe routes.RootPageController.onPageLoad()
      }

      "go to ClaimPeriodEndPage after ClaimPeriodStartPage" in {
        navigator.nextPage(ClaimPeriodStartPage, UserAnswers("id")) mustBe routes.ClaimPeriodEndController
          .onPageLoad()
      }

      "go to furloughOngoingPage after ClaimPeriodEndPage" in {
        navigator.nextPage(ClaimPeriodEndPage, UserAnswers("id")) mustBe routes.FurloughStartDateController
          .onPageLoad()
      }

      "go to correct page after furloughOngoingPage" in {
        navigator.nextPage(
          FurloughStatusPage,
          UserAnswers("id")
            .set(FurloughStatusPage, FurloughStatus.FurloughOngoing)
            .success
            .value) mustBe routes.PaymentFrequencyController.onPageLoad()
        navigator.nextPage(
          FurloughStatusPage,
          UserAnswers("id")
            .set(FurloughStatusPage, FurloughStatus.FurloughEnded)
            .success
            .value) mustBe routes.FurloughEndDateController.onPageLoad()
      }

      "go to PaymentFrequencyPage after FurloughEndDatePage" in {
        navigator.nextPage(FurloughEndDatePage, UserAnswers("id")) mustBe routes.PaymentFrequencyController
          .onPageLoad()
      }

      "go to correct page after PayMethodPage" in {
        navigator.nextPage(
          PayMethodPage,
          UserAnswers("id")
            .set(PayMethodPage, PayMethod.Regular)
            .success
            .value) mustBe routes.SalaryQuestionController.onPageLoad()

        navigator.nextPage(
          PayMethodPage,
          UserAnswers("id")
            .set(PayMethodPage, PayMethod.Variable)
            .success
            .value) mustBe routes.VariableLengthEmployedController.onPageLoad()

        navigator.nextPage(PayMethodPage, UserAnswers("id")) mustBe routes.PayMethodController.onPageLoad()
      }

      "go to SalaryQuestionPage after PaymentQuestionPage" in {
        navigator.nextPage(PaymentFrequencyPage, UserAnswers("id")) mustBe routes.PayMethodController
          .onPageLoad()
      }

      "go to PayDatePage after SalaryQuestionPage" in {
        navigator.nextPage(SalaryQuestionPage, UserAnswers("id")) mustBe routes.PayDateController
          .onPageLoad(1)
      }

      "loop around pay date if last pay date isn't claim end date or after" in {
        val userAnswers = UserAnswers("id")
          .set(ClaimPeriodEndPage, LocalDate.of(2020, 5, 30))
          .get
          .set(PayDatePage, LocalDate.of(2020, 5, 29), Some(1))
          .get

        navigator.nextPage(PayDatePage, userAnswers, Some(1)) mustBe routes.PayDateController.onPageLoad(2)
      }

      "stop loop around pay date if last pay date is claim end date" in {
        val userAnswers = UserAnswers("id")
          .set(ClaimPeriodEndPage, LocalDate.of(2020, 5, 30))
          .get
          .set(PayDatePage, LocalDate.of(2020, 5, 30), Some(1))
          .get

        navigator.nextPage(PayDatePage, userAnswers, Some(1)) mustBe routes.LastPayDateController
          .onPageLoad()
      }

      "stop loop around pay date if last pay date is after claim end date" in {
        val userAnswers = UserAnswers("id")
          .set(ClaimPeriodEndPage, LocalDate.of(2020, 5, 30))
          .get
          .set(PayDatePage, LocalDate.of(2020, 5, 31), Some(1))
          .get

        navigator.nextPage(PayDatePage, userAnswers, Some(1)) mustBe routes.LastPayDateController
          .onPageLoad()
      }

      "go to NicCategoryPage after LastPayDatePage if the pay-method is Regular" in {
        val userAnswers = UserAnswers("id")
          .set(PayMethodPage, Regular)
          .get

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.NicCategoryController.onPageLoad()
      }

      "go to NicCategoryPage after LastPayDatePage if the pay-method is Variable and employee has been employed over 12 months" in {
        val userAnswers = UserAnswers("id")
          .set(PayMethodPage, Variable)
          .get
          .set(FurloughStartDatePage, LocalDate.of(2020, 3, 2))
          .get
          .set(PayDatePage, LocalDate.of(2020, 3, 1), Some(1))
          .get
          .set(PayDatePage, LocalDate.of(2020, 4, 10), Some(2))
          .get
          .set(ClaimPeriodEndPage, LocalDate.of(2020, 5, 10))
          .get
          .set(EmployedStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
          .get

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.LastYearPayController.onPageLoad(1)
      }

      "go to NicCategoryPage after LastPayDatePage if the pay-method is Variable and employee start after 5th April 2019" in {
        val userAnswers = UserAnswers("id")
          .set(PayMethodPage, Variable)
          .get
          .set(FurloughStartDatePage, LocalDate.of(2020, 3, 2))
          .get
          .set(PayDatePage, LocalDate.of(2020, 3, 1), Some(1))
          .get
          .set(PayDatePage, LocalDate.of(2020, 4, 10), Some(2))
          .get
          .set(ClaimPeriodEndPage, LocalDate.of(2020, 5, 10))
          .get
          .set(EmployeeStartDatePage, LocalDate.of(2019, 4, 6))
          .get

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.NicCategoryController.onPageLoad()
      }

      "go to LastYearPay after LastPayDatePage if the pay-method is Variable and employee start before 6th April 2019" in {
        val userAnswers = UserAnswers("id")
          .set(PayMethodPage, Variable)
          .get
          .set(FurloughStartDatePage, LocalDate.of(2020, 3, 2))
          .get
          .set(PayDatePage, LocalDate.of(2020, 3, 1), Some(1))
          .get
          .set(PayDatePage, LocalDate.of(2020, 4, 10), Some(2))
          .get
          .set(ClaimPeriodEndPage, LocalDate.of(2020, 5, 10))
          .get
          .set(EmployeeStartDatePage, LocalDate.of(2019, 4, 5))
          .get

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.LastYearPayController.onPageLoad(1)
      }

      "go to PartialPayBeforeFurloughPage after LastPayDatePage if the pay-method is Variable and first pay period is partial" in {
        val userAnswers = UserAnswers("id")
          .set(PayMethodPage, Variable)
          .get
          .set(FurloughStartDatePage, LocalDate.of(2020, 3, 15))
          .get
          .set(PayDatePage, LocalDate.of(2020, 3, 10), Some(1))
          .get
          .set(PayDatePage, LocalDate.of(2020, 4, 10), Some(2))
          .get
          .set(PayDatePage, LocalDate.of(2020, 5, 10), Some(3))
          .get
          .set(PayDatePage, LocalDate.of(2020, 6, 10), Some(4))
          .get
          .set(ClaimPeriodStartPage, LocalDate.of(2020, 3, 15))
          .get
          .set(ClaimPeriodEndPage, LocalDate.of(2020, 5, 15))
          .get

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.PartialPayBeforeFurloughController.onPageLoad()
      }

      "go to PartialPayAfterFurloughPage after LastPayDatePage if the pay-method is Variable and last pay period is partial" in {
        val userAnswers = UserAnswers("id")
          .set(PayMethodPage, Variable)
          .get
          .set(FurloughStartDatePage, LocalDate.of(2020, 3, 10))
          .get
          .set(PayDatePage, LocalDate.of(2020, 3, 9), Some(1))
          .get
          .set(PayDatePage, LocalDate.of(2020, 4, 10), Some(2))
          .get
          .set(PayDatePage, LocalDate.of(2020, 5, 10), Some(3))
          .get
          .set(PayDatePage, LocalDate.of(2020, 6, 10), Some(4))
          .get
          .set(ClaimPeriodStartPage, LocalDate.of(2020, 3, 10))
          .get
          .set(ClaimPeriodEndPage, LocalDate.of(2020, 5, 15))
          .get

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.PartialPayAfterFurloughController.onPageLoad()
      }

      "go to payMethodPage after LastPayDatePage if the pay-method missing in UserAnswers" in {
        val userAnswers = UserAnswers("id")

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.PayMethodController.onPageLoad()
      }

      "go from PensionAutoEnrolmentPage to FurloughCalculationsPage" in {
        navigator.nextPage(PensionContributionPage, emptyUserAnswers) mustBe routes.FurloughCalculationsController.onPageLoad()
      }

      "go from furlough start date to furlough question" in {
        val answers = emptyUserAnswers
        navigator.nextPage(FurloughStartDatePage, answers) mustBe routes.FurloughOngoingController.onPageLoad()
      }

      "go from furlough end date" must {

        "to pay question" in {
          navigator.nextPage(FurloughEndDatePage, emptyUserAnswers) mustBe routes.PaymentFrequencyController
            .onPageLoad()
        }
      }

      "go to correct page after EmployedStartedPage" in {
        navigator.nextPage(
          EmployedStartedPage,
          UserAnswers("id")
            .set(EmployedStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
            .success
            .value) mustBe routes.VariableGrossPayController.onPageLoad()
        navigator.nextPage(
          EmployedStartedPage,
          UserAnswers("id")
            .set(EmployedStartedPage, EmployeeStarted.After1Feb2019)
            .success
            .value) mustBe routes.EmployeeStartDateController.onPageLoad()
      }

      "go to correct page after EmployeeStartDatePage" in {
        navigator.nextPage(
          EmployeeStartDatePage,
          UserAnswers("id")
            .set(EmployeeStartDatePage, LocalDate.now().minusDays(2))
            .success
            .value
        ) mustBe routes.VariableGrossPayController.onPageLoad()
        navigator.nextPage(
          EmployeeStartDatePage,
          UserAnswers("id")
            .set(EmployeeStartDatePage, LocalDate.of(2019, 4, 5))
            .success
            .value
        ) mustBe routes.VariableGrossPayController.onPageLoad()
      }

      "go to correct page after FurloughCalculationsPage" in {
        navigator.nextPage(
          FurloughCalculationsPage,
          UserAnswers("id")
            .set(FurloughCalculationsPage, FurloughCalculations.Yes)
            .success
            .value
        ) mustBe routes.ComingSoonController.onPageLoad(true)
        navigator.nextPage(
          FurloughCalculationsPage,
          UserAnswers("id")
            .set(FurloughCalculationsPage, FurloughCalculations.No)
            .success
            .value
        ) mustBe routes.ConfirmationController.onPageLoad()
      }

      "go to start of pay date loop after variable gross pay page" in {
        navigator.nextPage(
          VariableGrossPayPage,
          emptyUserAnswers
        ) mustBe routes.PayDateController.onPageLoad(1)
      }

      "loop around last year pay if there are more years to ask" in {
        val userAnswers = Json.parse(variableMonthlyPartial).as[UserAnswers]

        navigator.nextPage(LastYearPayPage, userAnswers, Some(1)) mustBe routes.LastYearPayController.onPageLoad(2)
      }

      "stop loop around last year pay if there are no more years to ask" in {
        val userAnswers = Json.parse(variableMonthlyPartial).as[UserAnswers]

        navigator.nextPage(LastYearPayPage, userAnswers, Some(2)) mustBe routes.NicCategoryController.onPageLoad()
      }

      "go to correct page after PartialPayAfterFurloughPage" when {

        "EmployedStarted is OnOrBefore1Feb2019" in {
          val userAnswers = emptyUserAnswers.set(EmployedStartedPage, EmployeeStarted.OnOrBefore1Feb2019).success.value

          navigator.nextPage(
            PartialPayAfterFurloughPage,
            userAnswers
          ) mustBe routes.LastYearPayController.onPageLoad(1)
        }

        "EmployedStarted is After1Feb2019 and date is before April 6th" in {
          val userAnswers = emptyUserAnswers
            .set(EmployedStartedPage, EmployeeStarted.After1Feb2019)
            .success
            .value
            .set(EmployeeStartDatePage, LocalDate.of(2019, 4, 5))
            .success
            .value

          navigator.nextPage(
            PartialPayAfterFurloughPage,
            userAnswers
          ) mustBe routes.LastYearPayController.onPageLoad(1)
        }

        "EmployedStarted is After1Feb2019 and date is April 7th" in {
          val userAnswers = emptyUserAnswers
            .set(EmployedStartedPage, EmployeeStarted.After1Feb2019)
            .success
            .value
            .set(EmployeeStartDatePage, LocalDate.of(2020, 4, 7))
            .success
            .value

          navigator.nextPage(
            PartialPayAfterFurloughPage,
            userAnswers
          ) mustBe routes.NicCategoryController.onPageLoad()
        }

        "EmployeeStarted is After1Feb2019 and date is after April 7th" in {
          val userAnswers = emptyUserAnswers
            .set(EmployedStartedPage, EmployeeStarted.After1Feb2019)
            .success
            .value
            .set(EmployeeStartDatePage, LocalDate.of(2020, 4, 8))
            .success
            .value

          navigator.nextPage(
            PartialPayAfterFurloughPage,
            userAnswers
          ) mustBe routes.NicCategoryController.onPageLoad()
        }

        "EmployeeStarted is missing" in {
          val userAnswers = emptyUserAnswers

          navigator.nextPage(
            PartialPayAfterFurloughPage,
            userAnswers
          ) mustBe routes.NicCategoryController.onPageLoad()
        }

      }
    }

    "routeFor() is called" should {
      "return correct url for FurloughStartDatePage" in {
        navigator.routeFor(FurloughStartDatePage) mustBe routes.FurloughStartDateController.onPageLoad()
      }
      "return internalServerError url for any page other than FurloughStartDatePage" in {
        case object UnknownPage extends Page
        navigator.routeFor(UnknownPage) mustBe routes.ErrorController.internalServerError()
      }
    }
  }
}
