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

package views.includes

import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

import com.google.inject.Inject
import models._
import play.api.i18n.Messages
import services.Calculators._

class FurloughCapHelper @Inject()() {

  def calculationFor(cap: FurloughCap)(implicit messages: Messages): String =
    cap match {
      case FullPeriodCap(value) =>
        messages("furloughBreakdown.furloughCap.fullPeriodCap", value.formatted("%.2f"))
      case FullPeriodCapWithPartTime(value, unadjusted, usual, furloughed) =>
        messages(
          "phaseTwoFurloughBreakdown.furloughCap.fullPeriodCap.partTime",
          unadjusted.formatted("%.2f"),
          usual.formatted("%.2f"),
          furloughed.formatted("%.2f"),
          value.formatted("%.2f")
        )
      case PeriodSpansMonthCap(value, monthOneFurloughDays, monthOne, monthOneDaily, monthTwoFurloughDays, monthTwo, monthTwoDaily) =>
        messages(
          "furloughBreakdown.furloughCap.periodSpansMonthCap",
          monthOneFurloughDays,
          Month.of(monthOne).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
          monthOneDaily.formatted("%.2f"),
          monthTwoFurloughDays,
          Month.of(monthTwo).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
          monthTwoDaily.formatted("%.2f"),
          value.formatted("%.2f")
        )
      case PeriodSpansMonthCapWithPartTime(
          value,
          monthOneFurloughDays,
          monthOne,
          monthOneDaily,
          monthTwoFurloughDays,
          monthTwo,
          monthTwoDaily,
          _,
          usual,
          furloughed) =>
        messages(
          "phaseTwoFurloughBreakdown.furloughCap.periodSpansMonthCap.partTime",
          monthOneFurloughDays,
          Month.of(monthOne).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
          monthOneDaily.formatted("%.2f"),
          monthTwoFurloughDays,
          Month.of(monthTwo).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
          monthTwoDaily.formatted("%.2f"),
          usual.formatted("%.2f"),
          furloughed.formatted("%.2f"),
          value.formatted("%.2f")
        )
      case PartialPeriodCap(value, furloughDays, month, dailyCap) =>
        messages(
          "furloughBreakdown.furloughCap.partialPeriodCap",
          furloughDays,
          Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
          dailyCap.formatted("%.2f"),
          value.formatted("%.2f")
        )
      case PartialPeriodCapWithPartTime(value, furloughDays, month, dailyCap, _, usual, furloughed) =>
        messages(
          "phaseTwoFurloughBreakdown.furloughCap.partialPeriodCap.partTime",
          furloughDays,
          Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
          dailyCap.formatted("%.2f"),
          usual.formatted("%.2f"),
          furloughed.formatted("%.2f"),
          value.formatted("%.2f")
        )
    }

  def calculationForSept(cap: FurloughCap)(implicit messages: Messages): String =
    cap match {
      case FullPeriodCap(value) =>
        messages("furloughBreakdown.september.furloughCap.fullPeriodCap", seventy(value))
      case FullPeriodCapWithPartTime(value, unadjusted, usual, furloughed) =>
        messages(
          "phaseTwoFurloughBreakdown.furloughCap.fullPeriodCap.partTime",
          seventy(unadjusted),
          usual.formatted("%.2f"),
          furloughed.formatted("%.2f"),
          seventy(value)
        )
      case PeriodSpansMonthCap(value, monthOneFurloughDays, monthOne, monthOneDaily, monthTwoFurloughDays, monthTwo, monthTwoDaily) =>
        messages(
          "furloughBreakdown.furloughCap.periodSpansMonthCap",
          monthOneFurloughDays,
          Month.of(monthOne).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
          seventy(monthOneDaily),
          monthTwoFurloughDays,
          Month.of(monthTwo).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
          seventy(monthTwoDaily),
          seventy(value)
        )
      case PeriodSpansMonthCapWithPartTime(
          value,
          monthOneFurloughDays,
          monthOne,
          monthOneDaily,
          monthTwoFurloughDays,
          monthTwo,
          monthTwoDaily,
          _,
          usual,
          furloughed) =>
        messages(
          "phaseTwoFurloughBreakdown.furloughCap.periodSpansMonthCap.partTime",
          monthOneFurloughDays,
          Month.of(monthOne).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
          seventy(monthOneDaily),
          monthTwoFurloughDays,
          Month.of(monthTwo).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
          seventy(monthTwoDaily),
          usual.formatted("%.2f"),
          furloughed.formatted("%.2f"),
          seventy(value)
        )
      case PartialPeriodCap(value, furloughDays, month, dailyCap) =>
        messages(
          "furloughBreakdown.furloughCap.partialPeriodCap",
          furloughDays,
          Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
          seventy(dailyCap),
          seventy(value)
        )
      case PartialPeriodCapWithPartTime(value, furloughDays, month, dailyCap, _, usual, furloughed) =>
        messages(
          "phaseTwoFurloughBreakdown.furloughCap.partialPeriodCap.partTime",
          furloughDays,
          Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
          seventy(dailyCap),
          usual.formatted("%.2f"),
          furloughed.formatted("%.2f"),
          seventy(value)
        )
    }

  def calculationForOct(cap: FurloughCap)(implicit messages: Messages): String =
    cap match {
      case FullPeriodCap(value) =>
        messages("furloughBreakdown.october.furloughCap.fullPeriodCap", sixty(value))
      case FullPeriodCapWithPartTime(value, unadjusted, usual, furloughed) =>
        messages(
          "phaseTwoFurloughBreakdown.furloughCap.fullPeriodCap.partTime",
          sixty(unadjusted),
          usual.formatted("%.2f"),
          furloughed.formatted("%.2f"),
          sixty(value)
        )
      case PeriodSpansMonthCap(value, monthOneFurloughDays, monthOne, monthOneDaily, monthTwoFurloughDays, monthTwo, monthTwoDaily) =>
        messages(
          "furloughBreakdown.furloughCap.periodSpansMonthCap",
          monthOneFurloughDays,
          Month.of(monthOne).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
          sixty(monthOneDaily),
          monthTwoFurloughDays,
          Month.of(monthTwo).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
          sixty(monthTwoDaily),
          sixty(value)
        )
      case PeriodSpansMonthCapWithPartTime(
          value,
          monthOneFurloughDays,
          monthOne,
          monthOneDaily,
          monthTwoFurloughDays,
          monthTwo,
          monthTwoDaily,
          _,
          usual,
          furloughed) =>
        messages(
          "phaseTwoFurloughBreakdown.furloughCap.periodSpansMonthCap.partTime",
          monthOneFurloughDays,
          Month.of(monthOne).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
          sixty(monthOneDaily),
          monthTwoFurloughDays,
          Month.of(monthTwo).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
          sixty(monthTwoDaily),
          usual.formatted("%.2f"),
          furloughed.formatted("%.2f"),
          sixty(value)
        )
      case PartialPeriodCap(value, furloughDays, month, dailyCap) =>
        messages(
          "furloughBreakdown.furloughCap.partialPeriodCap",
          furloughDays,
          Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
          sixty(dailyCap),
          sixty(value)
        )
      case PartialPeriodCapWithPartTime(value, furloughDays, month, dailyCap, _, usual, furloughed) =>
        messages(
          "phaseTwoFurloughBreakdown.furloughCap.partialPeriodCap.partTime",
          furloughDays,
          Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
          sixty(dailyCap),
          usual.formatted("%.2f"),
          furloughed.formatted("%.2f"),
          sixty(value)
        )
    }

  private def seventy(in: BigDecimal): String =
    Amount((in / 80) * 70).halfUp.value.formatted("%.2f")

  private def sixty(in: BigDecimal): String =
    Amount((in / 80) * 60).halfUp.value.formatted("%.2f")

}
