/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.VariableLengthEmployed
import play.api.libs.json.JsPath

case object VariableLengthEmployedPage extends QuestionPage[VariableLengthEmployed] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "variableLengthEmployed"
}
