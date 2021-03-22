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
import forms.NumberOfStatLeaveDaysFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.NumberOfStatLeaveDaysPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.NumberOfStatLeaveDaysView
import scala.concurrent.{ExecutionContext, Future}
import cats.data.Validated.{Invalid, Valid}
import config.FrontendAppConfig
import play.api.data.Form
import viewmodels.BeenOnStatutoryLeaveHelper

class NumberOfStatLeaveDaysController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: NumberOfStatLeaveDaysFormProvider,
  helper: BeenOnStatutoryLeaveHelper,
  val controllerComponents: MessagesControllerComponents,
  view: NumberOfStatLeaveDaysView
)(implicit ec: ExecutionContext, appConfig: FrontendAppConfig)
    extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val form: Form[Int] = formProvider(boundaryStart = helper.boundaryStartDate, boundaryEnd = helper.boundaryEndDate)
    val preparedForm = request.userAnswers.getV(NumberOfStatLeaveDaysPage) match {
      case Invalid(_)   => form
      case Valid(value) => form.fill(value)
    }
    val postAction = controllers.routes.NumberOfStatLeaveDaysController.onSubmit()
    Ok(view(preparedForm, postAction, helper.boundaryStart(), helper.boundaryEnd()))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val postAction      = controllers.routes.NumberOfStatLeaveDaysController.onSubmit()
    val form: Form[Int] = formProvider(helper.boundaryStartDate, helper.boundaryEndDate)
    form
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(
            BadRequest(
              view(form = formWithErrors,
                   postAction = postAction,
                   boundaryStart = helper.boundaryStart(),
                   boundaryEnd = helper.boundaryEnd()))),
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(NumberOfStatLeaveDaysPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(NumberOfStatLeaveDaysPage, updatedAnswers))
      )
  }
}
