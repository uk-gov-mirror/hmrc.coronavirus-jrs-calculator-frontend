/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.FurloughDates
import play.api.libs.json.JsPath

case object FurloughDatesPage extends QuestionPage[FurloughDates] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "furloughDates"
}
