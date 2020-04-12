/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.{PaymentFrequency, TaxYear, TaxYearEnding2020, TaxYearEnding2021}

case class FrequencyTaxYearKey(paymentFrequency: PaymentFrequency, taxYear: TaxYear)
case class Threshold(lower: Double, upper: Double)

object FrequencyTaxYearThresholdMapping {
  //TODO: This map could be loaded from application.conf
  val mappings: Map[FrequencyTaxYearKey, Threshold] = Map(
    FrequencyTaxYearKey(Monthly, TaxYearEnding2020)     -> Threshold(719.00, 2500.00),
    FrequencyTaxYearKey(Monthly, TaxYearEnding2021)     -> Threshold(732.00, 2500.00),
    FrequencyTaxYearKey(FourWeekly, TaxYearEnding2020)  -> Threshold(664.00, 2304.00),
    FrequencyTaxYearKey(FourWeekly, TaxYearEnding2021)  -> Threshold(676.00, 2304.00),
    FrequencyTaxYearKey(FortNightly, TaxYearEnding2020) -> Threshold(332.00, 1152.00),
    FrequencyTaxYearKey(FortNightly, TaxYearEnding2021) -> Threshold(338.00, 1152.00),
    FrequencyTaxYearKey(Weekly, TaxYearEnding2020)      -> Threshold(166.00, 576.00),
    FrequencyTaxYearKey(Weekly, TaxYearEnding2021)      -> Threshold(169.00, 576.00)
  )
}
