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

package services

import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.{PaymentFrequency, TaxYear, TaxYearEnding2020, TaxYearEnding2021}
import play.api.Logger

case class FrequencyTaxYearKey(paymentFrequency: PaymentFrequency, taxYear: TaxYear, rate: Rate)
case class Threshold(lower: BigDecimal)

sealed trait Rate {
  val value: BigDecimal
}
case class NiRate(value: BigDecimal = 13.8 / 100) extends Rate
case class PensionRate(value: BigDecimal = 3.0 / 100) extends Rate

object FrequencyTaxYearThresholdMapping {
  //TODO: This map could be loaded from application.conf
  val mappings: Map[FrequencyTaxYearKey, Threshold] = Map(
    FrequencyTaxYearKey(Monthly, TaxYearEnding2020, NiRate())          -> Threshold(719.00),
    FrequencyTaxYearKey(Monthly, TaxYearEnding2021, NiRate())          -> Threshold(732.00),
    FrequencyTaxYearKey(FourWeekly, TaxYearEnding2020, NiRate())       -> Threshold(664.00),
    FrequencyTaxYearKey(FourWeekly, TaxYearEnding2021, NiRate())       -> Threshold(676.00),
    FrequencyTaxYearKey(FortNightly, TaxYearEnding2020, NiRate())      -> Threshold(332.00),
    FrequencyTaxYearKey(FortNightly, TaxYearEnding2021, NiRate())      -> Threshold(338.00),
    FrequencyTaxYearKey(Weekly, TaxYearEnding2020, NiRate())           -> Threshold(166.00),
    FrequencyTaxYearKey(Weekly, TaxYearEnding2021, NiRate())           -> Threshold(169.00),
    FrequencyTaxYearKey(Monthly, TaxYearEnding2020, PensionRate())     -> Threshold(512.00),
    FrequencyTaxYearKey(Monthly, TaxYearEnding2021, PensionRate())     -> Threshold(520.00),
    FrequencyTaxYearKey(FourWeekly, TaxYearEnding2020, PensionRate())  -> Threshold(472.00),
    FrequencyTaxYearKey(FourWeekly, TaxYearEnding2021, PensionRate())  -> Threshold(480.00),
    FrequencyTaxYearKey(FortNightly, TaxYearEnding2020, PensionRate()) -> Threshold(236.00),
    FrequencyTaxYearKey(FortNightly, TaxYearEnding2021, PensionRate()) -> Threshold(240.00),
    FrequencyTaxYearKey(Weekly, TaxYearEnding2020, PensionRate())      -> Threshold(118.00),
    FrequencyTaxYearKey(Weekly, TaxYearEnding2021, PensionRate())      -> Threshold(120.00)
  )

  def findThreshold(frequency: PaymentFrequency, taxYear: TaxYear, rate: Rate): BigDecimal = {
    val frequencyTaxYearKey = FrequencyTaxYearKey(frequency, taxYear, rate)
    FrequencyTaxYearThresholdMapping.mappings
      .get(frequencyTaxYearKey)
      .fold {
        Logger.warn(s"Unable to find a threshold for $frequencyTaxYearKey")
        BigDecimal(0).setScale(2)
      } { threshold =>
        threshold.lower
      }
  }
}
