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
