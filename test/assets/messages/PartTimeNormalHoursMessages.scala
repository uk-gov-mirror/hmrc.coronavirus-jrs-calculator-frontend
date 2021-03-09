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

package assets.messages

import java.time.LocalDate

object PartTimeNormalHoursMessages {

  def dateToString(date: LocalDate): String =
    s"${date.getDayOfMonth} ${date.getMonth.toString.toLowerCase.capitalize} ${date.getYear}"

  def dateToStringWithoutYear(date: LocalDate): String =
    s"${date.getDayOfMonth} ${date.getMonth.toString.toLowerCase.capitalize}"

  def h1(start: LocalDate, end: LocalDate) =
    s"What would this employee’s usual hours have been between ${dateToStringWithoutYear(start)} and ${dateToString(end)}?"
  def h1Single(day: LocalDate) = s"What would this employee’s usual hours have been on ${dateToString(day)}?"

  val p1 =
    "This is the total number of hours this employee would have been expected to work during this pay period if they had not been furloughed."
  val link = "work out your employee’s usual hours and furloughed hours (opens in a new tab)."
  val p2   = s"Read more about how to $link"
}
