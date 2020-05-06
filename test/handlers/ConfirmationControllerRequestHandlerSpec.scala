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

package handlers

import java.time.LocalDate

import base.{CoreTestDataBuilder, SpecBase}
import models.Calculation.{FurloughCalculationResult, NicCalculationResult, PensionCalculationResult}
import models.{Amount, CalculationResult, FullPeriod, FullPeriodBreakdown, FullPeriodWithPaymentDate, PartialPeriod, PartialPeriodBreakdown, PartialPeriodWithPaymentDate, PaymentDate, Period, UserAnswers}
import play.api.libs.json.Json
import utils.CoreTestData
import viewmodels.ConfirmationViewBreakdown

class ConfirmationControllerRequestHandlerSpec extends SpecBase with CoreTestData with CoreTestDataBuilder {

  "do all calculations given a set of userAnswers" in new ConfirmationControllerRequestHandler {
    val userAnswers = Json.parse(userAnswersJson()).as[UserAnswers]

    def periodBreakdownOne(grant: BigDecimal) =
      FullPeriodBreakdown(
        Amount(grant),
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
          PaymentDate(LocalDate.of(2020, 3, 20)))
      )

    def periodBreakdownTwo(grant: BigDecimal) =
      FullPeriodBreakdown(
        Amount(grant),
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))),
          PaymentDate(LocalDate.of(2020, 4, 20)))
      )

    val furlough =
      CalculationResult(FurloughCalculationResult, 3200.00, Seq(periodBreakdownOne(1600.00), periodBreakdownTwo(1600.00)))
    val nic =
      CalculationResult(NicCalculationResult, 241.36, List(periodBreakdownOne(121.58), periodBreakdownTwo(119.78)))
    val pension =
      CalculationResult(PensionCalculationResult, 65.04, List(periodBreakdownOne(32.64), periodBreakdownTwo(32.40)))

    val expected = ConfirmationViewBreakdown(furlough, nic, pension)

    loadResultData(userAnswers).get.confirmationViewBreakdown mustBe expected //TODO metadata to be tested
  }

  "for a given user answer calculate furlough and empty results for ni and pension if do not apply" in new ConfirmationControllerRequestHandler {
    val userAnswers = Json.parse(jsStringWithNoNiNoPension).as[UserAnswers]
    val withPayDay: FullPeriodWithPaymentDate =
      FullPeriodWithPaymentDate(
        FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
        PaymentDate(LocalDate.of(2020, 3, 20)))
    val withPayDayTwo: FullPeriodWithPaymentDate =
      FullPeriodWithPaymentDate(
        FullPeriod(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))),
        PaymentDate(LocalDate.of(2020, 4, 20)))

    val payPeriodBreakdowns =
      List(FullPeriodBreakdown(Amount(1600.0), withPayDay), FullPeriodBreakdown(Amount(1600.0), withPayDayTwo))
    val nicPayPeriodBreakdowns =
      List(FullPeriodBreakdown(Amount(0.0), withPayDay), FullPeriodBreakdown(Amount(0.0), withPayDayTwo))
    val pensionPayPeriodBreakdowns =
      List(FullPeriodBreakdown(Amount(0.0), withPayDay), FullPeriodBreakdown(Amount(0.0), withPayDayTwo))

    loadResultData(userAnswers).get.confirmationViewBreakdown mustBe ConfirmationViewBreakdown(
      CalculationResult(FurloughCalculationResult, 3200.0, payPeriodBreakdowns),
      CalculationResult(NicCalculationResult, 0.0, nicPayPeriodBreakdowns),
      CalculationResult(PensionCalculationResult, 0.0, pensionPayPeriodBreakdowns)
    ) //TODO metadata to be tested
  }

  "partial period scenario" in new ConfirmationControllerRequestHandler {
    val userAnswers = Json.parse(tempTest).as[UserAnswers]

    def periodBreakdownOne(grossPay: BigDecimal, grant: BigDecimal) =
      PartialPeriodBreakdown(
        Amount(grossPay),
        Amount(grant),
        PartialPeriodWithPaymentDate(
          PartialPeriod(
            Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)),
            Period(LocalDate.of(2020, 3, 10), LocalDate.of(2020, 3, 31))),
          PaymentDate(LocalDate.of(2020, 3, 31))
        )
      )
    val furlough = CalculationResult(FurloughCalculationResult, 1774.30, List(periodBreakdownOne(1016.13, 1774.30)))
    val nic =
      CalculationResult(NicCalculationResult, 202.83, List(periodBreakdownOne(1016.13, 202.83)))
    val pension =
      CalculationResult(PensionCalculationResult, 42.32, List(periodBreakdownOne(1016.13, 42.32)))

    val expected = ConfirmationViewBreakdown(furlough, nic, pension)

    loadResultData(userAnswers).get.confirmationViewBreakdown mustBe expected //TODO metadata to be tested
  }

  "variable average partial period scenario" in new ConfirmationControllerRequestHandler {
    val userAnswers = Json.parse(variableAveragePartial).as[UserAnswers]

    def periodBreakdownOne(grossPay: BigDecimal, grant: BigDecimal) =
      PartialPeriodBreakdown(
        Amount(grossPay),
        Amount(grant),
        PartialPeriodWithPaymentDate(
          PartialPeriod(
            Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)),
            Period(LocalDate.of(2020, 3, 5), LocalDate.of(2020, 3, 31))),
          PaymentDate(LocalDate.of(2020, 3, 31))
        )
      )

    val furlough = CalculationResult(FurloughCalculationResult, 1289.95, List(periodBreakdownOne(280.00, 1289.95)))
    val nic = CalculationResult(NicCalculationResult, 102.16, List(periodBreakdownOne(280.00, 102.16)))
    val pension = CalculationResult(PensionCalculationResult, 25.29, List(periodBreakdownOne(280.00, 25.29)))

    val expected = ConfirmationViewBreakdown(furlough, nic, pension)

    loadResultData(userAnswers).get.confirmationViewBreakdown mustBe expected

  }

  "take into account all cylb payments for weekly frequency with partial period as first period" in new ConfirmationControllerRequestHandler {
    val userAnswers = Json.parse(manyPeriods).as[UserAnswers]

    val expected = 2402.63

    loadResultData(userAnswers).get.confirmationViewBreakdown.furlough.total mustBe expected

  }

}
