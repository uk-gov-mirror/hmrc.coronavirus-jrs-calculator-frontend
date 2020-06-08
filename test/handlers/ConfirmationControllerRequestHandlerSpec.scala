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
import cats.scalatest.ValidatedValues
import models.NicCategory.{Nonpayable, Payable}
import models.PaymentFrequency.Monthly
import models.PensionStatus.{DoesContribute, DoesNotContribute}
import models.{Amount, FullPeriodCap, FurloughCalculationResult, NicCalculationResult, NicCap, PartialPeriodCap, PensionCalculationResult, TaxYearEnding2020, TaxYearEnding2021, UserAnswers}
import services.Threshold
import utils.CoreTestData
import viewmodels.{ConfirmationViewBreakdown, PhaseOneConfirmationDataResult, PhaseTwoConfirmationDataResult}

class ConfirmationControllerRequestHandlerSpec extends SpecBase with CoreTestData with ValidatedValues with CoreTestDataBuilder {

  "do all calculations given a set of userAnswers returning a breakdown of each" in new ConfirmationControllerRequestHandler {
    val furlough =
      FurloughCalculationResult(
        3200.00,
        Seq(
          fullPeriodFurloughBreakdown(
            1600.00,
            regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-20")),
            FullPeriodCap(2500.00)),
          fullPeriodFurloughBreakdown(
            1600.00,
            regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-20")),
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
          regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-20")),
          Threshold(719.0, TaxYearEnding2020, Monthly),
          NicCap(Amount(1600.0), Amount(121.58), Amount(220.80)),
          Payable
        ),
        fullPeriodNicBreakdown(
          119.78,
          0.0,
          0.0,
          regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-20")),
          Threshold(732.0, TaxYearEnding2021, Monthly),
          NicCap(Amount(1600.0), Amount(119.78), Amount(220.80)),
          Payable
        )
      )
    )

    val pension = PensionCalculationResult(
      65.04,
      Seq(
        fullPeriodPensionBreakdown(
          32.64,
          regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-20")),
          Threshold(512.0, TaxYearEnding2020, Monthly),
          512.0,
          DoesContribute
        ),
        fullPeriodPensionBreakdown(
          32.40,
          regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-20")),
          Threshold(520.0, TaxYearEnding2021, Monthly),
          520.0,
          DoesContribute
        )
      )
    )

    val expectedBreakdown = ConfirmationViewBreakdown(furlough, nic, pension)

    val expectedClaimPeriod = period("2020-03-01", "2020-04-30")

    (loadResultData(dummyUserAnswers).value.asInstanceOf[PhaseOneConfirmationDataResult]).confirmationViewBreakdown mustBe expectedBreakdown
    (loadResultData(dummyUserAnswers).value.asInstanceOf[PhaseOneConfirmationDataResult]).metaData.claimPeriod mustBe expectedClaimPeriod
  }

  "for a given user answer calculate furlough and empty results for ni and pension if do not apply" in new ConfirmationControllerRequestHandler {
    val userAnswers = dummyUserAnswers.withNiCategory(Nonpayable).withPensionStatus(DoesNotContribute)
    val confirmationViewBreakdown: ConfirmationViewBreakdown =
      (loadResultData(userAnswers).value.asInstanceOf[PhaseOneConfirmationDataResult]).confirmationViewBreakdown

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
          regularPaymentWithPartialPeriod(
            1016.13,
            3500.00,
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
          regularPaymentWithPartialPeriod(
            1016.13,
            3500.00,
            2483.87,
            partialPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 31", "2020, 3, 10", "2020, 3, 31", "2020, 3, 31")),
          Threshold(719.0, TaxYearEnding2020, Monthly),
          NicCap(Amount(1774.30), Amount(202.83), Amount(244.85)),
          Payable
        )
      )
    )

    val pension = PensionCalculationResult(
      42.32,
      Seq(
        partialPeriodPensionBreakdown(
          42.32,
          regularPaymentWithPartialPeriod(
            1016.13,
            3500.00,
            2483.87,
            partialPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 31", "2020, 3, 10", "2020, 3, 31", "2020, 3, 31")),
          Threshold(512.0, TaxYearEnding2020, Monthly),
          363.35,
          DoesContribute
        )
      )
    )

    val expected = ConfirmationViewBreakdown(furlough, nic, pension)

    (loadResultData(userAnswers).value.asInstanceOf[PhaseOneConfirmationDataResult]).confirmationViewBreakdown mustBe expected
  }

  "variable average partial period scenario" in new ConfirmationControllerRequestHandler {
    val userAnswers = variableAveragePartial

    val furlough = FurloughCalculationResult(
      1289.95,
      Seq(
        partialPeriodFurloughBreakdown(
          1289.95,
          averagePaymentWithPartialPeriod(
            280.0,
            1612.44,
            partialPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 31", "2020, 3, 5", "2020, 3, 31", "2020, 3, 31"),
            12960.0,
            period("2019-08-01", "2020-03-04")
          ),
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
          averagePaymentWithPartialPeriod(
            280.0,
            1612.44,
            partialPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 31", "2020, 3, 5", "2020, 3, 31", "2020, 3, 31"),
            12960.0,
            period("2019-08-01", "2020-03-04")
          ),
          Threshold(719.0, TaxYearEnding2020, Monthly),
          NicCap(Amount(1289.95), Amount(102.16), Amount(178.01)),
          Payable
        )
      )
    )

    val pension = PensionCalculationResult(
      25.29,
      Seq(
        partialPeriodPensionBreakdown(
          25.29,
          averagePaymentWithPartialPeriod(
            280.0,
            1612.44,
            partialPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 31", "2020, 3, 5", "2020, 3, 31", "2020, 3, 31"),
            12960.0,
            period("2019-08-01", "2020-03-04")
          ),
          Threshold(512.0, TaxYearEnding2020, Monthly),
          445.94,
          DoesContribute
        )
      )
    )

    val expected = ConfirmationViewBreakdown(furlough, nic, pension)

    (loadResultData(userAnswers).value.asInstanceOf[PhaseOneConfirmationDataResult]).confirmationViewBreakdown mustBe expected
  }

  "take into account all cylb payments for weekly frequency with partial period as first period" in new ConfirmationControllerRequestHandler {

    (loadResultData(manyPeriods).value.asInstanceOf[PhaseOneConfirmationDataResult]).confirmationViewBreakdown.furlough.total mustBe 2402.63
  }

  "do phase two calculation if claimStartDate is on or after 1 July 2020" in new ConfirmationControllerRequestHandler {
    loadResultData(phaseTwoJourney()).value mustBe a[PhaseTwoConfirmationDataResult]
  }

}
