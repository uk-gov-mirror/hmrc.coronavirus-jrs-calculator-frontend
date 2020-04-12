/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.PayQuestion
import play.api.libs.json.JsPath

case object PayQuestionPage extends QuestionPage[PayQuestion] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "payQuestion"
}
