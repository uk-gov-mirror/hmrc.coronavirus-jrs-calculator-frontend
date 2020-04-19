/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.PayQuestion.Varies
import models.{Amount, FullPeriod, NonFurloughPay, PartialPeriod, PaymentWithPeriod, Period, Periods}
import utils.AmountRounding._

import scala.math.BigDecimal.RoundingMode._

trait ReferencePayCalculator extends PeriodHelper {

  def calculateVariablePay(
    nonFurloughPay: NonFurloughPay,
    priorFurloughPeriod: Period,
    afterFurloughPayPeriod: Seq[Periods],
    amount: Amount): Seq[PaymentWithPeriod] =
    afterFurloughPayPeriod.map(period => calculateReferencePay(nonFurloughPay, priorFurloughPeriod, period, amount))

  private def calculateReferencePay(
    nonFurloughPay: NonFurloughPay,
    priorFurloughPeriod: Period,
    afterFurloughPayPeriod: Periods,
    amount: Amount): PaymentWithPeriod = {

    val period = afterFurloughPayPeriod match {
      case FullPeriod(p)       => p
      case PartialPeriod(_, p) => p
    }

    val daily = periodDaysCount(period) * averageDailyCalculator(priorFurloughPeriod, amount)

    val nfp = afterFurloughPayPeriod match {
      case FullPeriod(p) => Amount(0.00)
      case pp @ PartialPeriod(_, _) =>
        if (isFurloughStart(pp)) nonFurloughPay.pre.fold(Amount(0.0))(v => v) else nonFurloughPay.post.fold(Amount(0.0))(v => v)
    }

    PaymentWithPeriod(nfp, Amount(daily), afterFurloughPayPeriod, Varies)
  }

  protected def averageDailyCalculator(period: Period, amount: Amount): BigDecimal =
    roundWithMode(amount.value / periodDaysCount(period), HALF_UP)
}
