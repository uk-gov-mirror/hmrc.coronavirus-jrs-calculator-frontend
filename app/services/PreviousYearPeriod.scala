/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import models.PaymentFrequency
import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}

trait PreviousYearPeriod {

  def previousYearPayDate(paymentFrequency: PaymentFrequency, payDateThisYear: LocalDate): Seq[LocalDate] = paymentFrequency match {
    case Monthly => Seq(payDateThisYear.minusYears(1))
    case _       => calculateDatesForPreviousYear(paymentFrequency, payDateThisYear)
  }

  private def calculateDatesForPreviousYear(paymentFrequency: PaymentFrequency, payDateThisYear: LocalDate): Seq[LocalDate] = {
    val dateAfter = payDateThisYear.minusDays(364)

    val dateBefore = paymentFrequency match {
      case Weekly      => dateAfter.minusDays(7)
      case FortNightly => dateAfter.minusDays(14)
      case FourWeekly  => dateAfter.minusDays(28)
    }

    val dateBeforeAdjusted = if (dateBefore.isBefore(LocalDate.of(2019, 3, 1))) dateBefore.plusDays(1) else dateBefore

    Seq(dateBeforeAdjusted, dateAfter)
  }
}
