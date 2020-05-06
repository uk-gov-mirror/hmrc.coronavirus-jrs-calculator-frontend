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
import forms.FurloughTopUpFormProvider
import handlers.FurloughTopUpControllerRequestHandler
import javax.inject.Inject
import navigation.Navigator
import pages.FurloughTopUpStatusPage
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.FurloughTopUpView

import scala.concurrent.{ExecutionContext, Future}

class FurloughTopUpController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: FurloughTopUpFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: FurloughTopUpView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with FurloughTopUpControllerRequestHandler {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    handleCalculationFurlough(request.userAnswers).fold {
      Logger.warn("couldn't calculate Furlough out of UserAnswers, restarting the journey")
      Redirect(routes.ClaimPeriodStartController.onPageLoad())
    } { data =>
      val preparedForm = request.userAnswers.get(FurloughTopUpStatusPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, data))
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    handleCalculationFurlough(request.userAnswers)
      .fold {
        Logger.warn("couldn't calculate Furlough out of UserAnswers, restarting the journey")
        Future.successful(Redirect(routes.ClaimPeriodStartController.onPageLoad()))
      } { data =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, data))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(FurloughTopUpStatusPage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(FurloughTopUpStatusPage, updatedAnswers))
          )
      }

  }
}
