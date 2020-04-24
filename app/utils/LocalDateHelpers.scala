/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package utils

import java.time.LocalDate

trait LocalDateHelpers {

  def latestOf(first: LocalDate, rest: LocalDate*): LocalDate =
    rest.fold(first)((a: LocalDate, b: LocalDate) => if (a.isAfter(b)) a else b)

  def earliestOf(first: LocalDate, rest: LocalDate*): LocalDate =
    rest.fold(first)((a: LocalDate, b: LocalDate) => if (a.isBefore(b)) a else b)

}

object LocalDateHelpers extends LocalDateHelpers {

  implicit class LocalDateHelper(val value: LocalDate) {
    def isEqualOrAfter(localDate: LocalDate) = value.compareTo(localDate) >= 0
    def isEqualOrBefore(localDate: LocalDate) = value.compareTo(localDate) <= 0
  }

}
