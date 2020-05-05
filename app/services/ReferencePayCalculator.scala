/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.{PaymentWithPeriod, ReferencePay, RegularPayData, VariablePayData, VariablePayWithCylbData}

trait ReferencePayCalculator extends RegularPayCalculator with AveragePayCalculator with CylbCalculator with Calculators {

  def calculateReferencePay(data: ReferencePay): Seq[PaymentWithPeriod] = data match {
    case rpd: RegularPayData  => calculateRegularPay(rpd.wage, rpd.referencePayData.periods)
    case vpd: VariablePayData => calculateAveragePay(vpd.nonFurloughPay, vpd.priorFurlough, vpd.periods, vpd.grossPay)
    case lbd: VariablePayWithCylbData => {
      val avg = calculateAveragePay(lbd.nonFurloughPay, lbd.priorFurlough, lbd.periods, lbd.grossPay)
      val cylb = calculateCylb(lbd.nonFurloughPay, lbd.frequency, lbd.cylbPayments, lbd.periods)

      takeGreaterGrossPay(cylb, avg)
    }
  }

  protected def takeGreaterGrossPay(cylb: Seq[PaymentWithPeriod], avg: Seq[PaymentWithPeriod]): Seq[PaymentWithPeriod] =
    cylb.zip(avg) map {
      case (cylbPayment, avgPayment) =>
        if (cylbPayment.furloughPayment.value > avgPayment.furloughPayment.value)
          cylbPayment
        else avgPayment
    }

}
