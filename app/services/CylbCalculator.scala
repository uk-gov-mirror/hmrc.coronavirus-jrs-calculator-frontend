/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import cats.data.NonEmptyList
import models.PayQuestion.Varies
import models.{Amount, CylbOperators, CylbPayment, FullPeriodWithPaymentDate, NonFurloughPay, PartialPeriodWithPaymentDate, PaymentFrequency, PaymentWithFullPeriod, PaymentWithPartialPeriod, PaymentWithPeriod, PeriodWithPaymentDate, Periods}
import Calculators._

trait CylbCalculator extends PreviousYearPeriod {

  def calculateCylb(
    nonFurloughPay: NonFurloughPay,
    frequency: PaymentFrequency,
    cylbs: Seq[CylbPayment],
    periods: Seq[PeriodWithPaymentDate],
    fn: (Periods, NonFurloughPay) => Amount
  ): Seq[PaymentWithPeriod] =
    for {
      period: PeriodWithPaymentDate <- periods
      datesRequired = previousYearPayDate(frequency, period)
      nfp = fn(period.period, nonFurloughPay)
    } yield cylbsAmount(frequency, period, datesRequired, nfp, cylbs)

  private def cylbsAmount(
    frequency: PaymentFrequency,
    period: PeriodWithPaymentDate,
    datesRequired: Seq[LocalDate],
    nfp: Amount,
    cylbs: Seq[CylbPayment]): PaymentWithPeriod = {
    val cylbOps = operators(frequency, period.period)
    val furlough: Amount = previousYearFurlough(cylbOps, previousPayments(datesRequired, cylbs).toList)

    period match {
      case fp: FullPeriodWithPaymentDate    => PaymentWithFullPeriod(furlough, fp, Varies)
      case pp: PartialPeriodWithPaymentDate => PaymentWithPartialPeriod(nfp, furlough, pp, Varies)
    }
  }

  private def previousYearFurlough(cylbOps: CylbOperators, previous: List[Amount]): Amount =
    (for {
      oneOrTwo <- NonEmptyList.fromList(previous)
      two      <- oneOrTwo.tail.headOption
    } yield two)
      .fold(totalOneToOne(previous.head, cylbOps))(two => totalTwoToOne(previous.head, two, cylbOps))
      .halfUp

  private def previousPayments(datesRequired: Seq[LocalDate], cylbs: Seq[CylbPayment]): Seq[Amount] =
    for {
      date     <- datesRequired
      previous <- cylbs.find(_.date == date)
    } yield previous.amount

  private def totalTwoToOne(payOne: Amount, payTwo: Amount, operator: CylbOperators): Amount = {
    import operator._
    Amount(((payOne.value / divider) * daysFromPrevious) + ((payTwo.value / divider) * daysFromCurrent))
  }

  private def totalOneToOne(pay: Amount, operator: CylbOperators): Amount =
    operator match {
      case CylbOperators(div, 0, multiplier) => Amount((pay.value / div) * multiplier)
      case CylbOperators(div, multiplier, 0) => Amount((pay.value / div) * multiplier)
    }

}
