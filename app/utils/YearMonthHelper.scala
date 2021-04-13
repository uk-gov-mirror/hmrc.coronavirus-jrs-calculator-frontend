/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, Month, Year, YearMonth}

trait YearMonthHelper {

  val y2020: Year = Year.of(2020)
  val y2021: Year = Year.of(2021)

  implicit class monthExt(month: Month) {

    implicit def inYear(year: Year): YearMonth =
      YearMonth.of(year.getValue, month)
  }

  implicit class yearMonthExt(yearMonth: YearMonth) {

    implicit def isEqualToOrBefore(other: YearMonth): Boolean =
      yearMonth.equals(other) || yearMonth.isBefore(other)

    implicit def isEqualToOrAfter(other: YearMonth): Boolean =
      yearMonth.equals(other) || yearMonth.isAfter(other)

    implicit def isBetweenInclusive(min: YearMonth, max: YearMonth): Boolean =
      yearMonth.isEqualToOrAfter(min) && yearMonth.isEqualToOrBefore(max)

    implicit def stringFmt: String =
      s"${yearMonth.getMonth.toString.toLowerCase}${yearMonth.getYear}"
  }

  implicit class localDateExt(date: LocalDate) {

    implicit def getYearMonth: YearMonth =
      YearMonth.of(date.getYear, date.getMonth)
  }
}
