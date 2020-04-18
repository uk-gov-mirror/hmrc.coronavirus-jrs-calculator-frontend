/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.Calculation.{NicCalculationResult, PensionCalculationResult}
import models.{Amount, CalculationResult, PaymentFrequency, PeriodBreakdown}
import play.api.Logger
import utils.TaxYearFinder
import utils.AmountRounding._

import scala.math.BigDecimal.RoundingMode

trait NicPensionCalculator extends TaxYearFinder with FurloughCapCalculator {

  def calculateGrant(paymentFrequency: PaymentFrequency, furloughPayment: Seq[PeriodBreakdown], rate: Rate): CalculationResult = {
    val paymentDateBreakdowns: Seq[PeriodBreakdown] =
      furloughPayment.map(payment => payment.copy(payment = Amount(calculate(paymentFrequency, payment, rate))))

    rate match {
      case NiRate(_) =>
        CalculationResult(NicCalculationResult, paymentDateBreakdowns.map(_.payment.value).sum, paymentDateBreakdowns)
      case PensionRate(_) =>
        CalculationResult(PensionCalculationResult, paymentDateBreakdowns.map(_.payment.value).sum, paymentDateBreakdowns)
    }
  }

  protected def calculate(paymentFrequency: PaymentFrequency, furloughPayment: PeriodBreakdown, rate: Rate): BigDecimal = {
    val frequencyTaxYearKey = FrequencyTaxYearKey(paymentFrequency, taxYearAt(furloughPayment.periodWithPaymentDate.paymentDate), rate)

    FrequencyTaxYearThresholdMapping.mappings
      .get(frequencyTaxYearKey)
      .fold {
        Logger.warn(s"Unable to find a threshold for $frequencyTaxYearKey")
        BigDecimal(0).setScale(2)
      } { threshold =>
        val roundedFurloughPayment = furloughPayment.payment.value.setScale(0, RoundingMode.DOWN)
        val cap = furloughCap(paymentFrequency, furloughPayment.periodWithPaymentDate.period).setScale(0, RoundingMode.DOWN)
        val cappedFurloughPayment = cap.min(roundedFurloughPayment)

        if (cappedFurloughPayment < threshold.lower) {
          BigDecimal(0).setScale(2)
        } else {
          roundWithMode((cappedFurloughPayment - threshold.lower) * frequencyTaxYearKey.rate.value, RoundingMode.HALF_UP)
        }
      }
  }
}
