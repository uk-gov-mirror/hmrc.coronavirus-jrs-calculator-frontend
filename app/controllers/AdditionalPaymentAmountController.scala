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
import forms.AdditionalPaymentAmountFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import models.{AdditionalPayment, Amount}
import navigation.Navigator
import pages.{AdditionalPaymentAmountPage, AdditionalPaymentPeriodsPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import services.UserAnswerPersistence
import views.html.AdditionalPaymentAmountView

import scala.concurrent.{ExecutionContext, Future}

class AdditionalPaymentAmountController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: AdditionalPaymentAmountFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AdditionalPaymentAmountView,
)(implicit ec: ExecutionContext, eh: ErrorHandler)
    extends BaseController {

  val form: Form[Amount] = formProvider()
  val feature: FeatureFlagActionProvider = new FeatureFlagActionProviderImpl()
  val userAnswerPersistence = new UserAnswerPersistence(sessionRepository.set)

  def onPageLoad(idx: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswerOrRedirectV(AdditionalPaymentPeriodsPage) { additionalPaymentPeriods =>
      withValidAdditionalPaymentDate(additionalPaymentPeriods, idx) { paymentDate =>
        val preparedForm = request.userAnswers.getV(AdditionalPaymentAmountPage, Some(idx)) match {
          case Invalid(e)   => form
          case Valid(value) => form.fill(value.amount)
        }

        Future.successful(Ok(view(preparedForm, paymentDate, idx)))
      }
    }
  }

  def onSubmit(idx: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswerOrRedirectV(AdditionalPaymentPeriodsPage) { additionalPaymentPeriods =>
      withValidAdditionalPaymentDate(additionalPaymentPeriods, idx) { paymentDate =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, paymentDate, idx))),
            value => {
              val additionalPayment = AdditionalPayment(paymentDate, value)
              userAnswerPersistence
                .persistAnswer(request.userAnswers, AdditionalPaymentAmountPage, additionalPayment, Some(idx))
                .map { updatedAnswers =>
                  Redirect(navigator.nextPage(AdditionalPaymentAmountPage, updatedAnswers, Some(idx)))
                }
            }
          )
      }
    }
  }

  private def withValidAdditionalPaymentDate(topUpPeriods: Seq[LocalDate], idx: Int)(f: LocalDate => Future[Result]): Future[Result] =
    topUpPeriods.lift(idx - 1) match {
      case Some(date) => f(date)
      case None       => Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
    }
}
