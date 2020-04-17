/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import base.SpecBase
import models.Calculation.{FurloughCalculationResult, NicCalculationResult, PensionCalculationResult}
import models.{Amount, CalculationResult, PayPeriodBreakdown, PaymentDate, Period, PeriodWithPayDay, UserAnswers}
import play.api.libs.json.Json
import utils.CoreTestData
import viewmodels.ConfirmationViewBreakdown

class ConfirmationControllerRequestHandlerSpec extends SpecBase with CoreTestData {

  "do all calculations given a set of userAnswers" in new ConfirmationControllerRequestHandler {
    val userAnswers = Json.parse(userAnswersJson()).as[UserAnswers]

    def periodBreakdownOne(amount: Double) =
      PayPeriodBreakdown(
        amount,
        PeriodWithPayDay(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)), PaymentDate(LocalDate.of(2020, 3, 31))),
        Amount(2500.00))
    def periodBreakdownTwo(amount: Double) =
      PayPeriodBreakdown(
        amount,
        PeriodWithPayDay(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30)), PaymentDate(LocalDate.of(2020, 4, 20))),
        Amount(2500.00))
    val furlough = CalculationResult(FurloughCalculationResult, 3200.00, List(periodBreakdownOne(1600.00), periodBreakdownTwo(1600.00)))
    val nic = CalculationResult(NicCalculationResult, 241.36, List(periodBreakdownOne(121.58), periodBreakdownTwo(119.78)))
    val pension = CalculationResult(PensionCalculationResult, 65.07, List(periodBreakdownOne(32.67), periodBreakdownTwo(32.40)))

    val expected = ConfirmationViewBreakdown(furlough, nic, pension)

    loadResultData(userAnswers).get.confirmationViewBreakdown mustBe expected //TODO metadata to be tested
  }

  "for a given user answer calculate furlough and empty results for ni and pension if do not apply" in new ConfirmationControllerRequestHandler {
    val userAnswers = Json.parse(jsStringWithNoNiNoPension).as[UserAnswers]
    val withPayDay: PeriodWithPayDay =
      PeriodWithPayDay(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)), PaymentDate(LocalDate.of(2020, 3, 31)))
    val withPayDayTwo: PeriodWithPayDay =
      PeriodWithPayDay(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30)), PaymentDate(LocalDate.of(2020, 4, 20)))

    val payPeriodBreakdowns =
      List(PayPeriodBreakdown(1600.0, withPayDay, Amount(2500.00)), PayPeriodBreakdown(1600.0, withPayDayTwo, Amount(2500.00)))
    val nicPayPeriodBreakdowns =
      List(PayPeriodBreakdown(0.0, withPayDay, Amount(2500.00)), PayPeriodBreakdown(0.0, withPayDayTwo, Amount(2500.00)))
    val pensionPayPeriodBreakdowns =
      List(PayPeriodBreakdown(0.0, withPayDay, Amount(2500.00)), PayPeriodBreakdown(0.0, withPayDayTwo, Amount(2500.00)))

    loadResultData(userAnswers).get.confirmationViewBreakdown mustBe ConfirmationViewBreakdown(
      CalculationResult(FurloughCalculationResult, 3200.0, payPeriodBreakdowns),
      CalculationResult(NicCalculationResult, 0.0, nicPayPeriodBreakdowns),
      CalculationResult(PensionCalculationResult, 0.0, pensionPayPeriodBreakdowns)
    ) //TODO metadata to be tested
  }

}
