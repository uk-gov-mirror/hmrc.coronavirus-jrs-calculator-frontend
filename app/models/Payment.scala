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

case class CylbPayment(date: LocalDate, amount: Amount)

object CylbPayment {
  implicit val defaultFormat: Format[CylbPayment] = Json.format
}

sealed trait PaymentWithPeriod {
  val furloughPayment: Amount
  val periodWithPaymentDate: PeriodWithPaymentDate
}
case class PaymentWithFullPeriod(furloughPayment: Amount, periodWithPaymentDate: FullPeriodWithPaymentDate) extends PaymentWithPeriod
case class PaymentWithPartialPeriod(nonFurloughPay: Amount, furloughPayment: Amount, periodWithPaymentDate: PartialPeriodWithPaymentDate)
    extends PaymentWithPeriod

case class AdditionalPayment(date: LocalDate, amount: Amount)

object AdditionalPayment {
  implicit val defaultFormat: Format[AdditionalPayment] = Json.format
}

case class TopUpPayment(date: LocalDate, amount: Amount)

object TopUpPayment {
  implicit val defaultFormat: Format[TopUpPayment] = Json.format
}
