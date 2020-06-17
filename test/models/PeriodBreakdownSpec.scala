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

package models

import java.time.LocalDate

import base.{CoreTestDataBuilder, SpecBaseWithApplication}
import models.NicCategory.Payable
import models.PaymentFrequency.Monthly
import models.PensionStatus.DoesContribute
import org.scalatest.{MustMatchers, OptionValues}
import services.Threshold

class PeriodBreakdownSpec extends SpecBaseWithApplication with MustMatchers with OptionValues with CoreTestDataBuilder {

  "PhaseTwoFurloughBreakdown" must {
    "detect if furlough has been capped" in {
      val referencePay: Amount = Amount(5000.00)
      val grant: Amount = Amount(2500.00)
      val payment = RegularPaymentWithPhaseTwoPeriod(
        Amount(5000.0),
        referencePay,
        PhaseTwoPeriod(fullPeriodWithPaymentDate("2020, 7, 1", "2020, 7, 31", "2020, 7, 31"), None, None))
      val furloughCap = FullPeriodCap(2500.00)

      val breakdown = PhaseTwoFurloughBreakdown(grant, payment, furloughCap)

      breakdown.isCapped mustBe true
    }

    "accurately calculate furlough" in {
      val referencePay: Amount = Amount(1337.33)
      val grant: Amount = Amount(2500.00)
      val payment = RegularPaymentWithPhaseTwoPeriod(
        Amount(1337.33),
        referencePay,
        PhaseTwoPeriod(fullPeriodWithPaymentDate("2020, 7, 1", "2020, 7, 31", "2020, 7, 31"), None, None))
      val furloughCap = FullPeriodCap(2500.00)

      val breakdown = PhaseTwoFurloughBreakdown(grant, payment, furloughCap)

      breakdown.calculatedFurlough mustBe "1069.86"
    }
  }

  "PhaseTwoNicBreakdown" must {

    val referencePay: Amount = Amount(300.00)
    val grant: Amount = Amount(200.00)
    val threshold: Threshold = Threshold(732.00, TaxYearEnding2021, Monthly)
    val nicCategory: NicCategory = Payable
    val paymentDate: PaymentDate = PaymentDate(LocalDate.of(2020, 8, 1))

    "format messages correctly" when {

      "breakdown is a full period and full time" in {
        val period = fullPeriod("2020,07,1", "2020,07,31")
        val periodWithPaymentDate = FullPeriodWithPaymentDate(period, paymentDate)
        val regularPay = Amount(250.00)
        val phaseTwoPeriod = PhaseTwoPeriod(periodWithPaymentDate, None, None)
        val paymentWithPhaseTwoPeriod = RegularPaymentWithPhaseTwoPeriod(regularPay, referencePay, phaseTwoPeriod)

        val breakdown = PhaseTwoNicBreakdown(grant, paymentWithPhaseTwoPeriod, threshold, nicCategory)

        breakdown.thresholdMessage mustBe "Deduct £732.00 (National Insurance contribution threshold for 20/21 tax year)."
      }

      "breakdown is a partial period and full time" in {
        val original = period("2020,07,1", "2020,07,31")
        val partial = period("2020,07,15", "2020,07,31")
        val partialPeriod = PartialPeriod(original, partial)
        val periodWithPaymentDate = PartialPeriodWithPaymentDate(partialPeriod, paymentDate)
        val regularPay = Amount(250.00)
        val phaseTwoPeriod = PhaseTwoPeriod(periodWithPaymentDate, None, None)
        val paymentWithPhaseTwoPeriod = RegularPaymentWithPhaseTwoPeriod(regularPay, referencePay, phaseTwoPeriod)

        val breakdown = PhaseTwoNicBreakdown(grant, paymentWithPhaseTwoPeriod, threshold, nicCategory)

        breakdown.thresholdMessage mustBe "Deduct £732.00 (£732.00 National Insurance contribution threshold for 20/21 tax year, divide by 31 days in pay period and multiply by 17 furlough days)."
      }

      "breakdown is a full period and part time" in {
        val period = fullPeriod("2020,07,1", "2020,07,31")
        val periodWithPaymentDate = FullPeriodWithPaymentDate(period, paymentDate)
        val regularPay = Amount(250.00)
        val phaseTwoPeriod = PhaseTwoPeriod(periodWithPaymentDate, Some(Hours(10.0)), Some(Hours(20.0)))
        val paymentWithPhaseTwoPeriod = RegularPaymentWithPhaseTwoPeriod(regularPay, referencePay, phaseTwoPeriod)

        val breakdown = PhaseTwoNicBreakdown(grant, paymentWithPhaseTwoPeriod, threshold, nicCategory)

        breakdown.thresholdMessage mustBe "Deduct £732.00 (£732.00 National Insurance contribution threshold for 20/21 tax year, divide by 20.00 usual hours, multiply by 10.00 furlough hours)."
      }

      "breakdown is a partial period and part time" in {
        val original = period("2020,07,1", "2020,07,31")
        val partial = period("2020,07,15", "2020,07,31")
        val partialPeriod = PartialPeriod(original, partial)
        val periodWithPaymentDate = PartialPeriodWithPaymentDate(partialPeriod, paymentDate)
        val regularPay = Amount(250.00)
        val phaseTwoPeriod = PhaseTwoPeriod(periodWithPaymentDate, Some(Hours(10.0)), Some(Hours(20.0)))
        val paymentWithPhaseTwoPeriod = RegularPaymentWithPhaseTwoPeriod(regularPay, referencePay, phaseTwoPeriod)

        val breakdown = PhaseTwoNicBreakdown(grant, paymentWithPhaseTwoPeriod, threshold, nicCategory)

        breakdown.thresholdMessage mustBe "Deduct £732.00 (£732.00 National Insurance contribution threshold for 20/21 tax year, divide by 31 days in pay period and multiply by 17 furlough days, divide by 20.00 usual hours, multiply by 10.00 furlough hours)."
      }

    }

  }

  "PhaseTwoPensionBreakdown" must {

    val referencePay: Amount = Amount(300.00)
    val grant: Amount = Amount(200.00)
    val threshold: Threshold = Threshold(500.00, TaxYearEnding2021, Monthly)
    val pensionStatus: PensionStatus = DoesContribute
    val paymentDate: PaymentDate = PaymentDate(LocalDate.of(2020, 8, 1))

    "format messages correctly" when {

      "breakdown is a full period and full time" in {
        val period = fullPeriod("2020,07,1", "2020,07,31")
        val periodWithPaymentDate = FullPeriodWithPaymentDate(period, paymentDate)
        val regularPay = Amount(250.00)
        val phaseTwoPeriod = PhaseTwoPeriod(periodWithPaymentDate, None, None)
        val paymentWithPhaseTwoPeriod = RegularPaymentWithPhaseTwoPeriod(regularPay, referencePay, phaseTwoPeriod)

        val breakdown = PhaseTwoPensionBreakdown(grant, paymentWithPhaseTwoPeriod, threshold, pensionStatus)

        breakdown.thresholdMessage mustBe "Deduct £500.00 (Lower Level of Qualifying Earnings for 20/21 tax year)."
      }

      "breakdown is a partial period and full time" in {
        val original = period("2020,07,1", "2020,07,31")
        val partial = period("2020,07,15", "2020,07,31")
        val partialPeriod = PartialPeriod(original, partial)
        val periodWithPaymentDate = PartialPeriodWithPaymentDate(partialPeriod, paymentDate)
        val regularPay = Amount(250.00)
        val phaseTwoPeriod = PhaseTwoPeriod(periodWithPaymentDate, None, None)
        val paymentWithPhaseTwoPeriod = RegularPaymentWithPhaseTwoPeriod(regularPay, referencePay, phaseTwoPeriod)

        val breakdown = PhaseTwoPensionBreakdown(grant, paymentWithPhaseTwoPeriod, threshold, pensionStatus)

        breakdown.thresholdMessage mustBe "Deduct £500.00 (£520.00 Lower Level of Qualifying Earnings for 20/21 tax year, divide by 31 days in pay period and multiply by 17 furlough days)."
      }

      "breakdown is a full period and part time" in {
        val period = fullPeriod("2020,07,1", "2020,07,31")
        val periodWithPaymentDate = FullPeriodWithPaymentDate(period, paymentDate)
        val regularPay = Amount(250.00)
        val phaseTwoPeriod = PhaseTwoPeriod(periodWithPaymentDate, Some(Hours(10.0)), Some(Hours(20.0)))
        val paymentWithPhaseTwoPeriod = RegularPaymentWithPhaseTwoPeriod(regularPay, referencePay, phaseTwoPeriod)

        val breakdown = PhaseTwoPensionBreakdown(grant, paymentWithPhaseTwoPeriod, threshold, pensionStatus)

        breakdown.thresholdMessage mustBe "Deduct £500.00 (£520.00 Lower Level of Qualifying Earnings for 20/21 tax year, divide by 20.00 usual hours, multiply by 10.00 furlough hours)."
      }

      "breakdown is a partial period and part time" in {
        val original = period("2020,07,1", "2020,07,31")
        val partial = period("2020,07,15", "2020,07,31")
        val partialPeriod = PartialPeriod(original, partial)
        val periodWithPaymentDate = PartialPeriodWithPaymentDate(partialPeriod, paymentDate)
        val regularPay = Amount(250.00)
        val phaseTwoPeriod = PhaseTwoPeriod(periodWithPaymentDate, Some(Hours(10.0)), Some(Hours(20.0)))
        val paymentWithPhaseTwoPeriod = RegularPaymentWithPhaseTwoPeriod(regularPay, referencePay, phaseTwoPeriod)

        val breakdown = PhaseTwoPensionBreakdown(grant, paymentWithPhaseTwoPeriod, threshold, pensionStatus)

        breakdown.thresholdMessage mustBe "Deduct £500.00 (£520.00 Lower Level of Qualifying Earnings for 20/21 tax year, divide by 31 days in pay period and multiply by 17 furlough days, divide by 20.00 usual hours, multiply by 10.00 furlough hours)."
      }

    }

  }

}
