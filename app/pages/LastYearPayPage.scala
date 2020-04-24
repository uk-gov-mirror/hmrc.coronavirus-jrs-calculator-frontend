/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.CylbPaymentWith2020Periods
import play.api.libs.json.JsPath

case object LastYearPayPage extends QuestionPage[CylbPaymentWith2020Periods] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "lastYearPay"
}
