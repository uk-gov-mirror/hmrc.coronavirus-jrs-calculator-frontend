/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import models.PayPeriod

trait PayPeriodGenerator {

  def generatePayPeriods(endDates: Seq[LocalDate]): Seq[PayPeriod] = {
    def generate(acc: Seq[PayPeriod], list: Seq[LocalDate]): Seq[PayPeriod] = list match {
      case Nil      => acc
      case h :: Nil => acc
      case h :: t   => generate(acc ++ Seq(PayPeriod(h.plusDays(1), t.head)), t)
    }

    if (endDates.length == 1) endDates.map(date => PayPeriod(date, date))
    else generate(Seq(), sortedEndDates(endDates))
  }

  protected def sortedEndDates(in: Seq[LocalDate]): Seq[LocalDate] = in.sortWith((x, y) => x.isBefore(y))

}
