/*
 * Copyright 2020 HM Revenue & Customs
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

import java.time.LocalDate

trait LocalDateHelpers {

  def latestOf(first: LocalDate, rest: LocalDate*): LocalDate =
    rest.fold(first)((a: LocalDate, b: LocalDate) => if (a.isAfter(b)) a else b)

  def earliestOf(first: LocalDate, rest: LocalDate*): LocalDate =
    rest.fold(first)((a: LocalDate, b: LocalDate) => if (a.isBefore(b)) a else b)

}

object LocalDateHelpers extends LocalDateHelpers {

  val apr6th2019 = LocalDate.of(2019, 4, 6)
  val mar20th2020 = LocalDate.of(2020, 3, 20)
  val apr5th2020 = LocalDate.of(2020, 4, 5)
  val apr6th2020 = LocalDate.of(2020, 4, 6)
  val july1st2020 = LocalDate.of(2020, 7, 1)
  val aug1st2020 = LocalDate.of(2020, 8, 1)
  val nov1st2020 = LocalDate.of(2020, 11, 1)

  implicit class LocalDateHelper(val value: LocalDate) {
    def isEqualOrAfter(localDate: LocalDate) = value.compareTo(localDate) >= 0
    def isEqualOrBefore(localDate: LocalDate) = value.compareTo(localDate) <= 0
  }

}
