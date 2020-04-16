/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.Calculation.{NicCalculationResult, PensionCalculationResult}
import models.{CalculationResult, PayPeriodBreakdown, PaymentFrequency}
import play.api.Logger
import utils.TaxYearFinder

import scala.math.BigDecimal.RoundingMode

trait NicPensionCalculator extends TaxYearFinder with FurloughCapCalculator {

  def calculateGrant(paymentFrequency: PaymentFrequency, furloughPayment: Seq[PayPeriodBreakdown], rate: Rate): CalculationResult = {
    val paymentDateBreakdowns: Seq[PayPeriodBreakdown] =
      furloughPayment.map(payment => PayPeriodBreakdown(calculate(paymentFrequency, payment, rate), payment.payPeriodWithPayDay))

    rate match {
      case NiRate(_)      => CalculationResult(NicCalculationResult, paymentDateBreakdowns.map(_.amount).sum, paymentDateBreakdowns)
      case PensionRate(_) => CalculationResult(PensionCalculationResult, paymentDateBreakdowns.map(_.amount).sum, paymentDateBreakdowns)
    }
  }

  protected def calculate(paymentFrequency: PaymentFrequency, furloughPayment: PayPeriodBreakdown, rate: Rate): BigDecimal = {
    val frequencyTaxYearKey = FrequencyTaxYearKey(paymentFrequency, taxYearAt(furloughPayment.payPeriodWithPayDay.paymentDate), rate)

    FrequencyTaxYearThresholdMapping.mappings
      .get(frequencyTaxYearKey)
      .fold {
        Logger.warn(s"Unable to find a threshold for $frequencyTaxYearKey")
        BigDecimal(0).setScale(2)
      } { threshold =>
        val cap = furloughCap(paymentFrequency, furloughPayment.payPeriodWithPayDay.payPeriod).setScale(0, RoundingMode.DOWN) //Remove the pennies
        val cappedFurloughPayment = cap.min(furloughPayment.amount)

        if (cappedFurloughPayment < threshold.lower) {
          BigDecimal(0).setScale(2)
        } else {
          roundWithMode((cappedFurloughPayment - threshold.lower) * frequencyTaxYearKey.rate.value, RoundingMode.HALF_UP)
        }
      }
  }
}
