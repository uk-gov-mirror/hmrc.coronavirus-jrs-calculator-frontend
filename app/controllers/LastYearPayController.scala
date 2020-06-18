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

import java.time.LocalDate

import cats.data.Validated.{Invalid, Valid}
import controllers.actions._
import forms.LastYearPayFormProvider
import handlers.LastYearPayControllerRequestHandler
import javax.inject.Inject
import models.{Amount, LastYearPayment, PaymentFrequency, UserAnswers}
import navigation.Navigator
import org.slf4j.{Logger, LoggerFactory}
import pages.{LastYearPayPage, PaymentFrequencyPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.LastYearPayView

import scala.concurrent.{ExecutionContext, Future}

class LastYearPayController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: LastYearPayFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: LastYearPayView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with LastYearPayControllerRequestHandler {

  implicit val logger: Logger = LoggerFactory.getLogger(getClass)

  val form: Form[Amount] = formProvider()

  def onPageLoad(idx: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getPayDatesV(request.userAnswers).fold(
      nel => {
        UserAnswers.logErrors(nel)
        Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
      }, { payDates =>
        withValidPayDate(payDates, idx) { date =>
          val preparedForm = request.userAnswers.getV(LastYearPayPage) match {
            case Invalid(e) =>
              UserAnswers.logWarnings(e)
              form
            case Valid(value) => form.fill(value.amount)
          }

          val isMonthlyFrequency = request.userAnswers.getV(PaymentFrequencyPage) match {
            case Valid(PaymentFrequency.Monthly) => true
            case _                               => false
          }
          Future.successful(Ok(view(preparedForm, idx, date, isMonthlyFrequency)))
        }
      }
    )
  }

  def withValidPayDate(payDates: Seq[LocalDate], idx: Int)(f: LocalDate => Future[Result]): Future[Result] =
    payDates.lift(idx - 1) match {
      case Some(date) => f(date)
      case None       => Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
    }

  def onSubmit(idx: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getPayDatesV(request.userAnswers).fold(
      nel => {
        UserAnswers.logErrors(nel)
        Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
      }, { payDates =>
        withValidPayDate(payDates, idx) {
          date =>
            val isMonthlyFrequency = request.userAnswers.getV(PaymentFrequencyPage) match {
              case Valid(PaymentFrequency.Monthly) => true
              case _                               => false
            }

            form
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, idx, date, isMonthlyFrequency))),
                value =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(LastYearPayPage, LastYearPayment(date, value), Some(idx)))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield Redirect(navigator.nextPage(LastYearPayPage, updatedAnswers, Some(idx)))
              )
        }
      }
    )
  }
}
