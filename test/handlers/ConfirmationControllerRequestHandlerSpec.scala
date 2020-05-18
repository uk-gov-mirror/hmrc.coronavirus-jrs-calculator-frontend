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

import base.{CoreTestDataBuilder, SpecBase}
import models.NicCategory.Nonpayable
import models.PensionStatus.DoesNotContribute
import models.{FullPeriodCap, FurloughCalculationResult, NicCalculationResult, PartialPeriodCap, PensionCalculationResult, UserAnswers}
import utils.CoreTestData
import viewmodels.ConfirmationViewBreakdown

class ConfirmationControllerRequestHandlerSpec extends SpecBase with CoreTestData with CoreTestDataBuilder {

  "do all calculations given a set of userAnswers returning a breakdown of each" in new ConfirmationControllerRequestHandler {
    val furlough =
      FurloughCalculationResult(
        3200.00,
        Seq(
          fullPeriodFurloughBreakdown(
            1600.00,
            paymentWithFullPeriod(2000.00, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-20")),
            FullPeriodCap(2500.00)),
          fullPeriodFurloughBreakdown(
            1600.00,
            paymentWithFullPeriod(2000.00, fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-20")),
            FullPeriodCap(2500.00))
        )
      )

    val nic = NicCalculationResult(
      241.36,
      Seq(
        fullPeriodNicBreakdown(
          121.58,
          0.0,
          0.0,
          paymentWithFullPeriod(2000.00, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-20"))),
        fullPeriodNicBreakdown(
          119.78,
          0.0,
          0.0,
          paymentWithFullPeriod(2000.00, fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-20")))
      )
    )

    val pension = PensionCalculationResult(
      65.04,
      Seq(
        fullPeriodPensionBreakdown(
          32.64,
          paymentWithFullPeriod(2000.00, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-20"))),
        fullPeriodPensionBreakdown(
          32.40,
          paymentWithFullPeriod(2000.00, fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-20")))
      )
    )

    val expectedBreakdown = ConfirmationViewBreakdown(furlough, nic, pension)

    val expectedClaimPeriod = period("2020-03-01", "2020-04-30")

    loadResultData(dummyUserAnswers).get.confirmationViewBreakdown mustBe expectedBreakdown
    loadResultData(dummyUserAnswers).get.metaData.claimPeriod mustBe expectedClaimPeriod
  }

  "for a given user answer calculate furlough and empty results for ni and pension if do not apply" in new ConfirmationControllerRequestHandler {
    val userAnswers = dummyUserAnswers.withNiCategory(Nonpayable).withPensionStatus(DoesNotContribute)
    val confirmationViewBreakdown: ConfirmationViewBreakdown = loadResultData(userAnswers).get.confirmationViewBreakdown

    confirmationViewBreakdown.furlough.total mustBe 3200.0
    confirmationViewBreakdown.nic.total mustBe 0.0
    confirmationViewBreakdown.pension.total mustBe 0.0
  }

  "partial period scenario" in new ConfirmationControllerRequestHandler {
    val userAnswers: UserAnswers = mandatoryAnswersOnRegularMonthly
      .withFurloughStartDate("2020-03-10")
      .withRegularPayAmount(3500.0)

    val furlough = FurloughCalculationResult(
      1774.30,
      Seq(
        partialPeriodFurloughBreakdown(
          1774.30,
          paymentWithPartialPeriod(
            1016.13,
            2483.87,
            partialPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 31", "2020, 3, 10", "2020, 3, 31", "2020, 3, 31")),
          PartialPeriodCap(1774.30, 22, 3, 80.65)
        )
      )
    )

    val nic = NicCalculationResult(
      202.83,
      Seq(
        partialPeriodNicBreakdown(
          202.83,
          0.0,
          0.0,
          paymentWithPartialPeriod(
            1016.13,
            2483.87,
            partialPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 31", "2020, 3, 10", "2020, 3, 31", "2020, 3, 31"))
        )
      )
    )

    val pension = PensionCalculationResult(
      42.32,
      Seq(
        partialPeriodPensionBreakdown(
          42.32,
          paymentWithPartialPeriod(
            1016.13,
            2483.87,
            partialPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 31", "2020, 3, 10", "2020, 3, 31", "2020, 3, 31")))
      )
    )

    val expected = ConfirmationViewBreakdown(furlough, nic, pension)

    loadResultData(userAnswers).get.confirmationViewBreakdown mustBe expected
  }

  "variable average partial period scenario" in new ConfirmationControllerRequestHandler {
    val userAnswers = variableAveragePartial

    val furlough = FurloughCalculationResult(
      1289.95,
      Seq(
        partialPeriodFurloughBreakdown(
          1289.95,
          paymentWithPartialPeriod(
            280.0,
            1612.44,
            partialPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 31", "2020, 3, 5", "2020, 3, 31", "2020, 3, 31")),
          PartialPeriodCap(2177.55, 27, 3, 80.65)
        )
      )
    )

    val nic = NicCalculationResult(
      102.16,
      Seq(
        partialPeriodNicBreakdown(
          102.16,
          0.0,
          0.0,
          paymentWithPartialPeriod(
            280.0,
            1612.44,
            partialPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 31", "2020, 3, 5", "2020, 3, 31", "2020, 3, 31"))
        )
      )
    )

    val pension = PensionCalculationResult(
      25.29,
      Seq(
        partialPeriodPensionBreakdown(
          25.29,
          paymentWithPartialPeriod(
            280.0,
            1612.44,
            partialPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 31", "2020, 3, 5", "2020, 3, 31", "2020, 3, 31")))
      )
    )

    val expected = ConfirmationViewBreakdown(furlough, nic, pension)

    loadResultData(userAnswers).get.confirmationViewBreakdown mustBe expected
  }

  "take into account all cylb payments for weekly frequency with partial period as first period" in new ConfirmationControllerRequestHandler {

    loadResultData(manyPeriods).get.confirmationViewBreakdown.furlough.total mustBe 2402.63
  }

}
