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

package controllers

import assets.constants.ConfirmationConstants._
import base.{CoreTestDataBuilder, SpecBaseControllerSpecs}
import config.CalculatorVersionConfiguration
import messages.JRSExtensionConfirmationMessages._
import models.FurloughStatus.FurloughOngoing
import models.NicCategory.Payable
import models.PartTimeQuestion.PartTimeNo
import models.PayMethod.Variable
import models.PaymentFrequency.Monthly
import models.PensionStatus.DoesContribute
import models._
import models.requests.DataRequest
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{AuditService, EmployeeTypeService, Threshold}
import viewmodels.{ConfirmationViewBreakdownWithoutNicAndPension, PhaseTwoConfirmationViewBreakdown}
import views.html._

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConfirmationControllerSpec extends SpecBaseControllerSpecs with CoreTestDataBuilder {

  val view          = app.injector.instanceOf[ConfirmationViewWithDetailedBreakdowns]
  val phaseTwoView  = app.injector.instanceOf[PhaseTwoConfirmationView]
  val extensionView = app.injector.instanceOf[JrsExtensionConfirmationView]
  val audit         = app.injector.instanceOf[AuditService]
  val service       = app.injector.instanceOf[EmployeeTypeService]

  val controller = new ConfirmationController(
    messagesApi = messagesApi,
    identify = identifier,
    getData = dataRetrieval,
    requireData = dataRequired,
    controllerComponents = component,
    employeeTypeService = service,
    viewWithDetailedBreakdowns = view,
    phaseTwoView = phaseTwoView,
    extensionView = extensionView,
    auditService = audit,
    navigator = navigator
  )

  def userAnswers(): UserAnswers =
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 15")
      .withFurloughStatus(FurloughOngoing)
      .withPaymentFrequency(Monthly)
      .withNiCategory()
      .withPensionStatus()
      .withPayMethod(Variable)
      .withFurloughInLastTaxYear(false)
      .withVariableLengthEmployed(EmployeeStarted.After1Feb2019)
      .withEmployeeStartDate("2020, 1, 31")
      .withPreviousFurloughedPeriodsAnswer(true)
      .withFirstFurloughDate("2020, 11, 10")
      .withPayDate(List("2020, 10, 31", "2020, 12, 1"))
      .withAnnualPayAmount(10000.00)
      .withPartTimeQuestion(PartTimeNo)

  "Confirmation Controller" must {

    "return OK and the confirmation view with detailed breakdowns for a GET (March 2020 journey)" in new CalculatorVersionConfiguration {

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(dummyUserAnswers))

      val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)
      val result  = controller.onPageLoad()(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(breakdown, meta.claimPeriod, calculatorVersionConf)(request, messages).toString
    }

    "return OK and the phase two confirmation view with detailed breakdowns for a GET for dates 1st to 31st July 2020" in new CalculatorVersionConfiguration {

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(phaseTwoJourney()))

      val request: FakeRequest[AnyContentAsEmpty.type]     = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)
      val result: Future[Result]                           = controller.onPageLoad()(request)
      val dataRequest: DataRequest[AnyContentAsEmpty.type] = DataRequest(request, userAnswers().id, userAnswers())
      val payment: RegularPaymentWithPhaseTwoPeriod = {
        RegularPaymentWithPhaseTwoPeriod(
          regularPay = Amount(2000.00),
          referencePay = Amount(2000.0),
          phaseTwoPeriod = PhaseTwoPeriod(fullPeriodWithPaymentDate("2020, 7, 1", "2020, 7, 31", "2020, 7, 31"), None, None)
        )
      }

      val breakdown = PhaseTwoConfirmationViewBreakdown(
        PhaseTwoFurloughCalculationResult(
          1600.00,
          Seq(PhaseTwoFurloughBreakdown(Amount(1600.0), payment, FullPeriodCap(2500.00)))
        ),
        PhaseTwoNicCalculationResult(
          119.78,
          Seq(PhaseTwoNicBreakdown(Amount(119.78), payment, Threshold(732.0, TaxYearEnding2021, Monthly), Payable))
        ),
        PhaseTwoPensionCalculationResult(
          32.40,
          Seq(PhaseTwoPensionBreakdown(Amount(32.40), payment, Threshold(520.0, TaxYearEnding2021, Monthly), DoesContribute))
        )
      )

      status(result) mustEqual OK
      contentAsString(result) mustEqual phaseTwoView(breakdown, period("2020, 7, 1", "2020, 7, 31"), calculatorVersionConf)(
        dataRequest,
        messages,
        appConf).toString
    }

    "return OK and the JRSExtension view with calculations, for a GET for dates 1st to 31st March 2021 (80% Grant)" in new CalculatorVersionConfiguration {

      def userAnswers(): UserAnswers =
        emptyUserAnswers
          .withClaimPeriodStart("2020, 11, 1")
          .withClaimPeriodEnd("2020, 11, 30")
          .withFurloughStartDate("2020, 11, 15")
          .withFurloughStatus(FurloughOngoing)
          .withPaymentFrequency(Monthly)
          .withNiCategory()
          .withPensionStatus()
          .withPayMethod(Variable)
          .withFurloughInLastTaxYear(false)
          .withVariableLengthEmployed(EmployeeStarted.After1Feb2019)
          .withEmployeeStartDate("2020, 1, 31")
          .withPreviousFurloughedPeriodsAnswer(true)
          .withFirstFurloughDate("2020, 11, 10")
          .withPayDate(List("2020, 10, 31", "2020, 12, 1"))
          .withAnnualPayAmount(10000.00)
          .withPartTimeQuestion(PartTimeNo)
          .withOnPayrollBefore30thOct2020()

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers()))

      val employeeIncomeForPeriod: Amount   = Amount(10000.00)
      val maxMonthFurloughGrant: BigDecimal = 2500.00
      val claimStartDate                    = "2021, 3, 1"
      val claimEndDate                      = "2021, 3, 31"

      val payment: RegularPaymentWithPhaseTwoPeriod = {
        RegularPaymentWithPhaseTwoPeriod(
          regularPay = employeeIncomeForPeriod,
          referencePay = employeeIncomeForPeriod,
          phaseTwoPeriod = PhaseTwoPeriod(
            periodWithPaymentDate = fullPeriodWithPaymentDate(start = claimStartDate, end = claimEndDate, paymentDate = claimEndDate),
            actualHours = None,
            usualHours = None
          )
        )
      }

      val breakdown: ConfirmationViewBreakdownWithoutNicAndPension = {
        ConfirmationViewBreakdownWithoutNicAndPension(
          furlough = PhaseTwoFurloughCalculationResult(
            total = maxMonthFurloughGrant,
            periodBreakdowns = Seq(
              PhaseTwoFurloughBreakdown(grant = Amount(maxMonthFurloughGrant),
                                        paymentWithPeriod = payment,
                                        furloughCap = FullPeriodCap(maxMonthFurloughGrant))
            )
          )
        )
      }

      val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)
      val dataRequest                                  = DataRequest(request, userAnswers().id, userAnswers())

      val result: Future[Result] = controller.onPageLoad()(request)

      val expected: String = contentAsString(result)
      val actual: String = extensionView(
        cvb = breakdown,
        claimPeriod = period(start = claimStartDate, end = claimEndDate),
        version = calculatorVersionConf,
        isNewStarterType5 = false,
        EightyPercent
      )(dataRequest, messages, appConf).toString

      status(result) mustEqual OK
      actual must include(heading)

    }

    "return OK and the JRSMayExtension view with calculations, for a GET for dates 1st to 31st July 2021 (70% Grant)" in new CalculatorVersionConfiguration {

      val claimStartDate                  = "2021, 7, 1"
      val claimEndDate                    = "2021, 7, 31"
      val employeeIncomeForPeriod: Amount = Amount(10000.00)

      def userAnswers(): UserAnswers =
        mandatoryAnswersOnRegularMonthly
          .withClaimPeriodStart(claimStartDate)
          .withClaimPeriodEnd(claimEndDate)
          .withFurloughStartDate("2020-03-01")
          .withLastPayDate("2021-07-31")
          .withRegularPayAmount(employeeIncomeForPeriod.value)
          .withPayDate(List("2021-06-30", "2021-07-31"))

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers()))

      val maxMonthFurloughGrant: BigDecimal = 2500

      val payment: RegularPaymentWithPhaseTwoPeriod = {
        RegularPaymentWithPhaseTwoPeriod(
          regularPay = employeeIncomeForPeriod,
          referencePay = employeeIncomeForPeriod,
          phaseTwoPeriod = PhaseTwoPeriod(
            periodWithPaymentDate = fullPeriodWithPaymentDate(start = claimStartDate, end = claimEndDate, paymentDate = claimEndDate),
            actualHours = None,
            usualHours = None
          )
        )
      }

      val breakdown: ConfirmationViewBreakdownWithoutNicAndPension = {
        ConfirmationViewBreakdownWithoutNicAndPension(
          furlough = PhaseTwoFurloughCalculationResult(
            total = maxMonthFurloughGrant,
            periodBreakdowns = Seq(
              PhaseTwoFurloughBreakdown(grant = Amount(maxMonthFurloughGrant),
                                        paymentWithPeriod = payment,
                                        furloughCap = FullPeriodCap(maxMonthFurloughGrant))
            )
          )
        )
      }

      val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)
      val dataRequest                                  = DataRequest(request, userAnswers().id, userAnswers())

      val result: Future[Result] = controller.onPageLoad()(request)

      val actual: String = contentAsString(result)
      val expected: String = extensionView(
        cvb = breakdown,
        claimPeriod = period(start = claimStartDate, end = claimEndDate),
        version = calculatorVersionConf,
        isNewStarterType5 = false,
        SeventyPercent
      )(dataRequest, messages, appConf).toString

      status(result) mustEqual OK
      actual mustBe expected
    }

    "return OK and the JRSMayExtension view with calculations, for a GET for dates 1st to 31st August 2021 (60% Grant)" in new CalculatorVersionConfiguration {

      val claimStartDate                  = "2021, 8, 1"
      val claimEndDate                    = "2021, 8, 31"
      val employeeIncomeForPeriod: Amount = Amount(10000.00)

      def userAnswers(): UserAnswers =
        mandatoryAnswersOnRegularMonthly
          .withClaimPeriodStart(claimStartDate)
          .withClaimPeriodEnd(claimEndDate)
          .withFurloughStartDate("2020-03-01")
          .withLastPayDate("2021-08-31")
          .withRegularPayAmount(employeeIncomeForPeriod.value)
          .withPayDate(List("2021-07-31", "2021-08-31"))

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers()))

      val maxMonthFurloughGrant: BigDecimal = 2500

      val payment: RegularPaymentWithPhaseTwoPeriod = {
        RegularPaymentWithPhaseTwoPeriod(
          regularPay = employeeIncomeForPeriod,
          referencePay = employeeIncomeForPeriod,
          phaseTwoPeriod = PhaseTwoPeriod(
            periodWithPaymentDate = fullPeriodWithPaymentDate(start = claimStartDate, end = claimEndDate, paymentDate = claimEndDate),
            actualHours = None,
            usualHours = None
          )
        )
      }

      val breakdown: ConfirmationViewBreakdownWithoutNicAndPension = {
        ConfirmationViewBreakdownWithoutNicAndPension(
          furlough = PhaseTwoFurloughCalculationResult(
            total = maxMonthFurloughGrant,
            periodBreakdowns = Seq(
              PhaseTwoFurloughBreakdown(grant = Amount(maxMonthFurloughGrant),
                                        paymentWithPeriod = payment,
                                        furloughCap = FullPeriodCap(maxMonthFurloughGrant))
            )
          )
        )
      }

      val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)
      val dataRequest                                  = DataRequest(request, userAnswers().id, userAnswers())

      val result: Future[Result] = controller.onPageLoad()(request)

      val actual: String = contentAsString(result)
      val expected: String = extensionView(
        cvb = breakdown,
        claimPeriod = period(start = claimStartDate, end = claimEndDate),
        version = calculatorVersionConf,
        isNewStarterType5 = false,
        SixtyPercent
      )(dataRequest, messages, appConf).toString

      status(result) mustEqual OK
      actual mustBe expected
    }

    "return OK and the JRSMayExtension view with calculations, for a GET for dates 1st to 31st September 2021 (60% Grant)" in new CalculatorVersionConfiguration {

      val claimStartDate                  = "2021, 9, 1"
      val claimEndDate                    = "2021, 9, 30"
      val employeeIncomeForPeriod: Amount = Amount(10000.00)

      def userAnswers(): UserAnswers =
        mandatoryAnswersOnRegularMonthly
          .withClaimPeriodStart(claimStartDate)
          .withClaimPeriodEnd(claimEndDate)
          .withFurloughStartDate("2020-03-01")
          .withLastPayDate("2021-09-30")
          .withRegularPayAmount(employeeIncomeForPeriod.value)
          .withPayDate(List("2021-08-31", "2021-09-30"))

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(userAnswers()))

      val maxMonthFurloughGrant: BigDecimal = 2500

      val payment: RegularPaymentWithPhaseTwoPeriod = {
        RegularPaymentWithPhaseTwoPeriod(
          regularPay = employeeIncomeForPeriod,
          referencePay = employeeIncomeForPeriod,
          phaseTwoPeriod = PhaseTwoPeriod(
            periodWithPaymentDate = fullPeriodWithPaymentDate(start = claimStartDate, end = claimEndDate, paymentDate = claimEndDate),
            actualHours = None,
            usualHours = None
          )
        )
      }

      val breakdown: ConfirmationViewBreakdownWithoutNicAndPension = {
        ConfirmationViewBreakdownWithoutNicAndPension(
          furlough = PhaseTwoFurloughCalculationResult(
            total = maxMonthFurloughGrant,
            periodBreakdowns = Seq(
              PhaseTwoFurloughBreakdown(grant = Amount(maxMonthFurloughGrant),
                                        paymentWithPeriod = payment,
                                        furloughCap = FullPeriodCap(maxMonthFurloughGrant))
            )
          )
        )
      }

      val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)
      val dataRequest                                  = DataRequest(request, userAnswers().id, userAnswers())

      val result: Future[Result] = controller.onPageLoad()(request)

      val actual: String = contentAsString(result)
      val expected: String = extensionView(
        cvb = breakdown,
        claimPeriod = period(start = claimStartDate, end = claimEndDate),
        version = calculatorVersionConf,
        isNewStarterType5 = false,
        SixtyPercent
      )(dataRequest, messages, appConf).toString

      status(result) mustEqual OK
      actual mustBe expected
    }
  }
}
