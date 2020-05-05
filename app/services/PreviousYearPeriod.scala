/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.{CylbDuration, CylbOperators, FullPeriod, PartialPeriod, PaymentFrequency, PeriodWithPaymentDate, Periods}
import PaymentFrequency._

trait PreviousYearPeriod extends PeriodHelper {

  def previousYearPayDate(paymentFrequency: PaymentFrequency, withPaymentDate: PeriodWithPaymentDate): Seq[LocalDate] = {
    val cylbDuration = CylbDuration(paymentFrequency, withPaymentDate.period)

    (cylbDuration.previousPeriodDays, cylbDuration.equivalentPeriodDays) match {
      case (0, _) => Seq(lastYear(paymentFrequency, withPaymentDate.paymentDate.value))
      case (_, 0) =>
        Seq(lastYear(paymentFrequency, withPaymentDate.paymentDate.value).minusDays(paymentFrequencyDays(paymentFrequency)))
      case _ => calculateDatesForPreviousYear(paymentFrequency, withPaymentDate.paymentDate.value)
    }
  }

  //TODO remove this
  def operators(paymentFrequency: PaymentFrequency, period: Periods): CylbOperators =
    (paymentFrequency, period) match {
      case (Monthly, FullPeriod(p))                 => CylbOperators(periodDaysCount(p), 0, periodDaysCount(p))
      case (Monthly, PartialPeriod(o, p))           => CylbOperators(periodDaysCount(o), 0, periodDaysCount(p))
      case (Weekly, _: FullPeriod)                  => CylbOperators(7, 2, 5)
      case (f: Weekly.type, pp: PartialPeriod)      => handlePartial(f, pp)
      case (FortNightly, _: FullPeriod)             => CylbOperators(14, 2, 12)
      case (f: FortNightly.type, pp: PartialPeriod) => handlePartial(f, pp)
      case (FourWeekly, _: FullPeriod)              => CylbOperators(28, 2, 26)
      case (f: FourWeekly.type, pp: PartialPeriod)  => handlePartial(f, pp)
    }

  private val predicateStart: PartialPeriod => Boolean =
    pp => periodDaysCount(pp.partial) < (periodDaysCount(pp.original) - 1)

  private def handlePartial(frequency: PaymentFrequency, p: PartialPeriod): CylbOperators =
    if (isFurloughStart(p))
      handleFurloughStart(frequency, p)
    else
      CylbOperators(paymentFrequencyDays(frequency), 2, periodDaysCount(p.partial) - 2)

  private def handleFurloughStart(frequency: PaymentFrequency, p: PartialPeriod): CylbOperators =
    if (predicateStart(p))
      CylbOperators(paymentFrequencyDays(frequency), 0, periodDaysCount(p.partial))
    else
      CylbOperators(paymentFrequencyDays(frequency), 1, periodDaysCount(p.partial) - 1)

  private def calculateDatesForPreviousYear(paymentFrequency: PaymentFrequency, payDateThisYear: LocalDate): Seq[LocalDate] = {
    val payDateTwo = lastYear(paymentFrequency, payDateThisYear)
    val payDateOne = payDateTwo.minusDays(paymentFrequencyDays(paymentFrequency))

    Seq(payDateOne, payDateTwo)
  }

  private def lastYear(paymentFrequency: PaymentFrequency, payDateThisYear: LocalDate): LocalDate = paymentFrequency match {
    case Monthly => payDateThisYear.minusYears(1)
    case _ =>
      val date = payDateThisYear.minusDays(364)
      if (date.isBefore(LocalDate.of(2019, 3, 1))) date.plusDays(1) else date
  }
}
