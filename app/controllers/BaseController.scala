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

import handlers.ErrorHandler
import models.requests.DataRequest
import navigation.Navigator
import pages.QuestionPage
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.libs.json.Reads
import play.api.mvc.Result
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

import scala.concurrent.Future

trait BaseController extends FrontendBaseController with I18nSupport {

  val navigator: Navigator

  def getAnswer[A](page: QuestionPage[A], idx: Int)(implicit request: DataRequest[_], reads: Reads[A]): Option[A] =
    getAnswer(page, Some(idx))

  def getAnswer[A](page: QuestionPage[A], idx: Option[Int] = None)(implicit request: DataRequest[_], reads: Reads[A]): Option[A] =
    request.userAnswers.get(page, idx)

  def getRequiredAnswer[A](page: QuestionPage[A], idx: Int)(
    f: A => Future[Result])(implicit request: DataRequest[_], reads: Reads[A], errorHandler: ErrorHandler): Future[Result] =
    getRequiredAnswer(page, Some(idx))(f)

  def getRequiredAnswer[A](page: QuestionPage[A], idx: Option[Int] = None)(
    f: A => Future[Result])(implicit request: DataRequest[_], reads: Reads[A], errorHandler: ErrorHandler): Future[Result] =
    getAnswer(page, idx) match {
      case Some(ans) => f(ans)
      case _ =>
        Logger.error(s"[BaseController][getRequiredAnswer] Failed to retrieve expected data for page: $page")
        Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
    }

  def getRequiredAnswerOrRedirect[A](page: QuestionPage[A], idx: Option[Int] = None)(
    f: A => Future[Result])(implicit request: DataRequest[_], reads: Reads[A]): Future[Result] =
    getAnswer(page, idx) match {
      case Some(ans) => f(ans)
      case _ =>
        val requiredPage = navigator.routeFor(page)
        Logger.error(s"Failed to retrieve expected data for page: $page, redirecting to $requiredPage")
        Future.successful(Redirect(requiredPage))
    }

  def getRequiredAnswers[A, B](pageA: QuestionPage[A], pageB: QuestionPage[B], idxA: Option[Int] = None, idxB: Option[Int] = None)(
    f: (A, B) => Future[Result])(
    implicit request: DataRequest[_],
    readsA: Reads[A],
    readsB: Reads[B],
    errorHandler: ErrorHandler): Future[Result] =
    getAnswer(pageA, idxA) match {
      case Some(ansA) =>
        getAnswer(pageB, idxB) match {
          case Some(ansB) => f(ansA, ansB)
          case _ =>
            Logger.error(s"[BaseController][getRequiredAnswers] Failed to retrieve expected data for page: $pageB")
            Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
        }
      case _ =>
        Logger.error(s"[BaseController][getRequiredAnswers] Failed to retrieve expected data for page: $pageA")
        Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
    }

}
