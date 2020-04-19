/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.VariableLengthPartialPay
import play.api.libs.json.JsPath

case object PartialPayBeforeFurloughPage extends QuestionPage[VariableLengthPartialPay] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "PartialPayBeforeFurlough"
}
