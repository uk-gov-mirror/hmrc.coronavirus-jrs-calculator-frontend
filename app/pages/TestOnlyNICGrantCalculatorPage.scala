/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.TestOnlyNICGrantModel
import play.api.libs.json.JsPath

case object TestOnlyNICGrantCalculatorPage extends QuestionPage[TestOnlyNICGrantModel] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "testOnlyNICGrantCalculator"
}
