/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.PensionContribution
import play.api.libs.json.JsPath

case object PensionContributionPage extends QuestionPage[PensionContribution] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "pensionContribution"
}
