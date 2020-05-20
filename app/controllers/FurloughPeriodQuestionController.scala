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

import controllers.actions.FeatureFlag.FastTrackJourneyFlag
import controllers.actions._
import forms.FurloughPeriodQuestionFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import models.{FurloughEnded, FurloughOngoing}
import navigation.Navigator
import pages.{FurloughPeriodQuestionPage, FurloughStartDatePage, FurloughStatusPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.FurloughPeriodExtractor
import views.html.FurloughPeriodQuestionView

import scala.concurrent.{ExecutionContext, Future}

class FurloughPeriodQuestionController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val navigator: Navigator,
  feature: FeatureFlagActionProvider,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: FurloughPeriodQuestionFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: FurloughPeriodQuestionView
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler)
    extends BaseController with FurloughPeriodExtractor {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen feature(FastTrackJourneyFlag) andThen getData andThen requireData).async {
    implicit request =>
      getRequiredAnswers(FurloughStartDatePage, FurloughStatusPage) { (furloughStart, furloughStatus) =>
        val preparedForm = request.userAnswers.get(FurloughPeriodQuestionPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        extractFurloughPeriod(request.userAnswers) match {
          case Some(FurloughOngoing(_)) =>
            Future.successful(Ok(view(preparedForm, furloughStart, furloughStatus, None)))
          case Some(FurloughEnded(_, end)) =>
            Future.successful(Ok(view(preparedForm, furloughStart, furloughStatus, Some(end))))
        }
      }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen feature(FastTrackJourneyFlag) andThen getData andThen requireData).async {
    implicit request =>
      getRequiredAnswers(FurloughStartDatePage, FurloughStatusPage) { (furloughStart, furloughStatus) =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors =>
              extractFurloughPeriod(request.userAnswers) match {
                case Some(FurloughOngoing(_)) =>
                  Future.successful(BadRequest(view(formWithErrors, furloughStart, furloughStatus, None)))
                case Some(FurloughEnded(_, end)) =>
                  Future.successful(BadRequest(view(formWithErrors, furloughStart, furloughStatus, Some(end))))
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
