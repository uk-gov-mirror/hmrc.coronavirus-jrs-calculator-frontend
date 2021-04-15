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

package models

import java.time.YearMonth
import java.time.Month._

sealed trait FurloughGrantRate {
  val value: Int
  def asPercentage: BigDecimal = BigDecimal(value) / 100
}
case object SixtyPercent   extends FurloughGrantRate { override val value: Int = 60 }
case object SeventyPercent extends FurloughGrantRate { override val value: Int = 70 }
case object EightyPercent  extends FurloughGrantRate { override val value: Int = 80 }

object FurloughGrantRate {

  val yearMonthMap = Map[YearMonth, FurloughGrantRate](
    YearMonth.of(2020, SEPTEMBER) -> SeventyPercent,
    YearMonth.of(2020, OCTOBER)   -> SixtyPercent,
    YearMonth.of(2021, JULY)      -> SeventyPercent,
    YearMonth.of(2021, AUGUST)    -> SixtyPercent,
    YearMonth.of(2021, SEPTEMBER) -> SixtyPercent
  ).withDefaultValue(EightyPercent)

  def rateForYearMonth(yearMonth: YearMonth): FurloughGrantRate = yearMonthMap(yearMonth)
}
