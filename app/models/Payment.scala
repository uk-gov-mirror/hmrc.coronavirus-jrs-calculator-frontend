/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import java.time.LocalDate

import play.api.libs.json.{Format, Json}
import services.PeriodHelper
import utils.ValueClassFormat

case class PaymentDate(value: LocalDate)

object PaymentDate {
  implicit val defaultFormat: Format[PaymentDate] =
    ValueClassFormat.format(dateString => PaymentDate.apply(LocalDate.parse(dateString)))(_.value)
}

case class NonFurloughPay(pre: Option[Amount], post: Option[Amount])

object NonFurloughPay extends PeriodHelper {
  implicit val defaultFormat: Format[NonFurloughPay] = Json.format[NonFurloughPay]

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
