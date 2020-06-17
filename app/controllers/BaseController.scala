/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import cats.data.NonEmptyChain
import cats.data.Validated.{Invalid, Valid}
import handlers.ErrorHandler
import models.UserAnswers
import models.UserAnswers.AnswerV
import models.requests.DataRequest
import navigation.Navigator
import org.slf4j
import org.slf4j.LoggerFactory
import pages.QuestionPage
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsError, Reads}
import play.api.mvc.Result
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

import scala.concurrent.Future

trait BaseController extends FrontendBaseController with I18nSupport {

  def logger: slf4j.Logger = LoggerFactory.getLogger(getClass)

  def navigator: Navigator

  def logErrors(msg: String, source: NonEmptyChain[JsError]) = {
    logger.error(s"$msg. Validation errors:")
    logger.error(source.toChain.toList.mkString("\n"))
  }

  def getAnswerV[A](page: QuestionPage[A], idx: Int)(implicit request: DataRequest[_], reads: Reads[A]): AnswerV[A] =
    getAnswerV(page, Some(idx))

  def getAnswerV[A](page: QuestionPage[A], idx: Option[Int] = None)(implicit request: DataRequest[_], reads: Reads[A]): AnswerV[A] =
    request.userAnswers.getV(page, idx)

  def getRequiredAnswerV[A](page: QuestionPage[A], idx: Int)(
    f: A => Future[Result])(implicit request: DataRequest[_], reads: Reads[A], errorHandler: ErrorHandler): Future[Result] =
    getRequiredAnswerV(page, Some(idx))(f)

  def getRequiredAnswerV[A](page: QuestionPage[A], idx: Option[Int] = None)(
    f: A => Future[Result])(implicit request: DataRequest[_], reads: Reads[A], errorHandler: ErrorHandler): Future[Result] =
    getAnswerV(page, idx) match {
      case Valid(ans)      => f(ans)
      case Invalid(errors) =>
        // TODO (flav): Discuss with team if we want to display errors on the page.
        Logger.error(s"[BaseController][getRequiredAnswer] Failed to retrieve expected data for page: $page")
        Logger.error(errors.toNonEmptyList.toList.mkString("\n"))
        Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
    }

  def getRequiredAnswerOrRedirectV[A](page: QuestionPage[A], idx: Option[Int] = None)(
    f: A => Future[Result])(implicit request: DataRequest[_], reads: Reads[A]): Future[Result] =
    getAnswerV(page, idx) match {
      case Valid(ans) => f(ans)
      case Invalid(errors) =>
        val requiredPage = navigator.routeFor(page)
        Logger.error(s"Failed to retrieve expected data for page: $page, redirecting to $requiredPage")
        Logger.error(errors.toChain.toList.mkString("\n"))
        Future.successful(Redirect(requiredPage))
    }

  def getRequiredAnswersV[A, B](
    pageA: QuestionPage[A],
    pageB: QuestionPage[B],
    idxA: Option[Int] = None,
    idxB: Option[Int] = None
  )(
    f: (A, B) => Future[Result]
  )(implicit request: DataRequest[_], readsA: Reads[A], readsB: Reads[B], errorHandler: ErrorHandler): Future[Result] = {

    import cats.syntax.apply._

    (getAnswerV(pageA, idxA), getAnswerV(pageB, idxB))
      .mapN { (ansA, ansB) =>
        f(ansA, ansB)
      }
      .fold(
        nel => {
          Logger.error(s"[BaseController][getRequiredAnswers] Failed to retrieve expected data for page: $pageB")
          UserAnswers.logErrors(nel)
          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
        },
        identity
      )
  }

}
