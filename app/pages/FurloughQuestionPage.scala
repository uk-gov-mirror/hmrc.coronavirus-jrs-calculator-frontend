/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.FurloughQuestion
import play.api.libs.json.JsPath

case object FurloughQuestionPage extends QuestionPage[FurloughQuestion] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "furloughQuestion"
}
