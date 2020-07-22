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

import base.{CoreTestDataBuilder, SpecBaseControllerSpecs}
import controllers.routes
import models.ClaimPeriodQuestion._
import models.PartTimeQuestion.{PartTimeNo, PartTimeYes}
import models.PayMethod.{Regular, Variable}
import models.PaymentFrequency.Monthly
import models._
import pages._

class NavigatorSpecWithApplication extends SpecBaseControllerSpecs with CoreTestDataBuilder {

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
          .nextPage(FurloughStatusPage, emptyUserAnswers.withFurloughStatus(FurloughStatus.FlexibleFurlough)) mustBe routes.PaymentFrequencyController
          .onPageLoad()

        navigator
          .nextPage(FurloughStatusPage, emptyUserAnswers.withFurloughStatus(FurloughStatus.FurloughEnded)) mustBe routes.FurloughEndDateController
          .onPageLoad()
      }

      "go to PaymentFrequencyPage after FurloughEndDatePage" in {
        navigator.nextPage(FurloughEndDatePage, emptyUserAnswers) mustBe routes.PaymentFrequencyController
          .onPageLoad()
      }

      "go to pay dates page after PayMethodPage" in {
        navigator.nextPage(
          PayMethodPage,
          emptyUserAnswers
            .withPayMethod(Regular)
            .withPayDate(List())) mustBe routes.PayDateController.onPageLoad(1)

        navigator.nextPage(
          PayMethodPage,
          emptyUserAnswers.withPayMethod(PayMethod.Variable)
        ) mustBe routes.VariableLengthEmployedController.onPageLoad()

        navigator.nextPage(PayMethodPage, emptyUserAnswers) mustBe routes.PayMethodController.onPageLoad()
      }

      "go to regular-pay-amount page after PayMethodPage if regular and PayDates were persisted in fast journey" in {
        navigator.nextPage(
          PayMethodPage,
          dummyUserAnswers
            .withPayMethod(Regular)
            .withPayDate(List("2020-1-1"))) mustBe routes.RegularPayAmountController.onPageLoad()
      }

      "go to RegularPayAmountPage after PaymentQuestionPage" in {
        navigator.nextPage(PaymentFrequencyPage, emptyUserAnswers) mustBe routes.PayMethodController.onPageLoad()
      }

      "go to TopUpStatusPage after RegularPayAmountPage" in {
        navigator.nextPage(RegularPayAmountPage, emptyUserAnswers) mustBe routes.TopUpStatusController.onPageLoad()
      }

      "go to NicCategoryPage after PartTimeQuestionPage if `PartTimeNo`" in {
        navigator.nextPage(
          PartTimeQuestionPage,
          emptyUserAnswers
            .withClaimPeriodStart("2020, 7, 31")
            .withPartTimeQuestion(PartTimeNo)) mustBe routes.NicCategoryController.onPageLoad()
      }

      "go to ConfirmationPage after PartTimeQuestionPage if `PartTimeNo` and claim started after July" in {
        navigator.nextPage(
          PartTimeQuestionPage,
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

      "go to PartTimePeriodsPage after RegularPayAmountPage if phase two started and furlough is flexible" in {
        val userAnswers = emptyUserAnswers
          .withClaimPeriodStart("2020,7,1")
          .withFurloughStatus(FurloughStatus.FlexibleFurlough)

        val navigator = new Navigator()

        navigator.nextPage(RegularPayAmountPage, userAnswers) mustBe routes.PartTimePeriodsController.onPageLoad()
      }

      "go to NicCategoryPage after RegularPayAmountPage if phase two started and furlough is ongoing" in {
        val userAnswers = emptyUserAnswers
          .withClaimPeriodStart("2020,7,1")
          .withFurloughStatus(FurloughStatus.FurloughOngoing)

        val navigator = new Navigator()

        navigator.nextPage(RegularPayAmountPage, userAnswers) mustBe routes.NicCategoryController.onPageLoad()
      }

      "go to ConfirmationPage after RegularPayAmountPage if phase two started and furlough is ongoing and claim start Aug onwards" in {
        val userAnswers = emptyUserAnswers
          .withClaimPeriodStart("2020,8,1")
          .withFurloughStatus(FurloughStatus.FurloughOngoing)

        val navigator = new Navigator()

        navigator.nextPage(RegularPayAmountPage, userAnswers) mustBe routes.ConfirmationController.onPageLoad()
      }

      "go to PartTimePeriodsPage after PartTimeQuestionPage if PartTimeQuestion is PartTimeYes" in {
        val answersWithPartTime = emptyUserAnswers.withPartTimeQuestion(PartTimeYes)

        navigator
          .nextPage(PartTimeQuestionPage, answersWithPartTime.withPayDate(List("2020, 7, 1"))) mustBe routes.PartTimePeriodsController
          .onPageLoad()
      }

      "go to PartTimeQuestionPage after AnnualPayAmountPage if phase two started and furlough has ended" in {
        val userAnswers = emptyUserAnswers
          .withClaimPeriodStart(LocalDate.now)
          .withFurloughStatus(FurloughStatus.FurloughEnded)

        val navigator = new Navigator() {
          override lazy val phaseTwoStartDate: LocalDate = LocalDate.now
        }

        navigator.nextPage(AnnualPayAmountPage, userAnswers) mustBe routes.PartTimeQuestionController.onPageLoad()
      }

      "go to PartTimeHours after PartTimePeriods" in {
        val partTimePeriods: List[Periods] = List(fullPeriod("2020,7,1", "2020,7,8"), fullPeriod("2020,7,9", "2020,7,15"))
        val userAnswers = mandatoryAnswersOnRegularMonthly.withPartTimePeriods(partTimePeriods)
        navigator.nextPage(
          PartTimePeriodsPage,
          userAnswers
        ) mustBe routes.PartTimeNormalHoursController.onPageLoad(1)
      }

      "go to PartTimeNormalHours after PartTimeHours" in {
        val partTimePeriods: List[Periods] = List(fullPeriod("2020,7,1", "2020,7,8"), fullPeriod("2020,7,9", "2020,7,15"))
        val userAnswers = mandatoryAnswersOnRegularMonthly.withPartTimePeriods(partTimePeriods)
        navigator.nextPage(
          PartTimeHoursPage,
          userAnswers,
          Some(1)
        ) mustBe routes.PartTimeNormalHoursController.onPageLoad(2)
      }

      "go to Nic after PartTimeHours if period not found" in {
        val partTimePeriods: List[Periods] = List(fullPeriod("2020,7,1", "2020,7,8"), fullPeriod("2020,7,9", "2020,7,15"))
        val userAnswers = mandatoryAnswersOnRegularMonthly.withPartTimePeriods(partTimePeriods)
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
        val userAnswers = mandatoryAnswersOnRegularMonthly.withPartTimePeriods(partTimePeriods)
        navigator.nextPage(
          PartTimeNormalHoursPage,
          userAnswers,
          Some(1)
        ) mustBe routes.PartTimeHoursController.onPageLoad(1)
      }

      "stop loop around part time pages if there are no more PartTimePeriods to iterate" in {
        val partTimePeriods: List[Periods] = List(fullPeriod("2020,7,1", "2020,7,8"), fullPeriod("2020,7,9", "2020,7,15"))
        val userAnswers = mandatoryAnswersOnRegularMonthly.withPartTimePeriods(partTimePeriods)
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
          .withEmployeeStartedAfter1Feb2019()
          .withEmployeeStartDate("2019,3,4")

        navigator.nextPage(LastPayDatePage, userAnswers) mustBe routes.LastYearPayController.onPageLoad(1)
      }

      "go to AnnualPayAmountPage after LastPayDatePage if the pay-method is Variable and EmployeeStarted on or after Apr6th" in {
        val userAnswers = emptyUserAnswers
          .withPayMethod(Variable)
          .withEmployeeStartedAfter1Feb2019()
          .withEmployeeStartDate("2019,5,10")

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

      "go to correct page after EmployeeStartDatePage" in {
        navigator.nextPage(
          EmployeeStartDatePage,
          emptyUserAnswers
            .withEmployeeStartDate("2019,8,1")
        ) mustBe routes.PayDateController.onPageLoad(1)

        navigator.nextPage(
          EmployeeStartDatePage,
          emptyUserAnswers
            .withEmployeeStartDate("2019,8,1")
            .withPayDate(List("2020,3,1", "2020,3,7"))
        ) mustBe routes.LastPayDateController.onPageLoad()

        navigator.nextPage(
          EmployeeStartDatePage,
          emptyUserAnswers
            .withPayMethod()
            .withEmployeeStartDate("2019, 8, 1")
            .withPayDate(List("2020,3,1", "2020,3,7"))
            .withLastPayDate("2020,3,7")
        ) mustBe routes.RegularPayAmountController.onPageLoad()

        navigator.nextPage(
          EmployeeStartDatePage,
          emptyUserAnswers
            .withPayMethod(PayMethod.Variable)
            .withEmployeeStartedAfter1Feb2019()
            .withEmployeeStartDate("2019, 8, 1")
            .withPayDate(List("2020,3,1", "2020,3,7"))
            .withLastPayDate("2020,3,7")
        ) mustBe routes.AnnualPayAmountController.onPageLoad()

        navigator.nextPage(
          EmployeeStartDatePage,
          emptyUserAnswers
            .withPayMethod(PayMethod.Variable)
            .withEmployeeStartedAfter1Feb2019()
            .withEmployeeStartDate("2019, 3, 30")
            .withPayDate(List("2020,3,1", "2020,3,7"))
            .withLastPayDate("2020,3,7")
        ) mustBe routes.LastYearPayController.onPageLoad(1)
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
