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

import controllers.actions._
import forms.FurloughPeriodQuestionFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import models.FurloughStatus.{FurloughEnded, FurloughOngoing}
import navigation.Navigator
import pages.{FurloughEndDatePage, FurloughPeriodQuestionPage, FurloughStartDatePage, FurloughStatusPage}
import play.api.Logger
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import views.html.FurloughPeriodQuestionView

import scala.concurrent.{ExecutionContext, Future}

class FurloughPeriodQuestionController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: FurloughPeriodQuestionFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: FurloughPeriodQuestionView
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler)
    extends BaseController {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswers(FurloughStartDatePage, FurloughStatusPage) { (furloughStart, furloughStatus) =>
      val preparedForm = request.userAnswers.get(FurloughPeriodQuestionPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      furloughStatus match {
        case FurloughOngoing => Future.successful(Ok(view(preparedForm, furloughStart, furloughStatus, None)))
        case FurloughEnded =>
          getAnswer(FurloughEndDatePage) match {
            case Some(furloughEnd) => Future.successful(Ok(view(preparedForm, furloughStart, furloughStatus, Some(furloughEnd))))
            case None =>
              Logger.error("expecting FurloughEndDate in mongo when its furlough ended, but not found")
              Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
          }
      }
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswers(FurloughStartDatePage, FurloughStatusPage) { (furloughStart, furloughStatus) =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            furloughStatus match {
              case FurloughOngoing => Future.successful(BadRequest(view(formWithErrors, furloughStart, furloughStatus, None)))
              case FurloughEnded =>
                getAnswer(FurloughEndDatePage) match {
                  case Some(furloughEnd) =>
                    Future.successful(BadRequest(view(formWithErrors, furloughStart, furloughStatus, Some(furloughEnd))))
                  case None =>
                    Logger.error("expecting FurloughEndDate in mongo when its furlough ended, but not found")
                    Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
                }
          },
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(FurloughPeriodQuestionPage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(FurloughPeriodQuestionPage, updatedAnswers))
        )
    }
  }
}
