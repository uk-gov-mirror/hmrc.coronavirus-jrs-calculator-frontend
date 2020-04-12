/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.PaymentFrequency
import play.api.libs.json.JsPath

case object PaymentFrequencyPage extends QuestionPage[PaymentFrequency] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "paymentFrequency"
}
