/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.{FurloughQuestion, UserAnswers}
import play.api.libs.json.JsPath

import scala.util.Try

case object FurloughQuestionPage extends QuestionPage[FurloughQuestion] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "furloughQuestion"

  override def cleanup(value: Option[FurloughQuestion], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(FurloughQuestion.No) =>
        userAnswers
          .remove(FurloughEndDatePage)
      case _ =>
        super.cleanup(value, userAnswers)
    }
}
