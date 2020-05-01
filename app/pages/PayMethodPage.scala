/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.PayMethod
import play.api.libs.json.JsPath

case object PayMethodPage extends QuestionPage[PayMethod] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "payMethod"
}
