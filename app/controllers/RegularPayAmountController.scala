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
import forms.RegularPayAmountFormProvider
import javax.inject.Inject
import models.PaymentFrequency
import navigation.Navigator
import pages.{PaymentFrequencyPage, RegularPayAmountPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.RegularPayAmountView

import scala.concurrent.{ExecutionContext, Future}

class RegularPayAmountController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: RegularPayAmountFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RegularPayAmountView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val maybePf = request.userAnswers.get[PaymentFrequency](PaymentFrequencyPage)
    val maybeSalary = request.userAnswers.get(RegularPayAmountPage)

    (maybePf, maybeSalary) match {
      case (Some(pf), Some(sq)) => Ok(view(form.fill(sq), pf))
      case (Some(pf), None)     => Ok(view(form, pf))
      case (None, _)            => Redirect(routes.PaymentFrequencyController.onPageLoad())
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => {
          val maybePf = request.userAnswers.get[PaymentFrequency](PaymentFrequencyPage)

          val result = maybePf match {
            case Some(pf) => BadRequest(view(formWithErrors, pf))
            case None     => Redirect(routes.PaymentFrequencyController.onPageLoad())
          }
          Future.successful(result)
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(RegularPayAmountPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(RegularPayAmountPage, updatedAnswers))
      )
  }
}
