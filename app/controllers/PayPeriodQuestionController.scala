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
import forms.PayPeriodQuestionFormProvider
import handlers.{ErrorHandler, FastJourneyUserAnswersHandler}
import javax.inject.Inject
import models.PayPeriodQuestion
import models.requests.DataRequest
import navigation.Navigator
import org.slf4j.{Logger, LoggerFactory}
import pages.{PayDatePage, PayPeriodQuestionPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import views.html.PayPeriodQuestionView

import scala.concurrent.{ExecutionContext, Future}

class PayPeriodQuestionController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PayPeriodQuestionFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PayPeriodQuestionView
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler)
    extends BaseController with I18nSupport with FastJourneyUserAnswersHandler {

  override val logger: Logger = LoggerFactory.getLogger(getClass)
  val form: Form[PayPeriodQuestion] = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.getV(PayPeriodQuestionPage) match {
      case Invalid(e)   => form
      case Valid(value) => form.fill(value)
    }

    previousPageOrRedirect(Ok(view(preparedForm, generatePeriods(request.userAnswers.getList(PayDatePage)))))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, generatePeriods(request.userAnswers.getList(PayDatePage))))),
        value => processSubmittedAnswer(request, value)
      )
  }

  private def processSubmittedAnswer(request: DataRequest[AnyContent], value: PayPeriodQuestion): Future[Result] =
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(PayPeriodQuestionPage, value))
      _              <- sessionRepository.set(updatedAnswers)
      call = navigator.nextPage(PayPeriodQuestionPage, updatedAnswers)
      updatedJourney <- {
        payQuestion(updatedAnswers) match {
          case Valid(updatedJourney) =>
            sessionRepository.set(updatedJourney.updated).map { _ =>
              Redirect(call)
            }
          case Invalid(_) =>
            Future.successful {
              InternalServerError(errorHandler.internalServerErrorTemplate(request))
            }
        }
      }
    } yield updatedJourney
}
