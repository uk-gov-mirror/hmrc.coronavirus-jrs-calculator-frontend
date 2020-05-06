/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import models.PaymentFrequency.{Monthly, _}
import models.{CylbDuration, PaymentFrequency, PeriodWithPaymentDate}

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
