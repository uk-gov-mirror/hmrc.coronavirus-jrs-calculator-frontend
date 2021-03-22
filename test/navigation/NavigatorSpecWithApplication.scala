/*
 * Copyright 2021 HM Revenue & Customs
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
import base.{CoreTestDataBuilder, SpecBaseControllerSpecs}
import config.featureSwitch.{ExtensionTwoNewStarterFlow, FeatureSwitching, StatutoryLeaveFlow}
import controllers.routes
import models.ClaimPeriodQuestion._
import models.PartTimeQuestion.{PartTimeNo, PartTimeYes}
import models.PayMethod.{Regular, Variable}
import models.PaymentFrequency.Monthly
import models._
import pages._
import play.api.mvc.Call

class NavigatorSpecWithApplication extends SpecBaseControllerSpecs with CoreTestDataBuilder with FeatureSwitching {

  override val navigator = new Navigator()

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from a page that doesn't exist in the route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, emptyUserAnswers) mustBe routes.RootPageController.onPageLoad()
      }

      "go to ClaimPeriodEndPage after ClaimPeriodStartPage" in {
        navigator.nextPage(ClaimPeriodStartPage, emptyUserAnswers) mustBe routes.ClaimPeriodEndController
          .onPageLoad()
      }

      "go to furloughOngoingPage after ClaimPeriodEndPage" in {
        navigator.nextPage(ClaimPeriodEndPage, emptyUserAnswers) mustBe routes.FurloughStartDateController
          .onPageLoad()
      }

      "go to correct page after furloughOngoingPage" in {
        navigator
          .nextPage(FurloughStatusPage, emptyUserAnswers.withFurloughStatus(FurloughStatus.FurloughOngoing)) mustBe routes.PaymentFrequencyController
          .onPageLoad()

        navigator
          .nextPage(FurloughStatusPage, emptyUserAnswers.withFurloughStatus(FurloughStatus.FurloughEnded)) mustBe routes.FurloughEndDateController
          .onPageLoad()
      }

      "go to PaymentFrequencyPage after FurloughEndDatePage" in {
        navigator.nextPage(FurloughEndDatePage, emptyUserAnswers) mustBe routes.PaymentFrequencyController
          .onPageLoad()
      }

      "go to pay dates page after PayMethodPage for claims starting before 01/11/2020" in {
        navigator.nextPage(PayMethodPage,
                           emptyUserAnswers
                             .withPayMethod(Regular)
                             .withClaimPeriodStart("2020-10-01")
                             .withPayDate(List())) mustBe routes.PayDateController.onPageLoad(1)

        navigator.nextPage(PayMethodPage, emptyUserAnswers) mustBe routes.PayMethodController.onPageLoad()
      }

      "go to furlough in last tax year page after PayMethodPage for claims starting 01/11/2020 onward with variable pay" in {
        navigator.nextPage(
          PayMethodPage,
          emptyUserAnswers
            .withPayMethod(PayMethod.Variable)
            .withClaimPeriodStart("2020-11-01")
        ) mustBe routes.FurloughInLastTaxYearController.onPageLoad()
      }

      "go to variable length employed page after PayMethodPage for claims starting before 01/11/2020 with variable pay" in {
        navigator.nextPage(
          PayMethodPage,
          emptyUserAnswers
            .withPayMethod(PayMethod.Variable)
            .withClaimPeriodStart("2020-10-31")
        ) mustBe routes.VariableLengthEmployedController.onPageLoad()
      }

      "go to calculation unsupported from furlough in last tax year when answered true" in {
        navigator.nextPage(
          FurloughInLastTaxYearPage,
          emptyUserAnswers.withFurloughInLastTaxYear(true)
        ) mustBe routes.CalculationUnsupportedController.multipleFurloughUnsupported()
      }

      "go to variable length employed from furlough in last tax year when answered false" in {
        navigator.nextPage(
          FurloughInLastTaxYearPage,
          emptyUserAnswers.withFurloughInLastTaxYear(false)
        ) mustBe routes.VariableLengthEmployedController.onPageLoad()
      }

      "repeat furlough in last tax year when answer hasn't been stored" in {
        navigator.nextPage(
          FurloughInLastTaxYearPage,
          emptyUserAnswers
        ) mustBe routes.FurloughInLastTaxYearController.onPageLoad()
      }

      "go to regular-pay-amount page after PayMethodPage if regular and PayDates were persisted in fast journey" in {
        navigator.nextPage(PayMethodPage,
                           dummyUserAnswers
                             .withPayMethod(Regular)
                             .withPayDate(List("2020-1-1"))) mustBe routes.RegularPayAmountController.onPageLoad()
      }

      "go to RegularLengthEmployedPage after PayMethodPage for claims starting on or after 01/11/2020 for Regular payMethods" in {
        navigator.nextPage(PayMethodPage,
                           emptyUserAnswers
                             .withPayMethod(Regular)
                             .withClaimPeriodStart("2020-11-01")
                             .withPayDate(List())) mustBe routes.RegularLengthEmployedController.onPageLoad()

        navigator.nextPage(PayMethodPage, emptyUserAnswers) mustBe routes.PayMethodController.onPageLoad()
      }

      "RegularLengthEmployedPage" when {

        "the ExtensionTwoNewStarterFlow switch is enabled" when {

          "claim period start date is on or after 01/11/2020" should {

            "go to OnPayrollBefore30thOct2020Page if PayDate is defined & answer == No" in {

              enable(ExtensionTwoNewStarterFlow)

              navigator.nextPage(
                RegularLengthEmployedPage,
                emptyUserAnswers
                  .withRegularLengthEmployed(RegularLengthEmployed.No)
                  .withPayMethod(Regular)
                  .withClaimPeriodStart("2020-11-01")
              ) mustBe routes.OnPayrollBefore30thOct2020Controller.onPageLoad()
            }

            "go to RegularPayAmountPage if PayDate is defined" in {

              enable(ExtensionTwoNewStarterFlow)
              navigator.nextPage(
                RegularLengthEmployedPage,
                emptyUserAnswers
                  .withRegularLengthEmployed(RegularLengthEmployed.Yes)
                  .withPayMethod(Regular)
                  .withClaimPeriodStart("2020-11-01")
                  .withPayDate(List("2020-10-31"))
              ) mustBe routes.RegularPayAmountController.onPageLoad()
            }

            "go to PayDatePage if PayDate is not defined" in {

              enable(ExtensionTwoNewStarterFlow)
              navigator.nextPage(
                RegularLengthEmployedPage,
                emptyUserAnswers
                  .withRegularLengthEmployed(RegularLengthEmployed.Yes)
                  .withPayMethod(Regular)
                  .withClaimPeriodStart("2020-11-01")
              ) mustBe routes.PayDateController.onPageLoad(1)
            }
          }
        }

        "the ExtensionTwoNewStarterFlow switch is disabled" when {

          "claim period start date is on or after 01/11/2020" should {

            "go to RegularPayAmountPage if PayDate is defined & answer == Yes" in {

              disable(ExtensionTwoNewStarterFlow)

              navigator.nextPage(
                RegularLengthEmployedPage,
                emptyUserAnswers
                  .withRegularLengthEmployed(RegularLengthEmployed.Yes)
                  .withPayMethod(Regular)
                  .withClaimPeriodStart("2020-11-01")
                  .withPayDate(List("2020-10-31"))
              ) mustBe routes.RegularPayAmountController.onPageLoad()
            }

            "go to RegularPayAmountPage if PayDate is defined & answer == No" in {

              disable(ExtensionTwoNewStarterFlow)

              navigator.nextPage(
                RegularLengthEmployedPage,
                emptyUserAnswers
                  .withRegularLengthEmployed(RegularLengthEmployed.No)
                  .withPayMethod(Regular)
                  .withClaimPeriodStart("2020-11-01")
                  .withPayDate(List("2020-10-31"))
              ) mustBe routes.RegularPayAmountController.onPageLoad()
            }

            "go to PayDatePage if PayDate is not defined" in {

              disable(ExtensionTwoNewStarterFlow)
              navigator.nextPage(
                RegularLengthEmployedPage,
                emptyUserAnswers
                  .withRegularLengthEmployed(RegularLengthEmployed.Yes)
                  .withPayMethod(Regular)
                  .withClaimPeriodStart("2020-11-01")
              ) mustBe routes.PayDateController.onPageLoad(1)
            }
          }
        }

        "the date is before 01/11/2020" should {

          "go to the Root Page" in {

            navigator.nextPage(
              RegularLengthEmployedPage,
              emptyUserAnswers
                .withRegularLengthEmployed(RegularLengthEmployed.No)
                .withPayMethod(Regular)
                .withClaimPeriodStart("2020-10-01")
            ) mustBe routes.RootPageController.onPageLoad()

          }
        }
      }

      "go to RegularPayAmountPage after PaymentQuestionPage" in {
        navigator.nextPage(PaymentFrequencyPage, emptyUserAnswers) mustBe routes.PayMethodController.onPageLoad()
      }

      "go to TopUpStatusPage after RegularPayAmountPage" in {
        navigator.nextPage(RegularPayAmountPage, emptyUserAnswers) mustBe routes.TopUpStatusController.onPageLoad()
      }

      "go to NicCategoryPage after PartTimeQuestionPage if `PartTimeNo`" in {
        navigator.nextPage(PartTimeQuestionPage,
                           emptyUserAnswers
                             .withClaimPeriodStart("2020, 7, 31")
                             .withPartTimeQuestion(PartTimeNo)) mustBe routes.NicCategoryController.onPageLoad()
      }

      "go to ConfirmationPage after PartTimeQuestionPage if `PartTimeNo` and claim started after July" in {
        navigator.nextPage(PartTimeQuestionPage,
                           emptyUserAnswers
                             .withClaimPeriodStart("2020, 8, 1")
                             .withPartTimeQuestion(PartTimeNo)) mustBe routes.ConfirmationController.onPageLoad()
      }

      "go to PartTimeQuestionPage after RegularPayAmountPage if phase two started and furlough has ended" in {
        val userAnswers = emptyUserAnswers
          .withClaimPeriodStart("2020,7,1")
          .withFurloughStatus(FurloughStatus.FurloughEnded)

        val navigator = new Navigator()

        navigator.nextPage(RegularPayAmountPage, userAnswers) mustBe routes.PartTimeQuestionController.onPageLoad()
      }

      "go to PartTimeQuestionPage after RegularPayAmountPage if phase two started and furlough is ongoing" in {
        val userAnswers = emptyUserAnswers
          .withClaimPeriodStart("2020,7,1")
          .withFurloughStatus(FurloughStatus.FurloughOngoing)

        val navigator = new Navigator()

        navigator.nextPage(RegularPayAmountPage, userAnswers) mustBe routes.PartTimeQuestionController.onPageLoad()
      }

      "go to PartTimeQuestionPage after RegularPayAmountPage if phase two started and furlough is ongoing and claim start Aug onwards" in {
        val userAnswers = emptyUserAnswers
          .withClaimPeriodStart("2020,8,1")
          .withFurloughStatus(FurloughStatus.FurloughOngoing)

        val navigator = new Navigator()

        navigator.nextPage(RegularPayAmountPage, userAnswers) mustBe routes.PartTimeQuestionController.onPageLoad()
      }

      "go to PartTimePeriodsPage after PartTimeQuestionPage if PartTimeQuestion is PartTimeYes" in {
        val answersWithPartTime = emptyUserAnswers.withPartTimeQuestion(PartTimeYes)

        navigator
          .nextPage(PartTimeQuestionPage, answersWithPartTime.withPayDate(List("2020, 7, 1"))) mustBe routes.PartTimePeriodsController
          .onPageLoad()
      }

      "go to PartTimeQuestionPage after AnnualPayAmountPage if phase two started and furlough has ended" in {
        val userAnswers = emptyUserAnswers
          .withClaimPeriodStart(LocalDate.of(2021, 3, 1))
          .withFurloughStatus(FurloughStatus.FurloughEnded)

        navigator.nextPage(AnnualPayAmountPage, userAnswers) mustBe routes.PartTimeQuestionController.onPageLoad()
      }

      "go to PartTimeQuestionPage after AnnualPayAmountPage if May onwards claim AND Stat Leave journey is disabled" in {

        disable(StatutoryLeaveFlow)

        val userAnswers = emptyUserAnswers
          .withClaimPeriodStart(LocalDate.of(2021, 5, 1))

        navigator.nextPage(AnnualPayAmountPage, userAnswers) mustBe routes.PartTimeQuestionController.onPageLoad()
      }

      "go to StatutoryLeavePage after AnnualPayAmountPage if May onwards claim AND Stat Leave journey is enabled" in {

        enable(StatutoryLeaveFlow)

        val userAnswers = emptyUserAnswers
          .withClaimPeriodStart(LocalDate.of(2021, 5, 1))

        navigator.nextPage(AnnualPayAmountPage, userAnswers) mustBe routes.HasEmployeeBeenOnStatutoryLeaveController.onPageLoad()
      }

      "go to PartTimeHours after PartTimePeriods" in {
        val partTimePeriods: List[Periods] = List(fullPeriod("2020,7,1", "2020,7,8"), fullPeriod("2020,7,9", "2020,7,15"))
        val userAnswers                    = mandatoryAnswersOnRegularMonthly.withPartTimePeriods(partTimePeriods)
        navigator.nextPage(
          PartTimePeriodsPage,
          userAnswers
        ) mustBe routes.PartTimeNormalHoursController.onPageLoad(1)
      }

      "go to PartTimeNormalHours after PartTimeHours" in {
        val partTimePeriods: List[Periods] = List(fullPeriod("2020,7,1", "2020,7,8"), fullPeriod("2020,7,9", "2020,7,15"))
        val userAnswers                    = mandatoryAnswersOnRegularMonthly.withPartTimePeriods(partTimePeriods)
        navigator.nextPage(
          PartTimeHoursPage,
          userAnswers,
          Some(1)
        ) mustBe routes.PartTimeNormalHoursController.onPageLoad(2)
      }

      "go to Nic after PartTimeHours if period not found" in {
        val partTimePeriods: List[Periods] = List(fullPeriod("2020,7,1", "2020,7,8"), fullPeriod("2020,7,9", "2020,7,15"))
        val userAnswers                    = mandatoryAnswersOnRegularMonthly.withPartTimePeriods(partTimePeriods)
        navigator.nextPage(
          PartTimeHoursPage,
          userAnswers,
          Some(3)
        ) mustBe routes.NicCategoryController.onPageLoad()
      }

      "go to ConfirmationPage after PartTimeHours if period not found and claim started after July" in {
        val partTimePeriods: List[Periods] = List(fullPeriod("2020,8,1", "2020,8,8"), fullPeriod("2020,8,9", "2020,8,15"))
        val userAnswers = emptyUserAnswers
          .withClaimPeriodStart("2020, 8, 1")
          .withClaimPeriodEnd("2020, 8, 31")
          .withFurloughStartDate("2020, 8, 1")
          .withFurloughStatus()
          .withPaymentFrequency(Monthly)
          .withNiCategory()
          .withPensionStatus()
          .withPayMethod()
          .withLastPayDate("2020, 8, 31")
          .withPayDate(List("2020, 7, 29", "2020, 8, 31"))
          .withPartTimePeriods(partTimePeriods)

        navigator.nextPage(
          PartTimeHoursPage,
          userAnswers,
          Some(3)
        ) mustBe routes.ConfirmationController.onPageLoad()
      }

      "loop from PartTimeHours to PartTimeNormalHours if there are more PartTimePeriods to iterate" in {
        val partTimePeriods: List[Periods] = List(fullPeriod("2020,7,1", "2020,7,8"), fullPeriod("2020,7,9", "2020,7,15"))
        val userAnswers                    = mandatoryAnswersOnRegularMonthly.withPartTimePeriods(partTimePeriods)
        navigator.nextPage(
          PartTimeNormalHoursPage,
          userAnswers,
          Some(1)
        ) mustBe routes.PartTimeHoursController.onPageLoad(1)
      }

      "stop loop around part time pages if there are no more PartTimePeriods to iterate" in {
        val partTimePeriods: List[Periods] = List(fullPeriod("2020,7,1", "2020,7,8"), fullPeriod("2020,7,9", "2020,7,15"))
        val userAnswers                    = mandatoryAnswersOnRegularMonthly.withPartTimePeriods(partTimePeriods)
        navigator.nextPage(
          PartTimeHoursPage,
          userAnswers,
          Some(2)
        ) mustBe routes.NicCategoryController.onPageLoad()
      }

      "loop around pay date if last pay date isn't claim end date or after" in {
        val userAnswers = emptyUserAnswers
          .set(ClaimPeriodEndPage, LocalDate.of(2020, 5, 30))
          .get
          .set(PayDatePage, LocalDate.of(2020, 5, 29), Some(1))
          .get

        navigator.nextPage(PayDatePage, userAnswers, Some(1)) mustBe routes.PayDateController.onPageLoad(2)
      }

      "stop loop around pay date if last pay date is claim end date" in {
        val userAnswers = emptyUserAnswers
          .set(ClaimPeriodEndPage, LocalDate.of(2020, 5, 30))
          .get
          .set(PayDatePage, LocalDate.of(2020, 5, 30), Some(1))
          .get

        navigator.nextPage(PayDatePage, userAnswers, Some(1)) mustBe routes.PayPeriodsListController
          .onPageLoad()
      }

      "stop loop around pay date if last pay date is after claim end date" in {
        val userAnswers = emptyUserAnswers
          .set(ClaimPeriodEndPage, LocalDate.of(2020, 5, 30))
          .get
          .set(PayDatePage, LocalDate.of(2020, 5, 31), Some(1))
          .get

        navigator.nextPage(PayDatePage, userAnswers, Some(1)) mustBe routes.PayPeriodsListController
          .onPageLoad()
      }

      "display LastPayDatePage only when relevant" in {
        navigator.nextPage(
          PayPeriodsListPage,
          emptyUserAnswers
            .withClaimPeriodStart("2020,3,1")
            .withClaimPeriodEnd("2020,3,31")
            .withPayDate(List("2020,2,29", "2020,3,31"))
            .withPayMethod()
            .withPayPeriodsList()
        ) mustBe routes.LastPayDateController.onPageLoad()

        navigator.nextPage(
          PayPeriodsListPage,
          emptyUserAnswers
            .withClaimPeriodStart("2020,4,1")
            .withClaimPeriodEnd("2020,4,30")
            .withPayDate(List("2020,3,31", "2020,4,30"))
            .withPayPeriodsList()
        ) mustBe routes.LastPayDateController.onPageLoad()

        navigator.nextPage(
          PayPeriodsListPage,
          emptyUserAnswers
            .withClaimPeriodStart("2020,5,1")
            .withClaimPeriodEnd("2020,5,31")
            .withPayDate(List("2020,4,30", "2020,5,31"))
            .withPayMethod()
            .withPayPeriodsList()
        ) mustBe routes.RegularPayAmountController.onPageLoad()
      }

      "go to NicCategoryPage after LastPayDatePage if the pay-method is Regular" in {
        val userAnswers = emptyUserAnswers
          .set(PayMethodPage, Regular)
          .get

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.RegularPayAmountController.onPageLoad()
      }

      "go to LastYearPayPage after LastPayDatePage if the pay-method is Variable and EmployeeStarted.OnOrBefore1Feb2019" in {
        val userAnswers = emptyUserAnswers
          .withPayMethod(Variable)
          .withEmployeeStartedOnOrBefore1Feb2019()

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.LastYearPayController.onPageLoad(1)
      }

      "go to LastYearPayPage after LastPayDatePage if the pay-method is Variable and employee started before apr6th2019" in {
        val userAnswers = emptyUserAnswers
          .withPayMethod(Variable)
          .withPaymentFrequency(PaymentFrequency.Weekly)
          .withClaimPeriodStart("2020,3,20")
          .withClaimPeriodEnd("2020,3,26")
          .withFurloughStartDate("2020,3,20")
          .withPayDate(List("2020, 3, 19", "2020, 3, 27"))
          .withEmployeeStartedAfter1Feb2019()
          .withEmployeeStartDate("2019,3,4")

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.LastYearPayController.onPageLoad(1)
      }

      "go to LastYearPayPage after LastPayDatePage if the pay-method is Variable and employee started on or after Apr6th" in {
        val userAnswers = emptyUserAnswers
          .withPayMethod(Variable)
          .withEmployeeStartedAfter1Feb2019()
          .withEmployeeStartDate("2019,10,26") // Just outside of overlap
          .withPaymentFrequency(PaymentFrequency.Weekly)
          .withClaimPeriodStart("2020,11,1")
          .withClaimPeriodEnd("2020,11,7")
          .withFurloughStartDate("2020,11,1")
          .withPayDate(List("2020, 10, 31", "2020, 11, 7"))

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.LastYearPayController.onPageLoad(1)
      }

      "go to AnnualPayAmountPage after LastPayDatePage if the pay-method is Variable and EmployeeStarted on or after Apr6th" in {
        val userAnswers = emptyUserAnswers
          .withPayMethod(Variable)
          .withEmployeeStartedAfter1Feb2019()
          .withEmployeeStartDate("2019,5,10")
          .withPaymentFrequency(PaymentFrequency.Weekly)
          .withClaimPeriodStart("2020,6,20")
          .withClaimPeriodEnd("2020,6,26")
          .withFurloughStartDate("2020,6,20")
          .withPayDate(List("2020, 6, 19", "2020, 6, 27"))

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.AnnualPayAmountController.onPageLoad()
      }

      "go to last year pay after LastPayDatePage if the pay-method is Variable and employee start overlaps lookback first day" in {
        val userAnswers = emptyUserAnswers
          .withPayMethod(Variable)
          .withEmployeeStartedAfter1Feb2019()
          .withEmployeeStartDate("2019,10,27")
          .withPaymentFrequency(PaymentFrequency.Weekly)
          .withClaimPeriodStart("2020,11,1")
          .withClaimPeriodEnd("2020,11,7")
          .withFurloughStartDate("2020,11,1")
          .withPayDate(List("2020, 10, 31", "2020, 11, 7"))

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.LastYearPayController.onPageLoad(1)
      }

      "go to calculation unsupported after LastPayDatePage if the pay-method is Variable and employee start overlaps lookback after the first day" in {
        val userAnswers = emptyUserAnswers
          .withPayMethod(Variable)
          .withEmployeeStartedAfter1Feb2019()
          .withEmployeeStartDate("2019,10,28")
          .withPaymentFrequency(PaymentFrequency.Weekly)
          .withClaimPeriodStart("2020,11,1")
          .withClaimPeriodEnd("2020,11,7")
          .withFurloughStartDate("2020,11,1")
          .withPayDate(List("2020, 10, 31", "2020, 11, 7"))

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.CalculationUnsupportedController.startDateWithinLookbackUnsupported()
      }

      "go to calculation unsupported after LastPayDatePage if the pay-method is Variable and employee start overlaps the second lookback period" in {
        val userAnswers = emptyUserAnswers
          .withPayMethod(Variable)
          .withEmployeeStartedAfter1Feb2019()
          .withEmployeeStartDate("2019,11,9")
          .withPaymentFrequency(PaymentFrequency.Weekly)
          .withClaimPeriodStart("2020,11,1")
          .withClaimPeriodEnd("2020,11,30")
          .withFurloughStartDate("2020,11,1")
          .withFurloughEndDate("2020,11,14")
          .withPayDate(List("2020, 10, 31", "2020, 11, 7", "2020, 11, 14"))

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.CalculationUnsupportedController.startDateWithinLookbackUnsupported()
      }

      "go to AnnualPayAmountPage after LastPayDatePage if the pay-method is Variable and employee start is after the second lookback period" in {
        val userAnswers = emptyUserAnswers
          .withPayMethod(Variable)
          .withEmployeeStartedAfter1Feb2019()
          .withEmployeeStartDate("2019,11,10")
          .withPaymentFrequency(PaymentFrequency.Weekly)
          .withClaimPeriodStart("2020,11,1")
          .withClaimPeriodEnd("2020,11,30")
          .withFurloughStartDate("2020,11,1")
          .withFurloughEndDate("2020,11,14")
          .withPayDate(List("2020, 10, 31", "2020, 11, 7", "2020, 11, 14"))

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.AnnualPayAmountController.onPageLoad()
      }

      "go to payMethodPage after LastPayDatePage if the pay-method missing in UserAnswers" in {
        val userAnswers = emptyUserAnswers

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

      "go to pay dates page after EmployedStartedPage in a normal journey" in {
        navigator.nextPage(EmployeeStartedPage, emptyUserAnswers.withEmployeeStartedOnOrBefore1Feb2019()) mustBe routes.PayDateController
          .onPageLoad(1)

        navigator
          .nextPage(EmployeeStartedPage, emptyUserAnswers.withEmployeeStartedAfter1Feb2019()) mustBe routes.EmployeeStartDateController
          .onPageLoad()
      }

      "go to last-year-pay after EmployedStartedPage in a fast journey if pay dates are persisted and OnOrBefore1Feb2019" in {
        navigator.nextPage(
          EmployeeStartedPage,
          emptyUserAnswers
            .withEmployeeStartedOnOrBefore1Feb2019()
            .withPayDate(List("2019-12-12"))
        ) mustBe routes.LastYearPayController.onPageLoad(1)
      }

      "go to EmployedStartedPage after `variable-length-employed` in a fast journey if pay dates are persisted and After1Feb2019" in {
        navigator.nextPage(
          EmployeeStartedPage,
          emptyUserAnswers
            .withEmployeeStartedAfter1Feb2019()
            .withPayDate(List("2019-12-12"))
        ) mustBe routes.EmployeeStartDateController.onPageLoad()
      }

      "EmployeeStartDatePage" when {

        "the ExtensionTwoNewStarterFlow switch is enabled" should {

          "and the employee start date is after the 19th March 2020, return the OnPayrollBefore30thOct2020 page" in {

            enable(ExtensionTwoNewStarterFlow)

            navigator.nextPage(
              EmployeeStartDatePage,
              emptyUserAnswers
                .withEmployeeStartDate("2020,3,20")
                .withClaimPeriodStart("2020,11,1")
            ) mustBe routes.OnPayrollBefore30thOct2020Controller.onPageLoad()
          }

        }

        "the ExtensionTwoNewStarterFlow switch is disabled" should {

          "go to correct next page" in {

            disable(ExtensionTwoNewStarterFlow)

            navigator.nextPage(
              EmployeeStartDatePage,
              emptyUserAnswers
                .withEmployeeStartDate("2020,3,20")
                .withClaimPeriodStart("2020,11,1")
            ) mustBe routes.PayDateController.onPageLoad(1)

            navigator.nextPage(
              EmployeeStartDatePage,
              emptyUserAnswers
                .withEmployeeStartDate("2020,4,1")
                .withFurloughStartDate("2020,11,10")
                .withClaimPeriodStart("2020,11,1")
            ) mustBe routes.PreviousFurloughPeriodsController.onPageLoad()

            navigator.nextPage(
              EmployeeStartDatePage,
              emptyUserAnswers
                .withEmployeeStartDate("2020,2,2")
                .withClaimPeriodStart("2020,11,1")
            ) mustBe routes.EmployeeRTISubmissionController.onPageLoad()

            navigator.nextPage(
              EmployeeStartDatePage,
              emptyUserAnswers
                .withEmployeeStartDate("2020,2,3")
                .withClaimPeriodStart("2020,11,1")
            ) mustBe routes.EmployeeRTISubmissionController.onPageLoad()

            navigator.nextPage(
              EmployeeStartDatePage,
              emptyUserAnswers
                .withEmployeeStartDate("2019,8,1")
                .withPayDate(List("2020,4,1", "2020,4,30"))
                .withClaimPeriodStart("2020,11,1")
            ) mustBe routes.LastPayDateController.onPageLoad()

            navigator.nextPage(
              EmployeeStartDatePage,
              emptyUserAnswers
                .withPayMethod()
                .withClaimPeriodStart("2020,11,1")
                .withEmployeeStartDate("2019, 8, 1")
                .withPayDate(List("2020,3,1", "2020,3,7"))
                .withLastPayDate("2020,3,7")
            ) mustBe routes.RegularPayAmountController.onPageLoad()

            navigator.nextPage(
              EmployeeStartDatePage,
              emptyUserAnswers
                .withPayMethod(PayMethod.Variable)
                .withPaymentFrequency(PaymentFrequency.Weekly)
                .withEmployeeStartedAfter1Feb2019()
                .withClaimPeriodStart("2020,11,1")
                .withClaimPeriodEnd("2020,11,8")
                .withFurloughStartDate("2020,11,1")
                .withEmployeeStartDate("2019, 8, 1")
                .withPayDate(List("2020,10,31", "2020,11,7"))
                .withLastPayDate("2020,11,7")
            ) mustBe routes.LastYearPayController.onPageLoad(1)

            navigator.nextPage(
              EmployeeStartDatePage,
              emptyUserAnswers
                .withPayMethod(PayMethod.Variable)
                .withPaymentFrequency(PaymentFrequency.Weekly)
                .withEmployeeStartedAfter1Feb2019()
                .withClaimPeriodStart("2020,11,1")
                .withClaimPeriodEnd("2020,11,8")
                .withEmployeeStartDate("2019, 3, 30")
                .withPayDate(List("2020,10,31", "2020,11,7"))
                .withLastPayDate("2020,11,7")
                .withFurloughStartDate("2020,11,1")
            ) mustBe routes.LastYearPayController.onPageLoad(1)
          }
        }
      }

      "EmployeeSRTISubmissionPage" when {

        "the ExtensionTwoNewStarterFlow switch is enabled" when {

          "answered No" should {

            "return the OnPayrollBefore30thOct2020 page" in {

              enable(ExtensionTwoNewStarterFlow)

              navigator.nextPage(
                EmployeeRTISubmissionPage,
                emptyUserAnswers
                  .withFurloughStartDate("2020,11,15")
                  .withRtiSubmission(EmployeeRTISubmission.No)
              ) mustBe routes.OnPayrollBefore30thOct2020Controller.onPageLoad()
            }
          }

          "answered Yes" should {

            "return the PayDatePage when furlough date is defined" in {

              enable(ExtensionTwoNewStarterFlow)

              navigator.nextPage(
                EmployeeRTISubmissionPage,
                emptyUserAnswers
                  .withFurloughStartDate("2020,11,15")
                  .withRtiSubmission(EmployeeRTISubmission.Yes)
              ) mustBe routes.PayDateController.onPageLoad(1)

            }

            "return the PayDatePage when furlough date is not defined" in {

              enable(ExtensionTwoNewStarterFlow)

              navigator.nextPage(
                EmployeeRTISubmissionPage,
                emptyUserAnswers.withRtiSubmission(EmployeeRTISubmission.Yes)
              ) mustBe routes.PayDateController.onPageLoad(1)
            }
          }
        }

        "the ExtensionTwoNewStarterFlow switch is disabled" should {

          "return the correct page" in {

            disable(ExtensionTwoNewStarterFlow)

            navigator.nextPage(
              EmployeeRTISubmissionPage,
              emptyUserAnswers.withRtiSubmission(EmployeeRTISubmission.Yes)
            ) mustBe routes.PayDateController.onPageLoad(1)

            navigator.nextPage(
              EmployeeRTISubmissionPage,
              emptyUserAnswers
                .withFurloughStartDate("2020,11,15")
                .withRtiSubmission(EmployeeRTISubmission.Yes)
            ) mustBe routes.PayDateController.onPageLoad(1)

            navigator.nextPage(
              EmployeeRTISubmissionPage,
              emptyUserAnswers
                .withFurloughStartDate("2020,11,15")
                .withRtiSubmission(EmployeeRTISubmission.No)
            ) mustBe routes.PreviousFurloughPeriodsController.onPageLoad()
          }
        }
      }

      "go to PartialPayBeforeFurloughPage loop after variable gross pay page" in {
        val userAnswers = emptyUserAnswers
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

      "go to PartialPayAfterFurloughPage loop after variable gross pay page phase one" in {
        val userAnswers = emptyUserAnswers
          .withFurloughStartDate("2020, 3, 10")
          .withPayDate(List("2020, 3, 9", "2020, 4, 10", "2020, 5, 10", "2020, 6, 10"))
          .withClaimPeriodStart("2020, 3, 10")
          .withClaimPeriodEnd("2020, 5, 15")

        navigator.nextPage(
          AnnualPayAmountPage,
          userAnswers
        ) mustBe routes.PartialPayAfterFurloughController.onPageLoad()
      }

      "go to TopUpStatusPage after variable gross pay page if there are no partial furloughs phase one" in {
        val userAnswers = emptyUserAnswers
          .withFurloughStartDate("2020, 3, 1")
          .withFurloughEndDate("2020, 4, 10")
          .withPayDate(List("2020, 3, 1", "2020, 4, 10"))

        navigator.nextPage(
          AnnualPayAmountPage,
          userAnswers
        ) mustBe routes.TopUpStatusController.onPageLoad()
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

      "got to confirmation page from AdditionalPaymentAmountPage if claim started after July" in {
        val additionalPaymentDates = List(
          LocalDate.of(2020, 8, 15),
          LocalDate.of(2020, 9, 15)
        )

        val userAnswers = emptyUserAnswers
          .withClaimPeriodStart("2020, 8, 2")
          .withAdditionalPaymentPeriods(additionalPaymentDates.map(_.toString))

        navigator.nextPage(
          AdditionalPaymentAmountPage,
          userAnswers,
          Some(2)
        ) mustBe routes.ConfirmationController.onPageLoad()
      }

      "go to AdditionalPaymentPeriods page after AdditionalPaymentStatusPage" in {
        navigator.nextPage(
          AdditionalPaymentStatusPage,
          emptyUserAnswers
            .set(AdditionalPaymentStatusPage, AdditionalPaymentStatus.YesAdditionalPayments)
            .success
            .value
        ) mustBe routes.AdditionalPaymentPeriodsController.onPageLoad()
      }

      "go to Nic page after AdditionalPaymentStatusPage if NoAdditionalPayments" in {
        navigator.nextPage(
          AdditionalPaymentStatusPage,
          emptyUserAnswers
            .withClaimPeriodStart("2020, 7, 31")
            .withAdditionalPaymentStatus(AdditionalPaymentStatus.NoAdditionalPayments)
        ) mustBe routes.NicCategoryController.onPageLoad()
      }

      "go to Confirmation page after AdditionalPaymentStatusPage if NoAdditionalPayments and after July" in {
        navigator.nextPage(
          AdditionalPaymentStatusPage,
          emptyUserAnswers
            .withClaimPeriodStart("2020, 8, 1")
            .withAdditionalPaymentStatus(AdditionalPaymentStatus.NoAdditionalPayments)
        ) mustBe routes.ConfirmationController.onPageLoad()
      }

      "go to TopUpStatusPage after PartialPayAfterFurloughPage if phase one" in {
        navigator.nextPage(
          PartialPayAfterFurloughPage,
          emptyUserAnswers
        ) mustBe routes.TopUpStatusController.onPageLoad()
      }

      "go to correct page after FurloughPeriodQuestionPage" in {
        navigator.nextPage(
          FurloughPeriodQuestionPage,
          emptyUserAnswers
            .set(FurloughPeriodQuestionPage, FurloughPeriodQuestion.FurloughedOnSamePeriod)
            .success
            .value
        ) mustBe routes.PayPeriodQuestionController.onPageLoad()

        navigator.nextPage(
          FurloughPeriodQuestionPage,
          emptyUserAnswers
            .set(FurloughPeriodQuestionPage, FurloughPeriodQuestion.FurloughedOnDifferentPeriod)
            .success
            .value
        ) mustBe routes.FurloughStartDateController.onPageLoad()
      }

      "go to correct page after PayPeriodQuestionPage" in {
        navigator.nextPage(
          PayPeriodQuestionPage,
          emptyUserAnswers
            .set(PayPeriodQuestionPage, PayPeriodQuestion.UseSamePayPeriod)
            .success
            .value
        ) mustBe routes.PayMethodController.onPageLoad()

        navigator.nextPage(
          PayPeriodQuestionPage,
          emptyUserAnswers
            .set(PayPeriodQuestionPage, PayPeriodQuestion.UseDifferentPayPeriod)
            .success
            .value
        ) mustBe routes.PaymentFrequencyController.onPageLoad()
      }

      "go to correct page after ClaimPeriodQuestionPage" in {
        navigator.nextPage(
          ClaimPeriodQuestionPage,
          emptyUserAnswers.withClaimPeriodQuestion(ClaimOnSamePeriod)
        ) mustBe routes.FurloughPeriodQuestionController.onPageLoad()

        navigator.nextPage(
          ClaimPeriodQuestionPage,
          emptyUserAnswers.withClaimPeriodQuestion(ClaimOnDifferentPeriod)
        ) mustBe routes.ClaimPeriodStartController.onPageLoad()
      }

      "go to the correct page after PreviousFurloughPeriodsPage" in {
        navigator.nextPage(
          PreviousFurloughPeriodsPage,
          emptyUserAnswers.withPreviousFurloughedPeriodsAnswer(true)
        ) mustBe routes.FirstFurloughDateController.onPageLoad()

        navigator.nextPage(
          PreviousFurloughPeriodsPage,
          emptyUserAnswers.withPreviousFurloughedPeriodsAnswer(false)
        ) mustBe routes.PayDateController.onPageLoad(1)
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

    "calling .requireLastPayDateRoutes()" when {

      "there are single Periods set" when {

        "the date is before 2020-4-5 return LastPayDateController.onPageLoad()" in {

          val userAnswers: UserAnswers = {
            emptyUserAnswers
              .set(PayDatePage, LocalDate.of(2020, 4, 4), Some(1))
              .success
              .value
          }

          val actual   = navigator.requireLastPayDateRoutes(userAnswers)
          val expected = routes.LastPayDateController.onPageLoad()

          actual mustBe expected
        }

        "the date.head is after or equal 2020-4-6 and pay method is regular return RegularPayAmountController.onPageLoad()" in {

          val userAnswers: UserAnswers = {
            emptyUserAnswers
              .set(PayDatePage, LocalDate.of(2020, 4, 6), Some(1))
              .success
              .value
              .set(PayMethodPage, Regular)
              .success
              .value
          }

          val actual: Call   = navigator.requireLastPayDateRoutes(userAnswers)
          val expected: Call = routes.RegularPayAmountController.onPageLoad()

          actual mustBe expected
        }
      }

      "there are multiple Periods set" when {

        "the date is before 2020-4-5 return LastPayDateController.onPageLoad()" in {

          val userAnswers: UserAnswers = {
            emptyUserAnswers
              .set(PayDatePage, LocalDate.of(2020, 4, 3), Some(1))
              .success
              .value
              .set(PayDatePage, LocalDate.of(2020, 4, 4), Some(1))
              .success
              .value
          }

          val actual   = navigator.requireLastPayDateRoutes(userAnswers)
          val expected = routes.LastPayDateController.onPageLoad()

          actual mustBe expected
        }

        "the date.head is after or equal 2020-4-6 and pay method is regular return RegularPayAmountController.onPageLoad()" in {

          val userAnswers: UserAnswers = {
            emptyUserAnswers
              .set(PayDatePage, LocalDate.of(2020, 4, 6), Some(1))
              .success
              .value
              .set(PayDatePage, LocalDate.of(2020, 4, 8), Some(2))
              .success
              .value
              .set(PayMethodPage, Regular)
              .success
              .value
          }

          val actual: Call   = navigator.requireLastPayDateRoutes(userAnswers)
          val expected: Call = routes.RegularPayAmountController.onPageLoad()

          actual mustBe expected
        }
      }
    }

    ".onPayrollBefore30thOct2020Routes()" when {
      "user has selected the 'Regular' pay option" must {
        "claims starting before 01/11/2020 for Regular payMethods" in {

          val userAnswers = emptyUserAnswers
            .withPayMethod(Regular)
            .withOnPayrollBefore30thOct2020()
            .withClaimPeriodStart("2020-10-31")
          navigator.nextPage(OnPayrollBefore30thOct2020Page, userAnswers) mustBe routes.RootPageController.onPageLoad()
        }

        "OnPayrollBefore30thOct2020Page for claims starting on or after 01/11/2020 for Regular payMethods" when {

          "pay dates list is empty" in {

            val userAnswers = emptyUserAnswers
              .withPayMethod(Regular)
              .withOnPayrollBefore30thOct2020()
              .withClaimPeriodStart("2020-11-01")

            navigator.nextPage(OnPayrollBefore30thOct2020Page, userAnswers) mustBe routes.PayDateController.onPageLoad(1)
          }

          "RegularLengthEmployee answered Yes" in {

            val userAnswers = emptyUserAnswers
              .withPayMethod(Regular)
              .withOnPayrollBefore30thOct2020(true)
              .withClaimPeriodStart("2020-11-01")
              .withPayDate(List("2020-11-01"))

            navigator.nextPage(OnPayrollBefore30thOct2020Page, userAnswers) mustBe routes.RegularPayAmountController.onPageLoad()
          }

          "RegularLengthEmployee NOT answered" in {

            val userAnswers = emptyUserAnswers
              .withPayMethod(Regular)
              .withClaimPeriodStart("2020-11-01")
              .withPayDate(List("2020-11-01"))

            navigator.nextPage(OnPayrollBefore30thOct2020Page, userAnswers) mustBe routes.OnPayrollBefore30thOct2020Controller.onPageLoad()
          }
        }
      }

      "user has selected the 'Variable' pay option" must {
        "route to the employee first furloughed page when the furlough start date is after November 8th and was on payroll before 30th October 2020" in {
          enable(ExtensionTwoNewStarterFlow)
          val userAnswers: UserAnswers = {
            emptyUserAnswers
              .set(FurloughStartDatePage, LocalDate.of(2020, 11, 10))
              .success
              .value
              .set(PayMethodPage, Variable)
              .success
              .value
              .set(OnPayrollBefore30thOct2020Page, true)
              .success
              .value
          }

          val actual: Call   = navigator.onPayrollBefore30thOct2020Routes(userAnswers)
          val expected: Call = routes.PreviousFurloughPeriodsController.onPageLoad()

          actual mustBe expected
          disable(ExtensionTwoNewStarterFlow)
        }

        "route to the employee first furloughed page when the furlough start date is after May 8th and was not on payroll before 30th October 2020" in {
          enable(ExtensionTwoNewStarterFlow)
          val userAnswers: UserAnswers = {
            emptyUserAnswers
              .set(FurloughStartDatePage, LocalDate.of(2021, 5, 10))
              .success
              .value
              .set(PayMethodPage, Variable)
              .success
              .value
              .set(OnPayrollBefore30thOct2020Page, false)
              .success
              .value
          }

          val actual: Call   = navigator.onPayrollBefore30thOct2020Routes(userAnswers)
          val expected: Call = routes.PreviousFurloughPeriodsController.onPageLoad()

          actual mustBe expected
          disable(ExtensionTwoNewStarterFlow)
        }

        "route to the employee first furloughed page when the furlough start date is after May 8th and was on payroll before 30th October 2020" in {
          enable(ExtensionTwoNewStarterFlow)
          val userAnswers: UserAnswers = {
            emptyUserAnswers
              .set(FurloughStartDatePage, LocalDate.of(2021, 5, 10))
              .success
              .value
              .set(PayMethodPage, Variable)
              .success
              .value
              .set(OnPayrollBefore30thOct2020Page, true)
              .success
              .value
          }

          val actual: Call   = navigator.onPayrollBefore30thOct2020Routes(userAnswers)
          val expected: Call = routes.PreviousFurloughPeriodsController.onPageLoad()

          actual mustBe expected
          disable(ExtensionTwoNewStarterFlow)
        }

        "route to the last pay date page when the furlough start date is before November 8th and was on payroll before 30th October 2020" in {
          enable(ExtensionTwoNewStarterFlow)
          val userAnswers: UserAnswers = {
            emptyUserAnswers
              .set(FurloughStartDatePage, LocalDate.of(2020, 11, 7))
              .success
              .value
              .set(PayMethodPage, Variable)
              .success
              .value
              .set(OnPayrollBefore30thOct2020Page, true)
              .success
              .value
          }

          val actual: Call   = navigator.onPayrollBefore30thOct2020Routes(userAnswers)
          val expected: Call = routes.PayDateController.onPageLoad(1)

          actual mustBe expected
          disable(ExtensionTwoNewStarterFlow)
        }

        "route to the last pay date page when the furlough start date is before May 8th 2021 and was not on payroll before 30th October 2020" in {
          enable(ExtensionTwoNewStarterFlow)
          val userAnswers: UserAnswers = {
            emptyUserAnswers
              .set(FurloughStartDatePage, LocalDate.of(2021, 4, 7))
              .success
              .value
              .set(PayMethodPage, Variable)
              .success
              .value
              .set(OnPayrollBefore30thOct2020Page, false)
              .success
              .value
          }

          val actual: Call   = navigator.onPayrollBefore30thOct2020Routes(userAnswers)
          val expected: Call = routes.PayDateController.onPageLoad(1)

          actual mustBe expected
          disable(ExtensionTwoNewStarterFlow)
        }
      }

      "user has not selected a PayMethod option" must {
        "route the user back to the starting page" in {
          enable(ExtensionTwoNewStarterFlow)
          val userAnswers: UserAnswers = {
            emptyUserAnswers
              .set(FurloughStartDatePage, LocalDate.of(2021, 4, 7))
              .success
              .value
              .set(OnPayrollBefore30thOct2020Page, false)
              .success
              .value
          }

          val actual: Call   = navigator.onPayrollBefore30thOct2020Routes(userAnswers)
          val expected: Call = routes.RootPageController.onPageLoad()

          actual mustBe expected
          disable(ExtensionTwoNewStarterFlow)
        }
      }
    }

    ".routeToEmployeeFirstFurloughed" should {
      "route to the PreviousFurloughPeriods page" when {
        "the feature switch is disabled and the furlough start date is after 8th Nov 2020" in {
          disable(ExtensionTwoNewStarterFlow)
          val userAnswers: UserAnswers = {
            emptyUserAnswers
              .set(FurloughStartDatePage, LocalDate.of(2020, 12, 7))
              .success
              .value
              .set(PayMethodPage, Variable)
              .success
              .value
          }

          val actual: Call   = navigator.routeToEmployeeFirstFurloughed(userAnswers)
          val expected: Call = routes.PreviousFurloughPeriodsController.onPageLoad()

          actual mustBe expected
          enable(ExtensionTwoNewStarterFlow)
        }

        "the feature switch is enabled - and the furlough start date is after the 8th Nov 2020 and was on payroll before 30th Oct 2020" in {
          enable(ExtensionTwoNewStarterFlow)
          val userAnswers: UserAnswers = {
            emptyUserAnswers
              .set(FurloughStartDatePage, LocalDate.of(2021, 5, 7))
              .success
              .value
              .set(PayMethodPage, Variable)
              .success
              .value
              .set(OnPayrollBefore30thOct2020Page, true)
              .success
              .value
          }

          val actual: Call   = navigator.routeToEmployeeFirstFurloughed(userAnswers)
          val expected: Call = routes.PreviousFurloughPeriodsController.onPageLoad()

          actual mustBe expected
          disable(ExtensionTwoNewStarterFlow)
        }

        "the feature switch is enabled - and the furlough start date is after the 8th May 2021 and was not on payroll before 30th Oct 2020" in {
          enable(ExtensionTwoNewStarterFlow)
          val userAnswers: UserAnswers = {
            emptyUserAnswers
              .set(FurloughStartDatePage, LocalDate.of(2021, 5, 9))
              .success
              .value
              .set(PayMethodPage, Variable)
              .success
              .value
              .set(OnPayrollBefore30thOct2020Page, false)
              .success
              .value
          }

          val actual: Call   = navigator.routeToEmployeeFirstFurloughed(userAnswers)
          val expected: Call = routes.PreviousFurloughPeriodsController.onPageLoad()

          actual mustBe expected
          disable(ExtensionTwoNewStarterFlow)
        }
      }

      "route to the LastPayDate page" when {

        "the feature switch is enabled - and it doesn't fall into the above categories" in {
          enable(ExtensionTwoNewStarterFlow)
          val userAnswers: UserAnswers = {
            emptyUserAnswers
              .set(FurloughStartDatePage, LocalDate.of(2020, 10, 9))
              .success
              .value
              .set(PayMethodPage, Variable)
              .success
              .value
              .set(OnPayrollBefore30thOct2020Page, true)
              .success
              .value
          }

          val actual: Call   = navigator.routeToEmployeeFirstFurloughed(userAnswers)
          val expected: Call = routes.PayDateController.onPageLoad(1)

          actual mustBe expected
          disable(ExtensionTwoNewStarterFlow)
        }

        "the feature switch is disabled - and the furlough start date is before Nov 8th 2020" in {
          disable(ExtensionTwoNewStarterFlow)
          val userAnswers: UserAnswers = {
            emptyUserAnswers
              .set(FurloughStartDatePage, LocalDate.of(2020, 10, 9))
              .success
              .value
              .set(PayMethodPage, Variable)
              .success
              .value
          }

          val actual: Call   = navigator.routeToEmployeeFirstFurloughed(userAnswers)
          val expected: Call = routes.PayDateController.onPageLoad(1)

          actual mustBe expected
          enable(ExtensionTwoNewStarterFlow)
        }
      }
    }

    ".statutoryLeavePayRoutes" when {
      "feature switch is enabled" should {
        "route to the next page (PartTimeQuestionPage) when the answer to the StatutoryLeavePayPage is valid" in {
          enable(StatutoryLeaveFlow)
          val userAnswers: UserAnswers = {
            emptyUserAnswers
              .set(ClaimPeriodStartPage, LocalDate.of(2021, 5, 1))
              .success
              .value
              .set(ClaimPeriodEndPage, LocalDate.of(2021, 5, 31))
              .success
              .value
              .set(StatutoryLeavePayPage, Amount(BigDecimal(30.10)))
              .success
              .value
          }
          val actual: Call   = navigator.statutoryLeavePayRoutes(userAnswers)
          val expected: Call = routes.PartTimeQuestionController.onPageLoad()

          actual mustBe expected
          disable(StatutoryLeaveFlow)
        }

        "stay on the same page StatutoryLeavePayPage - when the answer is invalid" in {
          enable(StatutoryLeaveFlow)
          val userAnswers: UserAnswers = {
            emptyUserAnswers
              .set(ClaimPeriodStartPage, LocalDate.of(2021, 5, 1))
              .success
              .value
              .set(ClaimPeriodEndPage, LocalDate.of(2021, 5, 31))
              .success
              .value
          }
          val actual: Call   = navigator.statutoryLeavePayRoutes(userAnswers)
          val expected: Call = routes.StatutoryLeavePayController.onPageLoad()

          actual mustBe expected
          disable(StatutoryLeaveFlow)
        }
      }

      "feature switch is disabled" should {
        "route back to the RootPage" in {
          disable(StatutoryLeaveFlow)
          val actual: Call   = navigator.statutoryLeavePayRoutes(emptyUserAnswers)
          val expected: Call = routes.RootPageController.onPageLoad()

          actual mustBe expected
          enable(StatutoryLeaveFlow)
        }
      }
    }
  }
}
