/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import models.PayPeriod

trait PayPeriodGenerator {

  def generatePayPeriods(endDates: List[LocalDate]): List[PayPeriod] = {
    def generate(acc: List[PayPeriod], list: List[LocalDate]): List[PayPeriod] = list match {
      case Nil      => acc
      case h :: Nil => acc
      case h :: t   => generate(acc ++ List(PayPeriod(h.plusDays(1), t.head)), t)
    }

    if (endDates.length == 1) endDates.map(date => PayPeriod(date, date))
    else generate(List(), sortedEndDates(endDates))
  }

  protected def sortedEndDates(in: List[LocalDate]): List[LocalDate] = in.sortWith((x, y) => x.isBefore(y))

}
