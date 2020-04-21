/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import java.time.LocalDate

import play.api.libs.json.{Format, Json}
import utils.ValueClassFormat

case class PaymentDate(value: LocalDate)

object PaymentDate {
  implicit val defaultFormat: Format[PaymentDate] =
    ValueClassFormat.format(dateString => PaymentDate.apply(LocalDate.parse(dateString)))(_.value)
}

case class NonFurloughPay(pre: Option[Amount], post: Option[Amount])

object NonFurloughPay {
  implicit val defaultFormat: Format[NonFurloughPay] = Json.format[NonFurloughPay]

  implicit class PrePostFurlough(nonFurloughPay: NonFurloughPay) {
    def preAmount: Amount = opt(nonFurloughPay.pre)
    def postAmount: Amount = opt(nonFurloughPay.post)

    private val opt: Option[Amount] => Amount =
      opt => opt.fold(Amount(0.0))(v => v)
  }
}

case class CylbPayment(paymentDate: PaymentDate, amount: Amount)

case class PaymentWithPeriod(nonFurloughPay: Amount, furloughPayment: Amount, period: PeriodWithPaymentDate, payQuestion: PayQuestion)

case class PeriodBreakdown(nonFurloughPay: Amount, grant: Amount, periodWithPaymentDate: PeriodWithPaymentDate)

object PeriodBreakdown {
  implicit val defaultFormat: Format[PeriodBreakdown] = Json.format[PeriodBreakdown]
}
