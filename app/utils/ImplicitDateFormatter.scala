/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.language.implicitConversions

trait ImplicitDateFormatter {
  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
  implicit def dateToString(date:LocalDate): String = dateFormatter.format(date)
}
