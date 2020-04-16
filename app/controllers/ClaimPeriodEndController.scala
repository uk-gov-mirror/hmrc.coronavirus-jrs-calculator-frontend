/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.ClaimPeriodEndFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage}
import play.api.data.FormError
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.ViewUtils._
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

  def form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val maybeClaimStart = request.userAnswers.get(ClaimPeriodStartPage)
    val maybeClaimEnd = request.userAnswers.get(ClaimPeriodEndPage)

    (maybeClaimStart, maybeClaimEnd) match {
      case (Some(_), Some(end)) => Ok(view(form.fill(end), mode))
      case (Some(_), None)      => Ok(view(form, mode))
      case (None, _)            => Redirect(routes.ClaimPeriodStartController.onPageLoad(mode))
    }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
        value => {
          val maybeClaimStart = request.userAnswers.get(ClaimPeriodStartPage)
          maybeClaimStart match {
            case Some(claimStart) if value.isBefore(claimStart) =>
              val errorForm = form
                .fill(value)
                .withError(FormError("value", "claimPeriodEnd.error.before.start", dateToString(claimStart)))
              Future.successful(BadRequest(view(errorForm, mode)))
            case Some(_) =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(ClaimPeriodEndPage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(ClaimPeriodEndPage, mode, updatedAnswers))
            case None => Future.successful(Redirect(routes.ClaimPeriodStartController.onPageLoad(mode)))
          }

        }
      )
  }
}
