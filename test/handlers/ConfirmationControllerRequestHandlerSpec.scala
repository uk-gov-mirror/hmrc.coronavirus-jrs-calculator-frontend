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
import models.NicCategory.{Nonpayable, Payable}
import models.PaymentFrequency.Monthly
import models.PensionStatus.{DoesContribute, DoesNotContribute}
import models.{AdditionalPayment, CalculationResult, FurloughOngoing, Period, TopUpPayment, UserAnswers}
import utils.CoreTestData
import viewmodels.{ConfirmationMetadata, ConfirmationViewBreakdown}
import models.Amount._

class ConfirmationControllerRequestHandlerSpec extends SpecBase with CoreTestData with CoreTestDataBuilder {

  "do all calculations given a set of userAnswers returning a breakdown of each" in new ConfirmationControllerRequestHandler {
    val userAnswers: UserAnswers = dummyUserAnswers

    def periodBreakdownOne(grant: BigDecimal) =
      fullPeriodBreakdown(grant, fullPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 31", "2020, 3, 20"))

    def periodBreakdownTwo(grant: BigDecimal) =
      fullPeriodBreakdown(grant, fullPeriodWithPaymentDate("2020, 4, 1", "2020, 4, 30", "2020, 4, 20"))
    val furlough =
      CalculationResult(FurloughCalculationResult, 3200.00, Seq(periodBreakdownOne(1600.00), periodBreakdownTwo(1600.00)))
    val nic =
      CalculationResult(NicCalculationResult, 241.36, List(periodBreakdownOne(121.58), periodBreakdownTwo(119.78)))

    val pension =
      CalculationResult(PensionCalculationResult, 65.04, List(periodBreakdownOne(32.64), periodBreakdownTwo(32.40)))

    loadResultData(userAnswers).get.confirmationViewBreakdown mustBe ConfirmationViewBreakdown(furlough, nic, pension)
    loadResultData(userAnswers).get.confirmationMetadata must matchPattern {
      case ConfirmationMetadata(Period(_, _), FurloughOngoing(_), Monthly, Payable, DoesContribute) =>
    }
  }

  "for a given user answer calculate furlough and empty results for ni and pension if do not apply" in new ConfirmationControllerRequestHandler {
    val userAnswers = dummyUserAnswers.withNiCategory(Nonpayable).withPensionStatus(DoesNotContribute)
    val confirmationViewBreakdown: ConfirmationViewBreakdown = loadResultData(userAnswers).get.confirmationViewBreakdown

    confirmationViewBreakdown.furlough.total mustBe 3200.0
    confirmationViewBreakdown.nic.total mustBe 0.0
    confirmationViewBreakdown.pension.total mustBe 0.0
  }

  "partial period scenario including topup and additional payment" in new ConfirmationControllerRequestHandler {
    val userAnswers: UserAnswers = mandatoryAnswersOnRegularMonthly
      .withFurloughStartDate("2020-03-10")
      .withRegularPayAmount(3500)
      .withToppedUpStatus()
      .withTopUpAmount(TopUpPayment(LocalDate.of(2020, 3, 31), 100.0.toAmount), Some(1))
      .withAdditionalPaymentAmount(AdditionalPayment(LocalDate.of(2020, 3, 31), 50.0.toAmount), Some(1))
      .withAdditionalPaymentPeriods(List("2020, 3, 31"))

    def periodBreakdown(grossPay: BigDecimal, grant: BigDecimal) =
      partialPeriodBreakdown(
        grossPay,
        grant,
        partialPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 31", "2020, 3, 10", "2020, 3, 31", "2020, 3, 31"))

    val furlough = CalculationResult(FurloughCalculationResult, 1774.30, List(periodBreakdown(1016.13, 1774.30)))
    val nic =
      CalculationResult(NicCalculationResult, 205.91, List(periodBreakdown(1016.13, 205.91)))
    val pension =
      CalculationResult(PensionCalculationResult, 42.32, List(periodBreakdown(1016.13, 42.32)))

    val expected = ConfirmationViewBreakdown(furlough, nic, pension)

    loadResultData(userAnswers).get.confirmationViewBreakdown mustBe expected
  }

  "variable average partial period scenario" in new ConfirmationControllerRequestHandler {
    def periodBreakdownOne(grossPay: BigDecimal, grant: BigDecimal) =
      partialPeriodBreakdown(
        grossPay,
        grant,
        partialPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 31", "2020, 3, 5", "2020, 3, 31", "2020, 3, 31"))

    val furlough = CalculationResult(FurloughCalculationResult, 1289.95, List(periodBreakdownOne(280.00, 1289.95)))
    val nic = CalculationResult(NicCalculationResult, 102.16, List(periodBreakdownOne(280.00, 102.16)))
    val pension = CalculationResult(PensionCalculationResult, 25.29, List(periodBreakdownOne(280.00, 25.29)))

    val expected = ConfirmationViewBreakdown(furlough, nic, pension)

    loadResultData(variableAveragePartial).get.confirmationViewBreakdown mustBe expected
  }

  "take into account all cylb payments for weekly frequency with partial period as first period" in new ConfirmationControllerRequestHandler {

    loadResultData(manyPeriods).get.confirmationViewBreakdown.furlough.total mustBe 2402.63
  }

}
