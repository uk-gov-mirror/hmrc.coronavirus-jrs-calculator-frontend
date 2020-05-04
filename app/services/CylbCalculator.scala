/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import models.NonFurloughPay.determineNonFurloughPay
import models.{Amount, CylbOperators, CylbPayment, FullPeriodWithPaymentDate, NonFurloughPay, PartialPeriodWithPaymentDate, PaymentFrequency, PaymentWithFullPeriod, PaymentWithPartialPeriod, PaymentWithPeriod, PeriodWithPaymentDate}
import services.Calculators.AmountRounding

trait CylbCalculator extends PreviousYearPeriod {

  def calculateCylb(
    nonFurloughPay: NonFurloughPay,
    frequency: PaymentFrequency,
    cylbs: Seq[CylbPayment],
    periods: Seq[PeriodWithPaymentDate]): Seq[PaymentWithPeriod] =
    for {
      period: PeriodWithPaymentDate <- periods
      datesRequired = previousYearPayDate(frequency, period)
      nfp = determineNonFurloughPay(period.period, nonFurloughPay)
    } yield cylbsAmount(frequency, period, datesRequired, nfp, cylbs)

  private def cylbsAmount(
    frequency: PaymentFrequency,
    period: PeriodWithPaymentDate,
    datesRequired: Seq[LocalDate],
    nfp: Amount,
    cylbs: Seq[CylbPayment]): PaymentWithPeriod = {
    val cylbOps = operators(frequency, period.period)
    val furlough: Amount = previousYearFurlough(datesRequired, cylbs, cylbOps)

    period match {
      case fp: FullPeriodWithPaymentDate    => PaymentWithFullPeriod(furlough, fp)
      case pp: PartialPeriodWithPaymentDate => PaymentWithPartialPeriod(nfp, furlough, pp)
    }
  }

  sealed trait PreviousYearAmount {
    def total: Amount
  }

  final case class OnePreviousAmount(amount: Amount, ops: CylbOperators) extends PreviousYearAmount {
    def total: Amount = ops match {
      case CylbOperators(div, 0, multiplier) => Amount((amount.value / div) * multiplier)
      case CylbOperators(div, multiplier, 0) => Amount((amount.value / div) * multiplier)
    }
  }

  final case class TwoPreviousAmounts(firstAmount: Amount, secondAmount: Amount, ops: CylbOperators) extends PreviousYearAmount {
    def total: Amount = ops match {
      case CylbOperators(divider, daysFromPrevious, daysFromCurrent) =>
        Amount(((firstAmount.value / divider) * daysFromPrevious) + ((secondAmount.value / divider) * daysFromCurrent))
    }
  }

  private def previousYearFurlough(datesRequired: Seq[LocalDate], cylbs: Seq[CylbPayment], ops: CylbOperators): Amount =
    (datesRequired.flatMap(date => cylbs.find(_.date == date)).map(_.amount) match {
      case x :: Nil      => OnePreviousAmount(x, ops)
      case x :: y :: Nil => TwoPreviousAmounts(x, y, ops)
    }).total.halfUp

}
