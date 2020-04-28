/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.LocalDate

import controllers.actions._
import forms.PayDateFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import models.UserAnswers
import navigation.Navigator
import pages.{ClaimPeriodStartPage, FurloughStartDatePage, PayDatePage}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import utils.LocalDateHelpers
import views.html.PayDateView

import scala.concurrent.{ExecutionContext, Future}

class PayDateController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PayDateFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PayDateView
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler)
    extends BaseController with I18nSupport with LocalDateHelpers {

  def onPageLoad(idx: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswers(ClaimPeriodStartPage, FurloughStartDatePage) { (claimStartDate, furloughStartDate) =>
      val effectiveStartDate = utils.LocalDateHelpers.latestOf(claimStartDate, furloughStartDate)
      messageDateFrom(effectiveStartDate, request.userAnswers, idx).fold {
        Logger.warn(s"onPageLoad messageDateFrom returned none for claimStartDate=$claimStartDate, payDates=${request.userAnswers.getList(
          PayDatePage)}, idx=$idx")
        Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
      } { messageDate =>
        val preparedForm = request.userAnswers.get(PayDatePage, Some(idx)) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Future.successful(Ok(view(preparedForm, idx, messageDate)))
      }
    }
  }

  def form = formProvider()

  def onSubmit(idx: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswers(ClaimPeriodStartPage, FurloughStartDatePage) { (claimStartDate, furloughStartDate) =>
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
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.setListWithInvalidation(PayDatePage, value, idx))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(PayDatePage, updatedAnswers, Some(idx)))
        )
    }
  }

  private def messageDateFrom(claimStartDate: LocalDate, userAnswers: UserAnswers, idx: Int): Option[LocalDate] =
    if (idx == 1) {
      Some(claimStartDate)
    } else {
      userAnswers.getList(PayDatePage).lift.apply(idx - 2)
    }
}
