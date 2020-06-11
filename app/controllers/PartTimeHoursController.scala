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
import forms.PartTimeHoursFormProvider
import javax.inject.Inject
import models.{PartTimeHours, Periods, UsualHours}
import navigation.Navigator
import pages.{PartTimeHoursPage, PartTimeNormalHoursPage, PartTimePeriodsPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import views.html.PartTimeHoursView

import scala.concurrent.{ExecutionContext, Future}

class PartTimeHoursController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PartTimeHoursFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PartTimeHoursView
)(implicit ec: ExecutionContext)
    extends BaseController {

  def onPageLoad(idx: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswerOrRedirectV(PartTimePeriodsPage) { partTimePeriods =>
      withValidPartTimePeriod(partTimePeriods, idx) { partTimePeriod =>
        val usuals: Seq[UsualHours] = request.userAnswers.getList(PartTimeNormalHoursPage)
        val form = formProvider(usuals, partTimePeriod)
        val preparedForm = request.userAnswers.getV(PartTimeHoursPage, Some(idx)) match {
          case Invalid(e)   => form
          case Valid(value) => form.fill(value.hours)
        }

        Future.successful(Ok(view(preparedForm, partTimePeriod, idx)))
      }
    }
  }

  def onSubmit(idx: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswerOrRedirectV(PartTimePeriodsPage) { partTimePeriods =>
      withValidPartTimePeriod(partTimePeriods, idx) { partTimePeriod =>
        val usuals: Seq[UsualHours] = request.userAnswers.getList(PartTimeNormalHoursPage)
        formProvider(usuals, partTimePeriod)
          .bindFromRequest()
          .fold(
            formWithErrors => {
              Future.successful(BadRequest(view(formWithErrors, partTimePeriod, idx)))
            },
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers
                                   .set(PartTimeHoursPage, PartTimeHours(partTimePeriod.period.end, value), Some(idx)))
                _ <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(PartTimeHoursPage, updatedAnswers, Some(idx)))
          )
      }
    }
  }

  private def withValidPartTimePeriod(partTimePeriods: Seq[Periods], idx: Int)(f: Periods => Future[Result]): Future[Result] =
    partTimePeriods.lift(idx - 1) match {
      case Some(periods) => f(periods)
      case None          => Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
    }
}
