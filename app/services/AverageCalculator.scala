/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.NonFurloughPay.determineNonFurloughPay
import models.PayQuestion.Varies
import models.{Amount, FullPeriodWithPaymentDate, NonFurloughPay, PartialPeriodWithPaymentDate, PaymentWithFullPeriod, PaymentWithPartialPeriod, PaymentWithPeriod, Period, PeriodWithPaymentDate}
import Calculators._

trait AverageCalculator extends PeriodHelper {

  def calculateAveragePay(
    nonFurloughPay: NonFurloughPay,
    priorFurloughPeriod: Period,
    afterFurloughPayPeriod: PeriodWithPaymentDate,
    amount: Amount): PaymentWithPeriod =
    afterFurloughPayPeriod match {
      case fp: FullPeriodWithPaymentDate =>
        val daily = periodDaysCount(fp.period.period) * averageDailyCalculator(priorFurloughPeriod, amount).value
        PaymentWithFullPeriod(Amount(daily), fp, Varies)
      case pp: PartialPeriodWithPaymentDate =>
        val nfp = determineNonFurloughPay(afterFurloughPayPeriod.period, nonFurloughPay)
        val daily = periodDaysCount(pp.period.partial) * averageDailyCalculator(priorFurloughPeriod, amount).value

        PaymentWithPartialPeriod(nfp, Amount(daily), pp, Varies)
    }

  protected def averageDailyCalculator(period: Period, amount: Amount): Amount =
    Amount(amount.value / periodDaysCount(period)).halfUp
}
