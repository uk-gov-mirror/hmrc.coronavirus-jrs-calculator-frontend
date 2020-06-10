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

import cats.data.Validated.{Invalid, Valid}
import controllers.actions._
import forms.PartTimePeriodsFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import models.UserAnswers
import navigation.Navigator
import pages.{PartTimePeriodsPage, PayDatePage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import services.{FurloughPeriodExtractor, PeriodHelper}
import views.html.PartTimePeriodsView

import scala.concurrent.{ExecutionContext, Future}

class PartTimePeriodsController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  override val navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PartTimePeriodsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PartTimePeriodsView
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler)
    extends BaseController with PeriodHelper with FurloughPeriodExtractor {

  val form: Form[List[LocalDate]] = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.userAnswers.getList(PayDatePage) match {
      case Nil =>
        Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
      case dates =>
        extractFurloughWithinClaimV(request.userAnswers) match {
          case Valid(furlough) =>
            val periods = generatePeriodsWithFurlough(dates, furlough).toList
            val preparedForm = request.userAnswers.getV(PartTimePeriodsPage) match {
              case Invalid(_)             => form
              case Valid(selectedPeriods) => form.fill(selectedPeriods.map(_.period.end))
            }

            if (periods.length == 1) {
              saveAndRedirect(request.userAnswers, periods.map(_.period.end))
            } else {
              Future.successful(Ok(view(preparedForm, periods)))
            }
          case Invalid(errors) => Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
        }
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.userAnswers.getList(PayDatePage) match {
      case Nil =>
        Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
      case dates =>
        extractFurloughWithinClaimV(request.userAnswers) match {
          case Valid(furlough) =>
            val periods = generatePeriodsWithFurlough(dates, furlough).toList

            form
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, periods))), { selectedDates =>
                  saveAndRedirect(request.userAnswers, selectedDates)
                }
              )
          case Invalid(errors) => Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
        }
    }
  }

  private def saveAndRedirect(userAnswers: UserAnswers, selectedEndDates: List[LocalDate]): Future[Result] =
    extractFurloughWithinClaimV(userAnswers) match {
      case Valid(furlough) =>
        val endDates = userAnswers.getList(PayDatePage)
        val selectedPeriods = generatePeriodsWithFurlough(endDates, furlough).filter(p => selectedEndDates.contains(p.period.end)).toList

        for {
          updatedAnswers <- Future.fromTry(userAnswers.set(PartTimePeriodsPage, selectedPeriods))
          _              <- sessionRepository.set(updatedAnswers)
        } yield Redirect(navigator.nextPage(PartTimePeriodsPage, updatedAnswers))
      case Invalid(e) => Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
    }
}
