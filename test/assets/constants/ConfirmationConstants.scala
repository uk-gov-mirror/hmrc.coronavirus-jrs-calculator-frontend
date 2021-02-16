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

package assets.constants

import java.time.LocalDate

import base.CoreTestDataBuilder
import models.NicCategory.Payable
import models.PaymentFrequency._
import models.PensionStatus.DoesContribute
import models._
import play.api.i18n.Messages
import services.Threshold
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import viewmodels.{ConfirmationMetadata, ConfirmationViewBreakdown}

object ConfirmationConstants extends CoreTestDataBuilder {

  lazy val furlough: FurloughCalculationResult = {
    FurloughCalculationResult(
      total = 3200.00,
      periodBreakdowns = Seq(
        fullPeriodFurloughBreakdown(
          grant = 1600.00,
          payment = regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-20")),
          cap = FullPeriodCap(2500.00)
        ),
        fullPeriodFurloughBreakdown(
          grant = 1600.00,
          payment = regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-20")),
          cap = FullPeriodCap(2500.00)
        )
      )
    )
  }

  lazy val nic = NicCalculationResult(
    total = 241.36,
    periodBreakdowns = Seq(
      fullPeriodNicBreakdown(
        grant = 121.58,
        topUp = 0.0,
        additional = 0.0,
        payment = regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-20")),
        threshold = Threshold(719.0, TaxYearEnding2020, Monthly),
        nicCap = NicCap(Amount(1600.0), Amount(121.58), Amount(200.80)),
        nicCategory = Payable
      ),
      fullPeriodNicBreakdown(
        grant = 119.78,
        topUp = 0.0,
        additional = 0.0,
        payment = regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-20")),
        threshold = Threshold(732.0, TaxYearEnding2021, Monthly),
        nicCap = NicCap(Amount(1600.00), Amount(119.78), Amount(220.80)),
        nicCategory = Payable
      )
    )
  )

  lazy val pension = PensionCalculationResult(
    total = 65.04,
    periodBreakdowns = Seq(
      fullPeriodPensionBreakdown(
        grant = 32.64,
        payment = regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-20")),
        threshold = Threshold(512.0, TaxYearEnding2020, Monthly),
        allowance = 512.0,
        pensionStatus = DoesContribute
      ),
      fullPeriodPensionBreakdown(
        grant = 32.40,
        payment = regularPaymentWithFullPeriod(2000.00, 2000.00, fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-20")),
        threshold = Threshold(520.0, TaxYearEnding2021, Monthly),
        allowance = 520.0,
        pensionStatus = DoesContribute
      )
    )
  )

  lazy val breakdown: ConfirmationViewBreakdown = ConfirmationViewBreakdown(furlough, nic, pension)

  val furloughPeriod: FurloughOngoing = FurloughOngoing(start = LocalDate.of(2020, 3, 1))

  val meta: ConfirmationMetadata = {
    ConfirmationMetadata(
      Period(
        start = LocalDate.of(2020, 3, 1),
        end = LocalDate.of(2020, 4, 30)
      ),
      furloughDates = furloughPeriod,
      frequency = Monthly,
      nic = Payable,
      pension = DoesContribute
    )
  }

}
