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

import java.time.LocalDate

import cats.data.Validated.{Invalid, Valid}
import config.FrontendAppConfig
import controllers.actions._
import forms.FirstFurloughDateFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import models.requests.DataRequest
import navigation.Navigator
import pages.{FirstFurloughDatePage, FurloughStartDatePage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import play.api.data.Form
import services.UserAnswerPersistence
import utils.EmployeeTypeUtil
import utils.LocalDateHelpers.{mar1st2020, may1st2021, nov1st2020}
import views.html.FirstFurloughDateView

import scala.concurrent.{ExecutionContext, Future}

class FirstFurloughDateController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: FirstFurloughDateFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: FirstFurloughDateView
)(implicit ec: ExecutionContext, appConfig: FrontendAppConfig, errorHandler: ErrorHandler)
    extends BaseController with I18nSupport with EmployeeTypeUtil {

  def form(startDate: LocalDate)(implicit messages: Messages): Form[LocalDate] = formProvider(startDate)

  protected val userAnswerPersistence = new UserAnswerPersistence(sessionRepository.set)

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswerV(FurloughStartDatePage) { startDate =>
      val preparedForm = request.userAnswers.getV(FirstFurloughDatePage) match {
        case Invalid(_)   => form(startDate)
        case Valid(value) => form(startDate).fill(value)
      }
      Future(renderPage(Ok, preparedForm))
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswerV(FurloughStartDatePage) { startDate =>
      form(startDate)
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(renderPage(BadRequest, formWithErrors)),
          value =>
            userAnswerPersistence
              .persistAnswer(request.userAnswers, FirstFurloughDatePage, value, None)
              .map { updatedAnswers =>
                Redirect(navigator.nextPage(FirstFurloughDatePage, updatedAnswers, None))
            }
        )
    }
  }

  private def renderPage(successfulCallStatus: Status, preparedForm: Form[LocalDate])(implicit request: DataRequest[_]): Result =
    variablePayResolver[LocalDate](
      type3EmployeeResult = Some(mar1st2020),
      type5aEmployeeResult = Some(nov1st2020),
      type5bEmployeeResult = Some(may1st2021)).fold(InternalServerError(errorHandler.internalServerErrorTemplate(request)))(
      dateToShow => successfulCallStatus(view(preparedForm, dateToShow))
    )

}
