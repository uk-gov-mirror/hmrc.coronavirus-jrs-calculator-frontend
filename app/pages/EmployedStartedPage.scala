/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.EmployeeStarted
import play.api.libs.json.JsPath

case object EmployedStartedPage extends QuestionPage[EmployeeStarted] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "employeeStarted"
}
