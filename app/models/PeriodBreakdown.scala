/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json.{Format, JsResult, JsValue, Json}

sealed trait PeriodBreakdown {
  val grant: Amount
  val periodWithPaymentDate: PeriodWithPaymentDate
}

final case class FullPeriodBreakdown(grant: Amount, periodWithPaymentDate: FullPeriodWithPaymentDate) extends PeriodBreakdown
final case class PartialPeriodBreakdown(nonFurloughPay: Amount, grant: Amount, periodWithPaymentDate: PartialPeriodWithPaymentDate)
    extends PeriodBreakdown

object FullPeriodBreakdown {
  implicit val defaultFormat: Format[FullPeriodBreakdown] = Json.format
}

object PartialPeriodBreakdown {
  implicit val defaultFormat: Format[PartialPeriodBreakdown] = Json.format
}

object PeriodBreakdown {

  implicit val defaultFormat: Format[PeriodBreakdown] = new Format[PeriodBreakdown] {
    override def writes(o: PeriodBreakdown): JsValue = o match {
      case fp: FullPeriodBreakdown    => Json.writes[FullPeriodBreakdown].writes(fp)
      case pp: PartialPeriodBreakdown => Json.writes[PartialPeriodBreakdown].writes(pp)
    }

    override def reads(json: JsValue): JsResult[PeriodBreakdown] =
      if ((json \ "original").isDefined && (json \ "partial").isDefined)
        Json.reads[PartialPeriodBreakdown].reads(json)
      else Json.reads[FullPeriodBreakdown].reads(json)
  }
}
