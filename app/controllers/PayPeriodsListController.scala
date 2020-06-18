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
import forms.PayPeriodsListFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.PayPeriodsListPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.PayPeriodsListView

import scala.concurrent.{ExecutionContext, Future}
import cats.data.Validated.{Invalid, Valid}
import handlers.PayPeriodsListHandler
import models.UserAnswers

class PayPeriodsListController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PayPeriodsListFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PayPeriodsListView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with PayPeriodsListHandler {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    extractPayPeriods(request.userAnswers) match {
      case Nil => Redirect(routes.ErrorController.somethingWentWrong())
      case periods =>
        extractClaimPeriod(request.userAnswers) match {
          case Valid(claimPeriod) =>
            val preparedForm = request.userAnswers.getV(PayPeriodsListPage) match {
              case Invalid(e) =>
                UserAnswers.logErrors(e)(logger)
                form
              case Valid(value) => form.fill(value)
            }

            Ok(view(preparedForm, periods, claimPeriod))
          case Invalid(err) =>
            UserAnswers.logErrors(err)(logger)
            Redirect(routes.ErrorController.somethingWentWrong())
        }
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    extractPayPeriods(request.userAnswers) match {
      case Nil => Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
      case periods =>
        extractClaimPeriod(request.userAnswers) match {
          case Valid(claimPeriod) =>
            form
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, periods, claimPeriod))),
                value =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(PayPeriodsListPage, value))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield Redirect(navigator.nextPage(PayPeriodsListPage, updatedAnswers))
              )
          case Invalid(err) =>
            UserAnswers.logErrors(err)(logger)
            Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
        }
    }

  }
}
