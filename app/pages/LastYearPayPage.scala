/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.CylbPayment
import play.api.libs.json.JsPath

case object LastYearPayPage extends QuestionPage[CylbPayment] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "lastYearPay"
}
