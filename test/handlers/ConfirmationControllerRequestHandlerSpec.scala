/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import base.SpecBase
import models.Calculation.{FurloughCalculationResult, NicCalculationResult, PensionCalculationResult}
import models.{Amount, CalculationResult, FullPeriod, PaymentDate, Period, PeriodBreakdown, PeriodWithPaymentDate, UserAnswers}
import play.api.libs.json.Json
import utils.CoreTestData
import viewmodels.ConfirmationViewBreakdown

class ConfirmationControllerRequestHandlerSpec extends SpecBase with CoreTestData {

  "do all calculations given a set of userAnswers" in new ConfirmationControllerRequestHandler {
    val userAnswers = Json.parse(userAnswersJson()).as[UserAnswers]

    def periodBreakdownOne(grossPay: BigDecimal, grant: BigDecimal) =
      PeriodBreakdown(
        Amount(grossPay),
        Amount(grant),
        PeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
          PaymentDate(LocalDate.of(2020, 3, 31)))
      )
    def periodBreakdownTwo(grossPay: BigDecimal, grant: BigDecimal) =
      PeriodBreakdown(
        Amount(grossPay),
        Amount(grant),
        PeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))),
          PaymentDate(LocalDate.of(2020, 4, 20)))
      )
    val furlough = CalculationResult(
      FurloughCalculationResult,
      3200.00,
      List(periodBreakdownOne(2000.00, 1600.00), periodBreakdownTwo(2000.00, 1600.00)))
    val nic =
      CalculationResult(NicCalculationResult, 241.36, List(periodBreakdownOne(2000.00, 121.58), periodBreakdownTwo(2000.00, 119.78)))
    val pension =
      CalculationResult(PensionCalculationResult, 65.07, List(periodBreakdownOne(2000.00, 32.67), periodBreakdownTwo(2000.00, 32.40)))

    val expected = ConfirmationViewBreakdown(furlough, nic, pension)

    loadResultData(userAnswers).get.confirmationViewBreakdown mustBe expected //TODO metadata to be tested
  }

  "for a given user answer calculate furlough and empty results for ni and pension if do not apply" in new ConfirmationControllerRequestHandler {
    val userAnswers = Json.parse(jsStringWithNoNiNoPension).as[UserAnswers]
    val withPayDay: PeriodWithPaymentDate =
      PeriodWithPaymentDate(FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))), PaymentDate(LocalDate.of(2020, 3, 31)))
    val withPayDayTwo: PeriodWithPaymentDate =
      PeriodWithPaymentDate(FullPeriod(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))), PaymentDate(LocalDate.of(2020, 4, 20)))

    val payPeriodBreakdowns =
      List(PeriodBreakdown(Amount(2000.00), Amount(1600.0), withPayDay), PeriodBreakdown(Amount(2000.00), Amount(1600.0), withPayDayTwo))
    val nicPayPeriodBreakdowns =
      List(PeriodBreakdown(Amount(2000.00), Amount(0.0), withPayDay), PeriodBreakdown(Amount(2000.00), Amount(0.0), withPayDayTwo))
    val pensionPayPeriodBreakdowns =
      List(PeriodBreakdown(Amount(2000.00), Amount(0.0), withPayDay), PeriodBreakdown(Amount(2000.00), Amount(0.0), withPayDayTwo))

    loadResultData(userAnswers).get.confirmationViewBreakdown mustBe ConfirmationViewBreakdown(
      CalculationResult(FurloughCalculationResult, 3200.0, payPeriodBreakdowns),
      CalculationResult(NicCalculationResult, 0.0, nicPayPeriodBreakdowns),
      CalculationResult(PensionCalculationResult, 0.0, pensionPayPeriodBreakdowns)
    ) //TODO metadata to be tested
  }

}
