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
import models.{NormalMode, UserAnswers}
import navigation.Navigator
import pages.{ClaimPeriodStartPage, PayDatePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
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
    extends BaseController with I18nSupport {

  def onPageLoad(idx: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswer(ClaimPeriodStartPage) { claimStartDate =>
      val messageDate = messageDateFrom(claimStartDate, request.userAnswers, idx)

      val preparedForm = request.userAnswers.get(PayDatePage, Some(idx)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Future.successful(Ok(view(preparedForm, idx, messageDate)))
    }
  }

  private def messageDateFrom(claimStartDate: LocalDate, userAnswers: UserAnswers, idx: Int): LocalDate =
    if (idx == 1) {
      claimStartDate
    } else {
      userAnswers.getList(PayDatePage).apply(idx - 2)
    }

  private def latestOf(a: LocalDate, b: LocalDate): LocalDate = if (a.isAfter(b)) a else b

  def form = formProvider()

  def onSubmit(idx: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswer(ClaimPeriodStartPage) { claimStartDate =>
      val messageDate = messageDateFrom(claimStartDate, request.userAnswers, idx)
      val dayBeforeClaimStart = claimStartDate.minusDays(1)
      val latestDate = request.userAnswers
        .getList(PayDatePage)
        .lift(idx - 2)
        .map(
          lastDate => latestOf(lastDate, dayBeforeClaimStart)
        )
        .getOrElse(dayBeforeClaimStart)

      formProvider(
        beforeDate = if (idx == 1) Some(claimStartDate) else None,
        afterDate = if (idx != 1) Some(latestDate) else None
      ).bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, idx, messageDate))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(PayDatePage, value, Some(idx)))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(PayDatePage, NormalMode, updatedAnswers, Some(idx)))
        )
    }
  }
}
