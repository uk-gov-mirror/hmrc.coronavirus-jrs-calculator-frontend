/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.{FurloughStatus, UserAnswers}
import play.api.libs.json.JsPath

import scala.util.Try

case object FurloughStatusPage extends QuestionPage[FurloughStatus] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "furloughStatus"

  override def cleanup(value: Option[FurloughStatus], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(FurloughStatus.FurloughOngoing) =>
        userAnswers
          .remove(FurloughEndDatePage)
      case _ =>
        super.cleanup(value, userAnswers)
    }
}
