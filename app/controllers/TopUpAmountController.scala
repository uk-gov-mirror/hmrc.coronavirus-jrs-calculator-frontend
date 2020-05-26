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
import controllers.actions.FeatureFlag.TopUpJourneyFlag
import controllers.actions._
import forms.TopUpAmountFormProvider
import javax.inject.Inject
import models.{TopUpPayment, TopUpPeriod}
import navigation.Navigator
import pages.{TopUpAmountPage, TopUpPeriodsPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import views.html.TopUpAmountView

import scala.concurrent.{ExecutionContext, Future}

class TopUpAmountController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val navigator: Navigator,
  identify: IdentifierAction,
  feature: FeatureFlagActionProvider,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: TopUpAmountFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: TopUpAmountView
)(implicit ec: ExecutionContext)
    extends BaseController {

  val form = formProvider()

  def onPageLoad(idx: Int): Action[AnyContent] = (identify andThen feature(TopUpJourneyFlag) andThen getData andThen requireData).async {
    implicit request =>
      getRequiredAnswerOrRedirectV(TopUpPeriodsPage) { topUpPeriods =>
        withValidTopUpDate(topUpPeriods, idx) { topUpPeriod =>
          val preparedForm = request.userAnswers.getV(TopUpAmountPage, Some(idx)) match {
            case Invalid(e)   => form
            case Valid(value) => form.fill(value.amount)
          }

          Future.successful(Ok(view(preparedForm, topUpPeriod, idx)))
        }
      }
  }

  def onSubmit(idx: Int): Action[AnyContent] = (identify andThen feature(TopUpJourneyFlag) andThen getData andThen requireData).async {
    implicit request =>
      getRequiredAnswerOrRedirectV(TopUpPeriodsPage) { topUpPeriods =>
        withValidTopUpDate(topUpPeriods, idx) { topUpPeriod =>
          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, topUpPeriod, idx))),
              value => {
                val topUpAmount = TopUpPayment(topUpPeriod.date, value)
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(TopUpAmountPage, topUpAmount, Some(idx)))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(TopUpAmountPage, updatedAnswers, Some(idx)))
              }
            )
        }
      }
  }

  private def withValidTopUpDate(topUpPeriods: Seq[TopUpPeriod], idx: Int)(f: TopUpPeriod => Future[Result]): Future[Result] =
    topUpPeriods.lift(idx - 1) match {
      case Some(date) => f(date)
      case None       => Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
    }
}
