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

import cats.data.Validated.{Invalid, Valid}
import controllers.actions._
import forms.PaymentFrequencyFormProvider
import javax.inject.Inject
import models.PaymentFrequency
import navigation.Navigator
import pages.PaymentFrequencyPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.PaymentFrequencyView

import scala.concurrent.{ExecutionContext, Future}

class PaymentFrequencyController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PaymentFrequencyFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PaymentFrequencyView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form: Form[PaymentFrequency] = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.getV(PaymentFrequencyPage) match {
      case Invalid(_)   => form
      case Valid(value) => form.fill(value)
    }

    val radioOptions: Seq[RadioItem] = PaymentFrequency.options(form = form)

    Ok(
      view(
        form = preparedForm,
        postAction = controllers.routes.PaymentFrequencyController.onSubmit(),
        radioItems = radioOptions
      )
    )
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val radioOptions: Seq[RadioItem] = PaymentFrequency.options(form = form)
    form
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(
            BadRequest(
              view(
                form = formWithErrors,
                postAction = controllers.routes.PaymentFrequencyController.onSubmit(),
                radioItems = radioOptions
              )
            )),
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(PaymentFrequencyPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(PaymentFrequencyPage, updatedAnswers))
      )
  }
}
