/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.{JourneyData, PaymentWithPeriod, RegularPayData, VariablePayData, VariablePayWithCylbData}

trait ReferencePayCalculator extends RegularPayCalculator with AveragePayCalculator with CylbCalculator with Calculators {

  def calculateReferencePay(journeyData: JourneyData): Seq[PaymentWithPeriod] = journeyData match {
    case rpd: RegularPayData  => calculateRegularPay(rpd.wage, rpd.core.periods)
    case vpd: VariablePayData => calculateAveragePay(vpd.nonFurloughPay, vpd.priorFurlough, vpd.core.periods, vpd.grossPay)
    case lbd: VariablePayWithCylbData => {
      val avg = calculateAveragePay(lbd.nonFurloughPay, lbd.priorFurlough, lbd.core.periods, lbd.grossPay)
      val cylb = calculateCylb(lbd.nonFurloughPay, lbd.core.frequency, lbd.cylbPayments, lbd.core.periods)

      takeGreaterGrossPay(cylb, avg)
    }
  }

//  def calculateVariablePay(
//    nonFurloughPay: NonFurloughPay,
//    priorFurloughPeriod: Period,
//    furloughPayPeriods: Seq[PeriodWithPaymentDate],
//    amount: Amount,
//    cylbs: Seq[CylbPayment],
//    frequency: PaymentFrequency): Seq[PaymentWithPeriod] = {
//    val avg: Seq[PaymentWithPeriod] =
//      furloughPayPeriods.map(period => calculateAveragePay(nonFurloughPay, priorFurloughPeriod, period, amount))
//
//    NonEmptyList
//      .fromList(cylbs.toList)
//      .fold(avg)(_ =>
//        takeGreaterGrossPay(calculateCylb(nonFurloughPay, frequency, cylbs, furloughPayPeriods, determineNonFurloughPay), avg))
//  }

  protected def takeGreaterGrossPay(cylb: Seq[PaymentWithPeriod], avg: Seq[PaymentWithPeriod]): Seq[PaymentWithPeriod] =
    cylb.zip(avg) map {
      case (cylbPayment, avgPayment) =>
        if (cylbPayment.furloughPayment.value > avgPayment.furloughPayment.value)
          cylbPayment
        else avgPayment
    }

}
