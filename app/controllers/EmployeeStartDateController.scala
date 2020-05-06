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

import java.time.LocalDate

import controllers.actions.FeatureFlag.VariableJourneyFlag
import controllers.actions._
import forms.EmployeeStartDateFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import navigation.Navigator
import pages.{EmployeeStartDatePage, FurloughStartDatePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import views.html.EmployeeStartDateView

import scala.concurrent.{ExecutionContext, Future}

class EmployeeStartDateController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val navigator: Navigator,
  identify: IdentifierAction,
  feature: FeatureFlagActionProvider,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: EmployeeStartDateFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: EmployeeStartDateView
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler)
    extends BaseController with I18nSupport {

  def form: LocalDate => Form[LocalDate] = formProvider(_)

  def onPageLoad(): Action[AnyContent] = (identify andThen feature(VariableJourneyFlag) andThen getData andThen requireData).async {
    implicit request =>
      getRequiredAnswerOrRedirect(FurloughStartDatePage) { furloughStart =>
        val preparedForm = request.userAnswers.get(EmployeeStartDatePage) match {
          case None        => form(furloughStart)
          case Some(value) => form(furloughStart).fill(value)
        }
        Future.successful(Ok(view(preparedForm)))
      }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen feature(VariableJourneyFlag) andThen getData andThen requireData).async {
    implicit request =>
      getRequiredAnswerOrRedirect(FurloughStartDatePage) { furloughStart =>
        form(furloughStart)
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(EmployeeStartDatePage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(EmployeeStartDatePage, updatedAnswers))
          )
      }
  }
}
