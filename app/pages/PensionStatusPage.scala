/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.PensionStatus
import play.api.libs.json.JsPath

case object PensionStatusPage extends QuestionPage[PensionStatus] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "pensionStatus"
}
