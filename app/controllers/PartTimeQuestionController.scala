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

import cats.data.Validated.{Invalid, Valid}
import controllers.actions._
import forms.PartTimeQuestionFormProvider
import javax.inject.Inject
import models.{PartTimeQuestion, UserAnswers}
import navigation.Navigator
import org.slf4j.{Logger, LoggerFactory}
import pages.PartTimeQuestionPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.FurloughPeriodExtractor
import views.html.PartTimeQuestionView

import scala.concurrent.{ExecutionContext, Future}

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
)(implicit ec: ExecutionContext)
    extends BaseController with I18nSupport with FurloughPeriodExtractor {

  implicit override val logger: Logger = LoggerFactory.getLogger(getClass)

  val form: Form[PartTimeQuestion] = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    extractFurloughWithinClaimV(request.userAnswers) match {
      case Valid(furlough) =>
        val preparedForm = request.userAnswers.getV(PartTimeQuestionPage) match {
          case Invalid(e)   => form
          case Valid(value) => form.fill(value)
        }
        Ok(view(preparedForm, furlough))
      case Invalid(err) => {
        UserAnswers.logErrors(err)
        Redirect(routes.ErrorController.somethingWentWrong())
      }
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    extractFurloughWithinClaimV(request.userAnswers) match {
      case Valid(furlough) =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, furlough))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(PartTimeQuestionPage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(PartTimeQuestionPage, updatedAnswers))
          )
      case Invalid(err) => {
        UserAnswers.logErrors(err)
        Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
      }
    }
  }
}
