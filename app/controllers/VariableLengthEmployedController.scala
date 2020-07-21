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
import forms.VariableLengthEmployedFormProvider
import javax.inject.Inject
import models.EmployeeStarted
import navigation.Navigator
import org.slf4j.{Logger, LoggerFactory}
import pages.EmployeeStartedPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import views.html.VariableLengthEmployedView

import scala.concurrent.{ExecutionContext, Future}

class VariableLengthEmployedController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  override val navigator: Navigator,
  identify: IdentifierAction,
  feature: FeatureFlagActionProvider,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: VariableLengthEmployedFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: VariableLengthEmployedView
)(implicit ec: ExecutionContext)
    extends BaseController {

  override implicit val logger: Logger = LoggerFactory.getLogger(getClass)

  val form: Form[EmployeeStarted] = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.getV(EmployeeStartedPage) match {
      case Invalid(e)   => form
      case Valid(value) => form.fill(value)
    }

    Ok(view(preparedForm))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(EmployeeStartedPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(EmployeeStartedPage, updatedAnswers))
      )
  }
}
