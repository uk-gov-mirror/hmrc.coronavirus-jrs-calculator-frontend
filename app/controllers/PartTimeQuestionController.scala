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
import forms.PartTimeQuestionFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.{ClaimPeriodStartPage, PartTimeQuestionPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.PartTimeQuestionView

import scala.concurrent.{ExecutionContext, Future}
import cats.data.Validated.{Invalid, Valid}
import handlers.ErrorHandler

class PartTimeQuestionController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PartTimeQuestionFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PartTimeQuestionView
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler)
    extends BaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswerV(ClaimPeriodStartPage) { claimStart =>
      val preparedForm = request.userAnswers.getV(PartTimeQuestionPage) match {
        case Invalid(e)   => form
        case Valid(value) => form.fill(value)
      }

      Future.successful(Ok(view(preparedForm, claimStart)))
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswerV(ClaimPeriodStartPage) { claimStart =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, claimStart))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(PartTimeQuestionPage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(PartTimeQuestionPage, updatedAnswers))
        )
    }
  }
}
