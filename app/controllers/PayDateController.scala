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
import forms.PayDateFormProvider
import handlers.{DataExtractor, ErrorHandler}
import javax.inject.Inject
import models.PaymentFrequency.Monthly
import models.{PaymentFrequency, UserAnswers}
import navigation.Navigator
import pages.{ClaimPeriodStartPage, FurloughStartDatePage, PayDatePage}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import services.PeriodHelper
import utils.LocalDateHelpers
import views.html.PayDateView

import scala.concurrent.{ExecutionContext, Future}

class PayDateController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PayDateFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PayDateView
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler)
    extends BaseController with I18nSupport with LocalDateHelpers with PeriodHelper with DataExtractor {

  def onPageLoad(idx: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    if (shouldRedirect(request.userAnswers, idx)) {
      Future.successful(Redirect(routes.PayDateController.onPageLoad(1)))
    } else {
      getRequiredAnswersV(ClaimPeriodStartPage, FurloughStartDatePage) { (claimStartDate, furloughStartDate) =>
        val effectiveStartDate = utils.LocalDateHelpers.latestOf(claimStartDate, furloughStartDate)
        messageDateFrom(effectiveStartDate, request.userAnswers, idx).fold {
          Logger.warn(s"onPageLoad messageDateFrom returned none for claimStartDate=$claimStartDate, payDates=${request.userAnswers.getList(
            PayDatePage)}, idx=$idx")
          Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
        } { messageDate =>
          val preparedForm = request.userAnswers.getV(PayDatePage, Some(idx)) match {
            case Invalid(err) =>
              UserAnswers.logWarnings(err)(logger)
              form
            case Valid(value) => form.fill(value)
          }

          Future.successful(Ok(view(preparedForm, idx, messageDate)))
        }
      }
    }
  }

  def form: Form[LocalDate] = formProvider()

  def onSubmit(idx: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswersV(ClaimPeriodStartPage, FurloughStartDatePage) { (claimStartDate, furloughStartDate) =>
      val effectiveStartDate = utils.LocalDateHelpers.latestOf(claimStartDate, furloughStartDate)

      val messageDate = messageDateFrom(effectiveStartDate, request.userAnswers, idx)
      val dayBeforeClaimStart = effectiveStartDate.minusDays(1)
      val latestDate = request.userAnswers
        .getList(PayDatePage)
        .lift(idx - 2)
        .map(
          lastDate => latestOf(lastDate, dayBeforeClaimStart)
        )
        .getOrElse(dayBeforeClaimStart)

      formProvider(
        beforeDate = if (idx == 1) Some(effectiveStartDate) else None,
        afterDate = if (idx != 1) Some(latestDate) else None
      ).bindFromRequest()
        .fold(
          formWithErrors => {
            messageDate.fold {
              Logger.warn(s"onSubmit messageDateFrom returned none for claimStartDate=$claimStartDate, payDates=${request.userAnswers
                .getList(PayDatePage)}, idx=$idx")
              Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
            } { messageDate =>
              Future.successful(BadRequest(view(formWithErrors, idx, messageDate)))
            }
          },
          value => saveAndRedirect(request.userAnswers, value, idx)
        )
    }
  }

  private[this] def messageDateFrom(claimStartDate: LocalDate, userAnswers: UserAnswers, idx: Int): Option[LocalDate] =
    if (idx == 1) {
      Some(claimStartDate)
    } else {
      userAnswers.getList(PayDatePage).lift.apply(idx - 2)
    }

  private[this] def saveAndRedirect(userAnswers: UserAnswers, payDate: LocalDate, idx: Int): Future[Result] =
    shouldGenerate(userAnswers, idx) match {
      case (true, freq) =>
        extractFurloughWithinClaimV(userAnswers) match {
          case Valid(furlough) => {
            val endDates = generateEndDates(freq, payDate, furlough)
            for {
              updatedAnswers <- Future.fromTry(userAnswers.setList(PayDatePage, endDates))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(PayDatePage, updatedAnswers, Some(idx)))
          }
          case Invalid(_) =>
            Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
        }
      case (false, _) =>
        for {
          updatedAnswers <- Future.fromTry(userAnswers.setListItemWithInvalidation(PayDatePage, payDate, idx))
          _              <- sessionRepository.set(updatedAnswers)
        } yield Redirect(navigator.nextPage(PayDatePage, updatedAnswers, Some(idx)))
    }

  private[this] def shouldGenerate(userAnswers: UserAnswers, idx: Int): (Boolean, PaymentFrequency) =
    if (idx != 1) {
      false -> Monthly
    } else {
      extractPaymentFrequencyV(userAnswers) match {
        case Valid(Monthly) => false -> Monthly
        case Valid(freq)    => true  -> freq
        case Invalid(_)     => false -> Monthly
      }
    }

  private[this] def shouldRedirect(userAnswers: UserAnswers, idx: Int): Boolean =
    extractPaymentFrequencyV(userAnswers) match {
      case Valid(Monthly)       => false
      case Valid(_) if idx != 1 => true
      case _                    => false
    }

}
