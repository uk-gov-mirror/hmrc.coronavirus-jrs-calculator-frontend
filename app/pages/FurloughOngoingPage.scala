/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.{FurloughOngoing, UserAnswers}
import play.api.libs.json.JsPath

import scala.util.Try

case object FurloughOngoingPage extends QuestionPage[FurloughOngoing] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "furloughOngoing"

  override def cleanup(value: Option[FurloughOngoing], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(FurloughOngoing.No) =>
        userAnswers
          .remove(FurloughEndDatePage)
      case _ =>
        super.cleanup(value, userAnswers)
    }
}
