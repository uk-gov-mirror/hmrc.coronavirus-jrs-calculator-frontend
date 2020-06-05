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
import forms.PartTimeNormalHoursFormProvider
import javax.inject.Inject
import models.{Periods, UsualHours}
import navigation.Navigator
import pages.{PartTimeNormalHoursPage, PartTimePeriodsPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import views.html.PartTimeNormalHoursView

import scala.concurrent.{ExecutionContext, Future}

class PartTimeNormalHoursController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PartTimeNormalHoursFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PartTimeNormalHoursView
)(implicit ec: ExecutionContext)
    extends BaseController {

  val form = formProvider()

  def onPageLoad(idx: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswerOrRedirectV(PartTimePeriodsPage) { partTimePeriods =>
      withValidPartTimePeriod(partTimePeriods, idx) { partTimePeriod =>
        val preparedForm = request.userAnswers.getV(PartTimeNormalHoursPage, Some(idx)) match {
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
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, partTimePeriod, idx))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers
                                   .set(PartTimeNormalHoursPage, UsualHours(partTimePeriod.period.end, value), Some(idx)))
                _ <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(PartTimeNormalHoursPage, updatedAnswers, Some(idx)))
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
