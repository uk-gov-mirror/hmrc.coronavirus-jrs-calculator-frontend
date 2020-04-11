/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.ClaimPeriodModel
import play.api.libs.json.JsPath

case object ClaimPeriodPage extends QuestionPage[ClaimPeriodModel] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "claimPeriod"
}
