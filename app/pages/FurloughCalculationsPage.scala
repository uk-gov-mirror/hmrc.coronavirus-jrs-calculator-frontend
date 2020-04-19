/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.FurloughCalculations
import play.api.libs.json.JsPath

case object FurloughCalculationsPage extends QuestionPage[FurloughCalculations] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "furloughCalculations"
}
