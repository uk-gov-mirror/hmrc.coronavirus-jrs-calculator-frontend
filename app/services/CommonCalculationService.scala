/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.{Amount, PaymentDate, PaymentFrequency}
import services.Calculators._
import utils.TaxYearFinder

trait CommonCalculationService extends TaxYearFinder {

  protected def greaterThanAllowance(amount: Amount, threshold: BigDecimal, rate: Rate): Amount =
    if (amount.value < threshold) Amount(0.0)
    else Amount((amount.value - threshold) * rate.value).halfUp

  protected def thresholdFinder(frequency: PaymentFrequency, paymentDate: PaymentDate, rate: Rate): BigDecimal =
    FrequencyTaxYearThresholdMapping.findThreshold(frequency, taxYearAt(paymentDate), rate)

}
