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
import controllers.actions._
import forms.FirstFurloughDateFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import models.UserAnswers
import navigation.Navigator
import pages.{FirstFurloughDatePage, FurloughStartDatePage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import play.api.data.Form
import services.UserAnswerPersistence
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
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler)
    extends BaseController with I18nSupport {

  def form(startDate: LocalDate)(implicit messages: Messages): Form[LocalDate] = formProvider(startDate)
  protected val userAnswerPersistence = new UserAnswerPersistence(sessionRepository.set)

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswerV(FurloughStartDatePage) { startDate =>
      val preparedForm = request.userAnswers.getV(FirstFurloughDatePage) match {
        case Invalid(_)   => form(startDate)
        case Valid(value) => form(startDate).fill(value)
      }
      Future(Ok(view(preparedForm)))
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswerV(FurloughStartDatePage) { startDate =>
      form(startDate)
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
          value =>
            userAnswerPersistence
              .persistAnswer(request.userAnswers, FirstFurloughDatePage, value, None)
              .map { updatedAnswers =>
                Redirect(navigator.nextPage(FirstFurloughDatePage, updatedAnswers, None))
            }
        )
    }
  }

}
