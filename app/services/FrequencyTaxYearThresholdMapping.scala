package services

import models.PaymentFrequency.{FourWeekly, Monthly}
import models.{PaymentFrequency, TaxYear, TaxYearEnding2020, TaxYearEnding2021}

case class FrequencyTaxYearKey(paymentFrequency: PaymentFrequency, taxYear: TaxYear)
case class Threshold(lower: Double, upper: Double)

object FrequencyTaxYearThresholdMapping {
  val mappings: Map[FrequencyTaxYearKey, Threshold] = Map(
    FrequencyTaxYearKey(Monthly, TaxYearEnding2020)    -> Threshold(719.00, 2500.00),
    FrequencyTaxYearKey(Monthly, TaxYearEnding2021)    -> Threshold(732.00, 2500.00),
    FrequencyTaxYearKey(FourWeekly, TaxYearEnding2020) -> Threshold(664.00, 2304.00),
    FrequencyTaxYearKey(FourWeekly, TaxYearEnding2021) -> Threshold(676.00, 2304.00)
  )
}
