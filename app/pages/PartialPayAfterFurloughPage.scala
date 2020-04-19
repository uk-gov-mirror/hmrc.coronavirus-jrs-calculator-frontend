/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.FurloughPartialPay
import play.api.libs.json.JsPath

case object PartialPayAfterFurloughPage extends QuestionPage[FurloughPartialPay] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "PartialPayAfterFurlough"
}
