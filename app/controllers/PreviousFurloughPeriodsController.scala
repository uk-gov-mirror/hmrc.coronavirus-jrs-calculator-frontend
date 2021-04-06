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

import java.time.{LocalDate, Month}
import cats.data.Validated.{Invalid, Valid}
import config.FrontendAppConfig
import config.featureSwitch.{ExtensionTwoNewStarterFlow, FeatureSwitching}
import controllers.actions._
import forms.PreviousFurloughPeriodsFormProvider
import handlers.ErrorHandler

import javax.inject.Inject
import models.EmployeeStarted.{After1Feb2019, OnOrBefore1Feb2019}
import models.Mode
import models.requests.DataRequest
import navigation.Navigator
import pages.{EmployeeStartedPage, FurloughStartDatePage, OnPayrollBefore30thOct2020Page, PreviousFurloughPeriodsPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.EmployeeTypeUtil
import utils.LocalDateHelpers._
import views.html.PreviousFurloughPeriodsView

import scala.concurrent.{ExecutionContext, Future}

class PreviousFurloughPeriodsController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PreviousFurloughPeriodsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PreviousFurloughPeriodsView
)(implicit ec: ExecutionContext, appConfig: FrontendAppConfig, errorHandler: ErrorHandler)
    extends FrontendBaseController with I18nSupport with FeatureSwitching with EmployeeTypeUtil {

  def form()(implicit request: DataRequest[_]): Form[Boolean] =
    formProvider(
      employeeTypeResolver[LocalDate](type5aEmployeeResult = Some(nov1st2020),
                                      type5bEmployeeResult = Some(may1st2021),
                                      defaultResult = mar1st2020))

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.getV(PreviousFurloughPeriodsPage) match {
      case Invalid(_)   => form
      case Valid(value) => form.fill(value)
    }
    renderPage(Ok, preparedForm)
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(renderPage(BadRequest, formWithErrors)),
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(PreviousFurloughPeriodsPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(PreviousFurloughPeriodsPage, updatedAnswers))
      )
  }

  private def renderPage(successfulCallStatus: Status, preparedForm: Form[Boolean])(implicit request: DataRequest[_]): Result =
    variablePayResolver[LocalDate](type3EmployeeResult = Some(mar1st2020),
                                   type5aEmployeeResult = Some(nov1st2020),
                                   type5bEmployeeResult = Some(may1st2021)).fold(
      InternalServerError(errorHandler.internalServerErrorTemplate(request))
    )(
      dateToShow => successfulCallStatus(view(preparedForm, dateToShow))
    )
}
