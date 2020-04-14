/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.NicCategory
import play.api.libs.json.JsPath

case object NicCategoryPage extends QuestionPage[NicCategory] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "nicCategory"
}
