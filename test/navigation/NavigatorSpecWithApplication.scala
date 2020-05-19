/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package navigation

import java.time.LocalDate

import base.{CoreTestDataBuilder, SpecBaseWithApplication}
import config.FrontendAppConfig
import controllers.routes
import models.PayMethod.{Regular, Variable}
import models._
import pages._

class NavigatorSpecWithApplication extends SpecBaseWithApplication with CoreTestDataBuilder {

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
            .value) mustBe routes.PayDateController.onPageLoad(1)

        navigator.nextPage(
          PayMethodPage,
          UserAnswers("id")
            .set(PayMethodPage, PayMethod.Variable)
            .success
            .value) mustBe routes.VariableLengthEmployedController.onPageLoad()

        navigator.nextPage(PayMethodPage, UserAnswers("id")) mustBe routes.PayMethodController.onPageLoad()
      }

      "go to RegularPayAmountPage after PaymentQuestionPage" in {
        navigator.nextPage(PaymentFrequencyPage, UserAnswers("id")) mustBe routes.PayMethodController
          .onPageLoad()
      }

      "go to TopUpStatusPage after RegularPayAmountPage" in {
        navigator.nextPage(RegularPayAmountPage, UserAnswers("id")) mustBe routes.TopUpStatusController.onPageLoad()
      }

      "go to NicCategoryPage after RegularPayAmountPage when the topUp feature is disabled" in {
        val application =
          applicationBuilder(None, Map("topup.journey.enabled" -> false))
            .build()
        val newNavigator = new Navigator(application.injector.instanceOf[FrontendAppConfig])
        newNavigator.nextPage(RegularPayAmountPage, UserAnswers("id")) mustBe routes.NicCategoryController.onPageLoad()
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

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.RegularPayAmountController.onPageLoad()
      }

      "go to LastYearPayPage after LastPayDatePage if the pay-method is Variable and EmployeeStarted.OnOrBefore1Feb2019" in {
        val userAnswers = UserAnswers("id")
          .set(PayMethodPage, Variable)
          .get
          .set(EmployeeStartDatePage, LocalDate.of(2019, 1, 2))
          .get

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.LastYearPayController.onPageLoad(1)
      }

      "go to LastYearPayPage after LastPayDatePage if the pay-method is Variable and employee started before apr6th2019" in {
        val userAnswers = UserAnswers("id")
          .set(PayMethodPage, Variable)
          .get
          .set(EmployeeStartDatePage, LocalDate.of(2019, 3, 4))
          .get

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.LastYearPayController.onPageLoad(1)
      }

      "go to AnnualPayAmountPage after LastPayDatePage if the pay-method is Variable and EmployeeStarted on or after Apr6th" in {
        val userAnswers = UserAnswers("id")
          .set(PayMethodPage, Variable)
          .get
          .set(EmployeeStartDatePage, LocalDate.of(2019, 6, 1))
          .get

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.AnnualPayAmountController.onPageLoad()
      }

      "go to payMethodPage after LastPayDatePage if the pay-method missing in UserAnswers" in {
        val userAnswers = UserAnswers("id")

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.PayMethodController.onPageLoad()
      }

      "go from PensionStatusPage to ConfirmationPage" in {
        navigator.nextPage(PensionStatusPage, emptyUserAnswers) mustBe routes.ConfirmationController.onPageLoad()
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
            .value) mustBe routes.PayDateController.onPageLoad(1)
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
        ) mustBe routes.PayDateController.onPageLoad(1)

        navigator.nextPage(
          EmployeeStartDatePage,
          UserAnswers("id")
            .set(EmployeeStartDatePage, LocalDate.of(2019, 4, 5))
            .success
            .value
        ) mustBe routes.PayDateController.onPageLoad(1)
      }

      "go to PartialPayBeforeFurloughPage loop after variable gross pay page" in {
        val userAnswers = UserAnswers("id")
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

        navigator.nextPage(
          AnnualPayAmountPage,
          userAnswers
        ) mustBe routes.PartialPayBeforeFurloughController.onPageLoad()
      }

      "go to PartialPayAfterFurloughPage loop after variable gross pay page" in {
        val userAnswers = UserAnswers("id")
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

        navigator.nextPage(
          AnnualPayAmountPage,
          userAnswers
        ) mustBe routes.PartialPayAfterFurloughController.onPageLoad()
      }

      "go to TopUpStatusPage after variable gross pay page if there are no partial furloughs" in {
        val userAnswers = UserAnswers("id")
          .set(FurloughStartDatePage, LocalDate.of(2020, 3, 1))
          .get
          .set(FurloughEndDatePage, LocalDate.of(2020, 4, 10))
          .get
          .set(PayDatePage, LocalDate.of(2020, 3, 1), Some(1))
          .get
          .set(PayDatePage, LocalDate.of(2020, 4, 10), Some(2))
          .get

        navigator.nextPage(
          AnnualPayAmountPage,
          userAnswers
        ) mustBe routes.TopUpStatusController.onPageLoad()
      }

      "go to NicCategoryPage after variable gross pay page if there are no partial furloughs when the topUp feature is disabled" in {
        val userAnswers = UserAnswers("id")
          .set(FurloughStartDatePage, LocalDate.of(2020, 3, 1))
          .get
          .set(FurloughEndDatePage, LocalDate.of(2020, 4, 10))
          .get
          .set(PayDatePage, LocalDate.of(2020, 3, 1), Some(1))
          .get
          .set(PayDatePage, LocalDate.of(2020, 4, 10), Some(2))
          .get

        val application =
          applicationBuilder(Some(userAnswers), Map("topup.journey.enabled" -> false))
            .build()
        val newNavigator = new Navigator(application.injector.instanceOf[FrontendAppConfig])
        newNavigator.nextPage(AnnualPayAmountPage, UserAnswers("id")) mustBe routes.NicCategoryController.onPageLoad()

      }

      "loop around last year pay if there are more years to ask" in {
        val userAnswers = variableMonthlyPartial

        navigator.nextPage(LastYearPayPage, userAnswers, Some(1)) mustBe routes.LastYearPayController.onPageLoad(2)
      }

      "stop loop around last year pay if there are no more years to ask" in {
        val userAnswers = variableMonthlyPartial

        navigator.nextPage(LastYearPayPage, userAnswers, Some(2)) mustBe routes.AnnualPayAmountController.onPageLoad()
      }

      "go to start of top up loop after top up periods page" in {
        val topUpPeriods = List(
          TopUpPeriod(LocalDate.of(2020, 3, 15), Amount(100.00)),
          TopUpPeriod(LocalDate.of(2020, 4, 15), Amount(150.00))
        )

        val userAnswers = mandatoryAnswersOnRegularMonthly.withTopUpPeriods(topUpPeriods)

        navigator.nextPage(
          TopUpPeriodsPage,
          userAnswers
        ) mustBe routes.TopUpAmountController.onPageLoad(1)
      }

      "loop around top up amounts if there are more periods to ask" in {
        val topUpPeriods = List(
          TopUpPeriod(LocalDate.of(2020, 3, 15), Amount(100.00)),
          TopUpPeriod(LocalDate.of(2020, 4, 15), Amount(150.00))
        )

        val userAnswers = mandatoryAnswersOnRegularMonthly.withTopUpPeriods(topUpPeriods)

        navigator.nextPage(
          TopUpAmountPage,
          userAnswers,
          Some(1)
        ) mustBe routes.TopUpAmountController.onPageLoad(2)
      }

      "stop loop around top up amounts if there are no more periods to ask" in {
        val topUpPeriods = List(
          TopUpPeriod(LocalDate.of(2020, 3, 15), Amount(100.00)),
          TopUpPeriod(LocalDate.of(2020, 4, 15), Amount(150.00))
        )

        val userAnswers = mandatoryAnswersOnRegularMonthly.withTopUpPeriods(topUpPeriods)

        navigator.nextPage(
          TopUpAmountPage,
          userAnswers,
          Some(2)
        ) mustBe routes.AdditionalPaymentStatusController.onPageLoad()
      }

      "go to start of additional payment loop after additional payment periods page" in {
        val additionalPaymentDates = List(
          LocalDate.of(2020, 3, 15),
          LocalDate.of(2020, 4, 15)
        )

        val userAnswers = mandatoryAnswersOnRegularMonthly.withAdditionalPaymentPeriods(additionalPaymentDates.map(_.toString))

        navigator.nextPage(
          AdditionalPaymentPeriodsPage,
          userAnswers
        ) mustBe routes.AdditionalPaymentAmountController.onPageLoad(1)
      }

      "loop around additional payment amounts if there are more periods to ask" in {
        val additionalPaymentDates = List(
          LocalDate.of(2020, 3, 15),
          LocalDate.of(2020, 4, 15)
        )

        val userAnswers = mandatoryAnswersOnRegularMonthly.withAdditionalPaymentPeriods(additionalPaymentDates.map(_.toString))

        navigator.nextPage(
          AdditionalPaymentAmountPage,
          userAnswers,
          Some(1)
        ) mustBe routes.AdditionalPaymentAmountController.onPageLoad(2)
      }

      "stop loop around additional payment amounts if there are no more periods to ask" in {
        val additionalPaymentDates = List(
          LocalDate.of(2020, 3, 15),
          LocalDate.of(2020, 4, 15)
        )

        val userAnswers = mandatoryAnswersOnRegularMonthly.withAdditionalPaymentPeriods(additionalPaymentDates.map(_.toString))

        navigator.nextPage(
          AdditionalPaymentAmountPage,
          userAnswers,
          Some(2)
        ) mustBe routes.NicCategoryController.onPageLoad()
      }

      "go to correct page after AdditionalPaymentStatusPage" in {
        navigator.nextPage(
          AdditionalPaymentStatusPage,
          UserAnswers("id")
            .set(AdditionalPaymentStatusPage, AdditionalPaymentStatus.YesAdditionalPayments)
            .success
            .value
        ) mustBe routes.AdditionalPaymentPeriodsController.onPageLoad()

        navigator.nextPage(
          AdditionalPaymentStatusPage,
          UserAnswers("id")
            .set(AdditionalPaymentStatusPage, AdditionalPaymentStatus.NoAdditionalPayments)
            .success
            .value
        ) mustBe routes.NicCategoryController.onPageLoad()
      }

      "go to TopUpStatusPage after PartialPayAfterFurloughPage" in {
        navigator.nextPage(
          PartialPayAfterFurloughPage,
          emptyUserAnswers
        ) mustBe routes.TopUpStatusController.onPageLoad()
      }

      "go to NicCategoryPage after PartialPayAfterFurloughPage when the topUp feature is disabled" in {
        val application =
          applicationBuilder(None, Map("topup.journey.enabled" -> false))
            .build()
        val newNavigator = new Navigator(application.injector.instanceOf[FrontendAppConfig])
        newNavigator.nextPage(PartialPayAfterFurloughPage, UserAnswers("id")) mustBe routes.NicCategoryController.onPageLoad()
      }

      "go to correct page after FurloughPeriodQuestionPage" in {
        navigator.nextPage(
          FurloughPeriodQuestionPage,
          UserAnswers("id")
            .set(FurloughPeriodQuestionPage, FurloughPeriodQuestion.FurloughedOnSamePeriod)
            .success
            .value
        ) mustBe routes.RootPageController.onPageLoad()

        navigator.nextPage(
          FurloughPeriodQuestionPage,
          UserAnswers("id")
            .set(FurloughPeriodQuestionPage, FurloughPeriodQuestion.FurloughedOnDifferentPeriod)
            .success
            .value
        ) mustBe routes.FurloughStartDateController.onPageLoad()
      }

      "go to correct page after PayPeriodQuestionPage" in {
        navigator.nextPage(
          PayPeriodQuestionPage,
          UserAnswers("id")
            .set(PayPeriodQuestionPage, PayPeriodQuestion.UseSamePayPeriod)
            .success
            .value
        ) mustBe routes.PayMethodController.onPageLoad()

        navigator.nextPage(
          PayPeriodQuestionPage,
          UserAnswers("id")
            .set(PayPeriodQuestionPage, PayPeriodQuestion.UseDifferentPayPeriod)
            .success
            .value
        ) mustBe routes.PaymentFrequencyController.onPageLoad()
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
