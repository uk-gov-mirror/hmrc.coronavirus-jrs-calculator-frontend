/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.{PaymentFrequency, TaxYear, TaxYearEnding2020, TaxYearEnding2021}

case class FrequencyTaxYearKey(paymentFrequency: PaymentFrequency, taxYear: TaxYear, rate: Rate)
case class Threshold(lower: Double, upper: Double)

sealed trait Rate {
  val value: Double
}
case class NiRate(value: Double = 13.8 / 100) extends Rate
case class PensionRate(value: Double = 3.0 / 100) extends Rate

object FrequencyTaxYearThresholdMapping {
  //TODO: This map could be loaded from application.conf
  val mappings: Map[FrequencyTaxYearKey, Threshold] = Map(
    FrequencyTaxYearKey(Monthly, TaxYearEnding2020, NiRate())          -> Threshold(719.00, 2500.00),
    FrequencyTaxYearKey(Monthly, TaxYearEnding2021, NiRate())          -> Threshold(732.00, 2500.00),
    FrequencyTaxYearKey(FourWeekly, TaxYearEnding2020, NiRate())       -> Threshold(664.00, 2304.00),
    FrequencyTaxYearKey(FourWeekly, TaxYearEnding2021, NiRate())       -> Threshold(676.00, 2304.00),
    FrequencyTaxYearKey(FortNightly, TaxYearEnding2020, NiRate())      -> Threshold(332.00, 1152.00),
    FrequencyTaxYearKey(FortNightly, TaxYearEnding2021, NiRate())      -> Threshold(338.00, 1152.00),
    FrequencyTaxYearKey(Weekly, TaxYearEnding2020, NiRate())           -> Threshold(166.00, 576.00),
    FrequencyTaxYearKey(Weekly, TaxYearEnding2021, NiRate())           -> Threshold(169.00, 576.00),
    FrequencyTaxYearKey(Monthly, TaxYearEnding2020, PensionRate())     -> Threshold(511.00, 2500.00),
    FrequencyTaxYearKey(Monthly, TaxYearEnding2021, PensionRate())     -> Threshold(520.00, 2500.00),
    FrequencyTaxYearKey(FourWeekly, TaxYearEnding2020, PensionRate())  -> Threshold(472.00, 2304.00),
    FrequencyTaxYearKey(FourWeekly, TaxYearEnding2021, PensionRate())  -> Threshold(480.00, 2304.00),
    FrequencyTaxYearKey(FortNightly, TaxYearEnding2020, PensionRate()) -> Threshold(236.00, 1152.00),
    FrequencyTaxYearKey(FortNightly, TaxYearEnding2021, PensionRate()) -> Threshold(240.00, 1152.00),
    FrequencyTaxYearKey(Weekly, TaxYearEnding2020, PensionRate())      -> Threshold(118.00, 576.00),
    FrequencyTaxYearKey(Weekly, TaxYearEnding2021, PensionRate())      -> Threshold(120.00, 576.00)
  )
}
