/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

<<<<<<< HEAD
import models.FurloughPartialPay
import play.api.libs.json.JsPath

case object PartialPayAfterFurloughPage extends QuestionPage[FurloughPartialPay] {
=======
import models.VariableLengthPartialPay
import play.api.libs.json.JsPath

case object PartialPayAfterFurloughPage extends QuestionPage[VariableLengthPartialPay] {
>>>>>>> 99695f13f65c4f3be36cb188c073ce349bf0618b

  override def path: JsPath = JsPath \ toString

  override def toString: String = "PartialPayAfterFurlough"
}
