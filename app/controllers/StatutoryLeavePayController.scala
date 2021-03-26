/*
 * Copyright 2021 HM Revenue & Customs
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
import forms.StatutoryLeavePayFormProvider

import javax.inject.Inject
import navigation.Navigator
import pages.{AnnualPayAmountPage, StatutoryLeavePayPage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.StatutoryLeavePayView

import scala.concurrent.{ExecutionContext, Future}
import cats.data.Validated.{Invalid, Valid}
import handlers.ErrorHandler
import models.Amount
import play.api.data.Form

class StatutoryLeavePayController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: StatutoryLeavePayFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: StatutoryLeavePayView
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler)
    extends BaseController with I18nSupport {

  def form(referencePay: BigDecimal)(implicit messages: Messages): Form[Amount] =
    formProvider(referencePay)

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswerV(AnnualPayAmountPage) { annualPayAmount =>
      {
        val preparedForm = request.userAnswers.getV(StatutoryLeavePayPage) match {
          case Invalid(e)   => form(annualPayAmount.amount)
          case Valid(value) => form(annualPayAmount.amount).fill(value)
        }

        Future(Ok(view(preparedForm)))
      }
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswerV(AnnualPayAmountPage) { annualPayAmount =>
      {
        form(annualPayAmount.amount)
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(StatutoryLeavePayPage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(StatutoryLeavePayPage, updatedAnswers))
          )
      }
    }
  }
}
