/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.VariableGrossPay
import play.api.libs.json.JsPath

case object VariableGrossPayPage extends QuestionPage[VariableGrossPay] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "variableGrossPay"
}
