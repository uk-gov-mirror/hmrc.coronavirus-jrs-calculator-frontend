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

package models

import play.api.libs.json.{Format, Json}
import services.Calculators._

sealed trait FurloughCap {
  val value: BigDecimal
  def toPartTime(usual: BigDecimal, furloughed: BigDecimal): FurloughCap
}

case class FullPeriodCap(value: BigDecimal) extends FurloughCap {
  override def toPartTime(usual: BigDecimal, furloughed: BigDecimal): FurloughCap = {
    val adjustedCap = Amount((value / usual) * furloughed).halfUp.value
    FullPeriodCapWithPartTime(adjustedCap, value, usual, furloughed)
  }
}

object FullPeriodCap {
  implicit val defaultFormat: Format[FullPeriodCap] = Json.format
}

case class FullPeriodCapWithPartTime(value: BigDecimal, unadjusted: BigDecimal, usual: BigDecimal, furloughed: BigDecimal)
    extends FurloughCap {
  override def toPartTime(usual: BigDecimal, furloughed: BigDecimal): FurloughCap = this
}

case class PeriodSpansMonthCap(
  value: BigDecimal,
  monthOneFurloughDays: Int,
  monthOne: Int,
  monthOneDaily: BigDecimal,
  monthTwoFurloughDays: Int,
  monthTwo: Int,
  monthTwoDaily: BigDecimal)
    extends FurloughCap {
  override def toPartTime(usual: BigDecimal, furloughed: BigDecimal): FurloughCap = {
    val adjustedCap = Amount((value / usual) * furloughed).halfUp.value
    PeriodSpansMonthCapWithPartTime(
      adjustedCap,
      monthOneFurloughDays,
      monthOne,
      monthOneDaily,
      monthTwoFurloughDays,
      monthTwo,
      monthTwoDaily,
      value,
      usual,
      furloughed)
  }
}

object PeriodSpansMonthCap {
  implicit val defaultFormat: Format[PeriodSpansMonthCap] = Json.format
}

case class PeriodSpansMonthCapWithPartTime(
  value: BigDecimal,
  monthOneFurloughDays: Int,
  monthOne: Int,
  monthOneDaily: BigDecimal,
  monthTwoFurloughDays: Int,
  monthTwo: Int,
  monthTwoDaily: BigDecimal,
  unadjusted: BigDecimal,
  usual: BigDecimal,
  furloughed: BigDecimal)
    extends FurloughCap {
  override def toPartTime(usual: BigDecimal, furloughed: BigDecimal): FurloughCap = this
}

case class PartialPeriodCap(value: BigDecimal, furloughDays: Int, month: Int, dailyCap: BigDecimal) extends FurloughCap {
  override def toPartTime(usual: BigDecimal, furloughed: BigDecimal): FurloughCap = {
    val adjustedCap = Amount((value / usual) * furloughed).halfUp.value
    PartialPeriodCapWithPartTime(adjustedCap, furloughDays, month, dailyCap, value, usual, furloughed)
  }
}

object PartialPeriodCap {
  implicit val defaultFormat: Format[PartialPeriodCap] = Json.format
}

case class PartialPeriodCapWithPartTime(
  value: BigDecimal,
  furloughDays: Int,
  month: Int,
  dailyCap: BigDecimal,
  unadjusted: BigDecimal,
  usual: BigDecimal,
  furloughed: BigDecimal)
    extends FurloughCap {
  override def toPartTime(usual: BigDecimal, furloughed: BigDecimal): FurloughCap = this
}
