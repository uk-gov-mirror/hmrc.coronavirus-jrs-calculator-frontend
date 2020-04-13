/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.Salary
import play.api.libs.json.JsPath

case object SalaryQuestionPage extends QuestionPage[Salary] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "salary"
}
