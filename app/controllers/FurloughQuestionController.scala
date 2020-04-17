/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.FurloughQuestionFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage, FurloughQuestionPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import views.html.FurloughQuestionView

import scala.concurrent.{ExecutionContext, Future}

class FurloughQuestionController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: FurloughQuestionFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: FurloughQuestionView
)(implicit ec: ExecutionContext)
    extends BaseController {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val maybeClaimStart = request.userAnswers.get(ClaimPeriodStartPage)
    val maybeClaimEnd = request.userAnswers.get(ClaimPeriodEndPage)
    val maybeFurlough = request.userAnswers.get(FurloughQuestionPage)

    (maybeClaimStart, maybeClaimEnd) match {
      case (Some(start), Some(end)) => Ok(view(maybeFurlough.map(fr => form.fill(fr)).getOrElse(form), start, end, mode))
      case (None, _)                => Redirect(routes.ClaimPeriodStartController.onPageLoad(mode))
      case (_, None)                => Redirect(routes.ClaimPeriodEndController.onPageLoad(mode))
    }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => {
          val maybeClaimStart = request.userAnswers.get(ClaimPeriodStartPage)
          val maybeClaimEnd = request.userAnswers.get(ClaimPeriodEndPage)

          val result = (maybeClaimStart, maybeClaimEnd) match {
            case (Some(start), Some(end)) => BadRequest(view(formWithErrors, start, end, mode))
            case (None, _)                => Redirect(routes.ClaimPeriodStartController.onPageLoad(mode))
            case (_, None)                => Redirect(routes.ClaimPeriodEndController.onPageLoad(mode))
          }
          Future.successful(result)
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(
                               request.userAnswers
                                 .set(FurloughQuestionPage, value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(FurloughQuestionPage, mode, updatedAnswers))
      )
  }
}
