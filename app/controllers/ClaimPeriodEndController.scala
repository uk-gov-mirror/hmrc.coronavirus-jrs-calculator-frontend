/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.LocalDate
import controllers.actions._
import forms.ClaimPeriodEndFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.ClaimPeriodEndView

import scala.concurrent.{ExecutionContext, Future}

class ClaimPeriodEndController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ClaimPeriodEndFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ClaimPeriodEndView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  def form(claimStart: LocalDate) = formProvider(claimStart)

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val maybeClaimStart = request.userAnswers.get(ClaimPeriodStartPage)
    val maybeClaimEnd = request.userAnswers.get(ClaimPeriodEndPage)

    (maybeClaimStart, maybeClaimEnd) match {
      case (Some(claimStart), Some(end)) => Ok(view(form(claimStart).fill(end), mode))
      case (Some(claimStart), None)      => Ok(view(form(claimStart), mode))
      case (None, _)                     => Redirect(routes.ClaimPeriodStartController.onPageLoad(mode))
    }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.userAnswers.get(ClaimPeriodStartPage) match {
      case Some(claimStart) =>
        form(claimStart)
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
            value => {
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(ClaimPeriodEndPage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(ClaimPeriodEndPage, mode, updatedAnswers))
            }
          )
      case None => Future.successful(Redirect(routes.ClaimPeriodStartController.onPageLoad(mode)))
    }
  }
}
