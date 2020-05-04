/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.FurloughTopUpStatus
import play.api.libs.json.JsPath

case object FurloughTopUpStatusPage extends QuestionPage[FurloughTopUpStatus] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "furloughTopUpStatus"
}
