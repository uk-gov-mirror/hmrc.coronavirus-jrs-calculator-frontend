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

import java.time.LocalDate

import play.api.libs.json.{Format, Json}
import services.PeriodHelper
import services.Calculators._

case class PaymentDate(value: LocalDate)

case class NonFurloughPay(pre: Option[Amount], post: Option[Amount])

object NonFurloughPay extends PeriodHelper {
  implicit class PrePostFurlough(nonFurloughPay: NonFurloughPay) {
    def preAmount: Amount = opt(nonFurloughPay.pre)
    def postAmount: Amount = opt(nonFurloughPay.post)

    private val opt: Option[Amount] => Amount =
      opt => opt.fold(Amount(0.0))(v => v)
  }

  def determineNonFurloughPay(period: Periods, nonFurloughPay: NonFurloughPay): Amount =
    period match {
      case _: FullPeriod => Amount(0.00)
      case pp: PartialPeriod =>
        val pre = if (isFurloughStart(pp)) nonFurloughPay.preAmount else Amount(0.00)
        val post = if (isFurloughEnd(pp)) nonFurloughPay.postAmount else Amount(0.00)
        Amount(pre.value + post.value)
    }
}

case class LastYearPayment(date: LocalDate, amount: Amount)

object LastYearPayment {
  implicit val defaultFormat: Format[LastYearPayment] = Json.format
}

case class AdditionalPayment(date: LocalDate, amount: Amount)

object AdditionalPayment {
  implicit val defaultFormat: Format[AdditionalPayment] = Json.format
}

case class TopUpPayment(date: LocalDate, amount: Amount)

object TopUpPayment {
  implicit val defaultFormat: Format[TopUpPayment] = Json.format
}

sealed trait CylbBreakdown {
  val referencePay: Amount
}

case class OnePeriodCylb(referencePay: Amount, periodPay: Amount, daysInPeriod: Int, daysRequiredFromPeriod: Int, lastYearPayDay: LocalDate)
    extends CylbBreakdown
case class TwoPeriodCylb(referencePay: Amount, firstPeriod: OnePeriodCylb, secondPeriod: OnePeriodCylb) extends CylbBreakdown

sealed trait PaymentWithPeriod {
  val referencePay: Amount
  val periodWithPaymentDate: PeriodWithPaymentDate

  def periodDays: Int = periodWithPaymentDate.period.period.countDays

  def furloughDays: Int = periodWithPaymentDate.period match {
    case fp: FullPeriod    => fp.period.countDays
    case pp: PartialPeriod => pp.partial.countDays
  }

  def nonFurloughDays: Int = periodWithPaymentDate.period match {
    case _: FullPeriod     => 0
    case pp: PartialPeriod => pp.period.countDays - pp.partial.countDays
  }
}

sealed trait PaymentWithFullPeriod extends PaymentWithPeriod {
  val periodWithPaymentDate: FullPeriodWithPaymentDate
}

sealed trait PaymentWithPartialPeriod extends PaymentWithPeriod {
  val nonFurloughPay: Amount
  val periodWithPaymentDate: PartialPeriodWithPaymentDate
}

sealed trait RegularPayment extends PaymentWithPeriod {
  val regularPay: Amount
}

sealed trait AveragePayment extends PaymentWithPeriod {
  val referencePay: Amount
  val annualPay: Amount
  val priorFurloughPeriod: Period
}

sealed trait CylbPayment extends PaymentWithPeriod {
  val averagePayment: AveragePayment
  val cylbBreakdown: CylbBreakdown
}

case class RegularPaymentWithFullPeriod(regularPay: Amount, referencePay: Amount, periodWithPaymentDate: FullPeriodWithPaymentDate)
    extends PaymentWithFullPeriod with RegularPayment

case class RegularPaymentWithPartialPeriod(
  nonFurloughPay: Amount,
  regularPay: Amount,
  referencePay: Amount,
  periodWithPaymentDate: PartialPeriodWithPaymentDate)
    extends PaymentWithPartialPeriod with RegularPayment

case class AveragePaymentWithFullPeriod(
  referencePay: Amount,
  periodWithPaymentDate: FullPeriodWithPaymentDate,
  annualPay: Amount,
  priorFurloughPeriod: Period)
    extends PaymentWithFullPeriod with AveragePayment

case class AveragePaymentWithPartialPeriod(
  nonFurloughPay: Amount,
  referencePay: Amount,
  periodWithPaymentDate: PartialPeriodWithPaymentDate,
  annualPay: Amount,
  priorFurloughPeriod: Period)
    extends PaymentWithPartialPeriod with AveragePayment

case class CylbPaymentWithFullPeriod(
  referencePay: Amount,
  periodWithPaymentDate: FullPeriodWithPaymentDate,
  averagePayment: AveragePayment,
  cylbBreakdown: CylbBreakdown)
    extends PaymentWithFullPeriod with CylbPayment

case class CylbPaymentWithPartialPeriod(
  nonFurloughPay: Amount,
  referencePay: Amount,
  periodWithPaymentDate: PartialPeriodWithPaymentDate,
  averagePayment: AveragePayment,
  cylbBreakdown: CylbBreakdown)
    extends PaymentWithPartialPeriod with CylbPayment

sealed trait PaymentWithPhaseTwoPeriod {
  val referencePay: Amount
  val phaseTwoPeriod: PhaseTwoPeriod

  def periodDays: Int = phaseTwoPeriod.periodWithPaymentDate.period.period.countDays

  def furloughDays: Int = phaseTwoPeriod.periodWithPaymentDate.period match {
    case fp: FullPeriod    => fp.period.countDays
    case pp: PartialPeriod => pp.partial.countDays
  }
}

case class RegularPaymentWithPhaseTwoPeriod(regularPay: Amount, referencePay: Amount, phaseTwoPeriod: PhaseTwoPeriod)
    extends PaymentWithPhaseTwoPeriod {
  def basedOnDays: String = Amount((regularPay.value / periodDays) * furloughDays).halfUp.value.formatted("%.2f")
  def basedOnHours: String = referencePay.value.formatted("%.2f")
}

case class AveragePaymentWithPhaseTwoPeriod(
  referencePay: Amount,
  annualPay: Amount,
  priorFurloughPeriod: Period,
  phaseTwoPeriod: PhaseTwoPeriod)
    extends PaymentWithPhaseTwoPeriod {
  def basedOnDays: String = {
    val daily = Amount(annualPay.value / priorFurloughPeriod.countDays).halfUp
    (daily.value * furloughDays).formatted("%.2f")
  }
  def basedOnHours: String = referencePay.value.formatted("%.2f")
}

case class CylbPaymentWithPhaseTwoPeriod(
  referencePay: Amount,
  phaseTwoPeriod: PhaseTwoPeriod,
  averagePayment: AveragePaymentWithPhaseTwoPeriod,
  cylbBreakdown: CylbBreakdown)
    extends PaymentWithPhaseTwoPeriod {
  def basedOnDays: String = cylbBreakdown.referencePay.value.formatted("%.2f")
  def basedOnHours: String =
    Amount((cylbBreakdown.referencePay.value / phaseTwoPeriod.usual) * phaseTwoPeriod.furloughed).halfUp.value.formatted("%.2f")
  def higherOfResult: String = referencePay.value.formatted("%.2f")
}
