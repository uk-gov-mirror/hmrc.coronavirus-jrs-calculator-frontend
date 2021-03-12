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
import config.FrontendAppConfig
import controllers.actions._
import forms.RegularPayAmountFormProvider
import handlers.ErrorHandler
import models.Salary
import models.requests.DataRequest
import navigation.Navigator
import pages.RegularPayAmountPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.EmployeeTypeUtil
import utils.LocalDateHelpers.{mar19th2020, mar2nd2021, oct30th2020}
import views.ViewUtils.dateToString
import views.html.RegularPayAmountView

import javax.inject.Inject
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
  view: RegularPayAmountView)(implicit ec: ExecutionContext, appconfig: FrontendAppConfig, errorHandler: ErrorHandler)
    extends FrontendBaseController with I18nSupport with EmployeeTypeUtil {

  val form: Form[Salary] = formProvider()

  def onPageLoad(): Action[AnyContent] =
    (identify andThen getData andThen requireData) { implicit request =>
      val maybeSalary = request.userAnswers.getV(RegularPayAmountPage)

      val preparedForm = maybeSalary match {
        case Valid(sq)  => form.fill(sq)
        case Invalid(e) => form
      }

      val postAction = controllers.routes.RegularPayAmountController.onSubmit()

      Ok(view(preparedForm, postAction, cutoffDateResolver))
    }

  def onSubmit(): Action[AnyContent] = {
    val postAction = controllers.routes.RegularPayAmountController.onSubmit()
    (identify andThen getData andThen requireData).async { implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => {
            Future.successful(BadRequest(view(formWithErrors, postAction, cutoffDateResolver)))
          },
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(RegularPayAmountPage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(RegularPayAmountPage, updatedAnswers))
        )
    }
  }

  def cutoffDateResolver()(implicit request: DataRequest[_]): String =
    regularPayResolver[String](
      type1EmployeeResult = Some(dateToString(mar19th2020)),
      type2aEmployeeResult = Some(dateToString(oct30th2020)),
      type2bEmployeeResult = Some(dateToString(mar2nd2021))
    ).fold(
      throw new InternalServerException("[RegularPayAmountController][cutoffResolver] result could not be resolved")
    )(identity)
}
